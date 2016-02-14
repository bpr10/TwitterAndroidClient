package git.bpr10.trendingtweets.views.activities;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import git.bpr10.trendingtweets.R;
import git.bpr10.trendingtweets.config.NetworkCallback;
import git.bpr10.trendingtweets.config.NetworkManager;
import git.bpr10.trendingtweets.model.SearchResult;
import git.bpr10.trendingtweets.model.TweetModel;
import git.bpr10.trendingtweets.utils.Constants.ERROR_CODES;
import git.bpr10.trendingtweets.utils.Constants;
import git.bpr10.trendingtweets.utils.JsonUtils;
import git.bpr10.trendingtweets.utils.MyUtilities;
import git.bpr10.trendingtweets.views.TweetsAdapter;

/**
 * Created by Bedprakash on 2/14/2016.
 */

public class SearchFragment extends Fragment implements OnClickListener,
        TextWatcher {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    private static final float MINIMUM = 25;
    private static final int REFRESH_INTERVAL = 10 * 1000;

    private View mRootError, mTxtRetry;
    private TextView mTxtErrorMsg, mTxtNoResult, mTxtErrorTitle;

    private EditText mEdtSearch;
    private ImageView mImgCross;
    private ImageView mImgError;
    private ImageView mImgNewFeeds;
    private ImageView mImgSearch;
    private View rootView;

    private HomeActivity mActivity;

    private LinearLayoutManager mRecyclerViewManager;
    private RecyclerView mListSearch;
    private TweetsAdapter mSearchAdapter;
    private ArrayList<TweetModel> mSearchDataList;

    private Handler mHandler;

    private String mCurQueryString;
    private String mNextPageUrl;
    private String mRefreshUrl;

    private boolean mIsNewSearch;
    private boolean mIsSearchLoading;
    private boolean mIsBubbleVisible;
    private int mFeedScrollDist;
    private boolean mIsMoreDataAvailable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomeActivity) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(refreshTask);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isValid(getSearchedQuery())) {
            mHandler.postDelayed(refreshTask, REFRESH_INTERVAL);
        }
    }

    private boolean isValid(String searchedQuery) {
        if (searchedQuery.trim().length() < 2) {
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView != null)
            return rootView;

        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mImgCross = (ImageView) rootView.findViewById(R.id.img_cross);
        mImgCross.setOnClickListener(this);
        ImageView mImgBackBtn = (ImageView) rootView.findViewById(R.id.img_back_btn);
        mImgBackBtn.setOnClickListener(this);
        mEdtSearch = (EditText) rootView.findViewById(R.id.edt_search);
        mEdtSearch.addTextChangedListener(this);
        mImgSearch = (ImageView) rootView.findViewById(R.id.img_search);

        mImgNewFeeds = (ImageView) rootView.findViewById(R.id.img_new_feeds);

        mImgNewFeeds.setOnClickListener(this);

        mRootError = rootView.findViewById(R.id.root_error);
        findErrorViews(rootView);
        MyUtilities.showKeyboard(mActivity, mEdtSearch);
        mSearchDataList = new ArrayList<>();
        mSearchAdapter = new TweetsAdapter(mActivity, mSearchDataList);

        ImageView mLoaderView = new ImageView(mActivity);
        AnimationDrawable mLoaderDrawable = (AnimationDrawable) getResources().getDrawable(
                R.drawable.loader_general);
        mLoaderView.setImageDrawable(mLoaderDrawable);

        // calling addFooterView() once before setAdapter
        // it is must for below kitkat devices
        mListSearch = (RecyclerView) rootView.findViewById(R.id.list_search);
        mRecyclerViewManager = new LinearLayoutManager(mActivity);
        mRecyclerViewManager.setOrientation(LinearLayoutManager.VERTICAL);

        mListSearch.setHasFixedSize(true);
        mListSearch.setLayoutManager(mRecyclerViewManager);
        mListSearch.setAdapter(mSearchAdapter);
        mListSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = mRecyclerViewManager.getChildCount();
                int totalItemCount = mRecyclerViewManager.getItemCount();
                int firstVisibleItem = mRecyclerViewManager
                        .findFirstVisibleItemPosition();
                if (MyUtilities.isNearEnd(firstVisibleItem, visibleItemCount,
                        totalItemCount)
                        && !mIsSearchLoading
                        && mIsMoreDataAvailable()) {
                    getNextPage();
                }

                if (firstVisibleItem == 0) {
                    showNewFeedsText(false);
                }

                // code for hiding the bubble on Scroll
                if (mIsBubbleVisible && mFeedScrollDist > MINIMUM) {
                    // moving up
                    mImgNewFeeds.animate().translationY(-mImgNewFeeds.getHeight() - getResources().getDimension(R.dimen.activity_horizontal_margin)).setInterpolator(new AccelerateInterpolator(2)).start();
                    mFeedScrollDist = 0;
                    mIsBubbleVisible = false;
                } else if (!mIsBubbleVisible && mFeedScrollDist < -MINIMUM) {
                    // moving down
                    mImgNewFeeds.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    mFeedScrollDist = 0;
                    mIsBubbleVisible = true;
                }

                if ((mIsBubbleVisible && dy > 0) || (!mIsBubbleVisible && dy < 0)) {
                    mFeedScrollDist += dy;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                MyUtilities.hideKeyboard(mEdtSearch, mActivity);
            }
        });
        mHandler = new Handler();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cross: {
                mEdtSearch.getText().clear();
                mSearchDataList.clear();
                mSearchAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.txt_retry: {
                search(mCurQueryString, 0);
                break;
            }
            case R.id.img_back_btn: {
                mActivity.onBackPressed();
                break;
            }
            case R.id.img_new_feeds: {
                mListSearch.smoothScrollToPosition(0);
                showNewFeedsText(false);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String query = s.toString().trim();
        if (query.isEmpty()) {
            mEdtSearch.requestFocus();
            mImgCross.setSelected(false);
            mImgSearch.setSelected(false);
            mSearchDataList.clear();
            mCurQueryString = "";
            mSearchAdapter.notifyDataSetChanged();
            mHandler.removeCallbacks(refreshTask);
            mSearchAdapter.setLoaderState(false);
            return;
        }
        if (query.length() < 2 || query.equals(mCurQueryString)) {
            return;
        }
        mIsMoreDataAvailable = true;
        mImgSearch.setSelected(true);
        mImgCross.setSelected(true);
        mImgCross.setImageResource(R.drawable.btn_close_active);
        mCurQueryString = s.toString().trim();
        mIsNewSearch = true;
        mRefreshUrl = null;
        mNextPageUrl = null;
        hideError();
        search(mCurQueryString, 500);

        mHandler.removeCallbacks(refreshTask);
        mHandler.postDelayed(refreshTask, REFRESH_INTERVAL);
    }

    public void search(final String searchString, int delay) {
        if (searchString != null && searchString.isEmpty()) {
            return;
        }
        if (!mIsSearchLoading && mIsMoreDataAvailable()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mCurQueryString.equals(searchString)) {
                        return;
                    }
                    mSearchDataList.clear();
                    mSearchAdapter.notifyDataSetChanged();
                    mIsSearchLoading = true;
                    mSearchAdapter.setLoaderState(true);
                    StringBuilder url = new StringBuilder(Constants.TWITTER_API_BASE_URL);
                    url.append("?q=" + Uri.encode(mCurQueryString.trim()));

                    NetworkManager.searchTweet(mActivity, url.toString(), mSearchrequestListener);

                }
            }, delay);
        }
    }


    private void getNextPage() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty()) {
            return;
        }
        if (!mIsSearchLoading && mIsMoreDataAvailable()) {
            mIsSearchLoading = true;
            mSearchAdapter.setLoaderState(true);
            StringBuilder url = new StringBuilder(Constants.TWITTER_API_BASE_URL);
            url.append(mNextPageUrl);

            NetworkManager.searchTweet(mActivity, url.toString(), mSearchrequestListener);
        }
    }

    private void refreshSearch() {
        StringBuilder url = new StringBuilder(Constants.TWITTER_API_BASE_URL);
        url.append(mRefreshUrl);

        NetworkManager.searchTweet(mActivity, url.toString(), new NetworkCallback() {
            @Override
            public void onResponseError(int error_code, String errorMsg) {

            }

            @Override
            public void onResponseSuccess(String response) {
                if (!isAdded()) {
                    return;
                }
                final SearchResult newPage = JsonUtils.objectify(response, SearchResult.class);
                if (newPage == null) {
                    return;
                }

                if (!(Uri.decode(newPage.getMetadata().getQuery()).equals(mCurQueryString))) {
                    return;
                }
                mRefreshUrl = newPage.getMetadata().getRefreshUrl();
                // set new list of post objects
                hideError();
                try {
                    if (!newPage.getStatuses().isEmpty()) {
                        int itemsAdded = MyUtilities.append(mSearchDataList, newPage.getStatuses(), 0);
                        mSearchAdapter.notifyItemRangeInserted(0, itemsAdded);
                        showNewFeedsText(true);
                    }
                } catch (Exception e) {
                    mSearchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private boolean mIsMoreDataAvailable() {
        return mIsMoreDataAvailable;
    }

    private NetworkCallback mSearchrequestListener = new NetworkCallback() {

        @Override
        public void onResponseError(final int error_code, final String errorMsg) {
            if (!isAdded()) {
                return;
            }
            mIsSearchLoading = false;
            mSearchAdapter.setLoaderState(false);
            handleApiError(error_code, errorMsg);

        }

        @Override
        public void onResponseSuccess(String response) {
            if (!isAdded()) {
                return;
            }
            mIsSearchLoading = false;

            final SearchResult newPage = JsonUtils.objectify(response, SearchResult.class);
            if (newPage == null) {
                onResponseError(Constants.ERROR_CODES.SHIT_HAPPENED, "Could not parse");
                return;
            }

            if (!(Uri.decode(newPage.getMetadata().getQuery().replace("+", " ")).equals(mCurQueryString))) {
                search(mCurQueryString, 500);
                return;
            }
            mNextPageUrl = newPage.getMetadata().getNextResults();
            // set new list of post objects
            hideError();
            if (mIsNewSearch) {
                mSearchAdapter.setLoaderState(false);
                mIsNewSearch = false;
                mSearchDataList.clear();
                mRefreshUrl = newPage.getMetadata().getRefreshUrl();
            }
            if (newPage.getStatuses().isEmpty()) {
                mIsMoreDataAvailable = false;
            } else {
                int startIndex = mSearchDataList.size();
                int itemsAdded = MyUtilities.append(mSearchDataList, newPage.getStatuses());
                if (startIndex == 0) {
                    mSearchAdapter.notifyDataSetChanged();
                } else {
                    try {
                        mSearchAdapter.notifyItemRangeInserted(startIndex, itemsAdded);
                    } catch (Exception e) {
                        mSearchAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (mSearchDataList.size() == 0) {
                showError(ERROR_CODES.EMPTY_RESULTS);
            }

        }
    };

    private void showError(int errorCode) {
        mRootError.setVisibility(View.VISIBLE);
        mListSearch.setVisibility(View.GONE);
        switch (errorCode) {
            case Constants.ERROR_CODES.NO_INTERNET: {
                mTxtRetry.setVisibility(View.VISIBLE);
                mImgError.setImageResource(R.drawable.search_no_internet);
                mTxtErrorMsg
                        .setText(Html
                                .fromHtml("<B>Couldn't connect to the internet!</B><Br>Please check your internet & retry"));
                mTxtNoResult.setVisibility(View.GONE);
                mTxtErrorTitle.setVisibility(View.GONE);
                mTxtErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.ERROR_CODES.EMPTY_RESULTS: {
                mTxtRetry.setVisibility(View.GONE);
                mImgError.setImageResource(R.drawable.no_result_cat);
                mTxtErrorTitle.setText("Sorry!");
                mTxtNoResult.setText("No results found!");
                mTxtNoResult.setVisibility(View.VISIBLE);
                mTxtErrorTitle.setVisibility(View.VISIBLE);
                mTxtErrorMsg.setVisibility(View.GONE);
                break;
            }
            default: {
                mTxtRetry.setVisibility(View.VISIBLE);
                mImgError.setImageResource(R.drawable.ic_apifailure);
                mTxtErrorMsg
                        .setText(Html
                                .fromHtml("<B>Oops!</B><Br>Server error! Please try again after some time"));
                mTxtNoResult.setVisibility(View.GONE);
                mTxtErrorTitle.setVisibility(View.GONE);
                mTxtErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void hideError() {
        if (mListSearch.getVisibility() != View.VISIBLE) {
            mListSearch.setVisibility(View.VISIBLE);
        }
        mRootError.setVisibility(View.GONE);
    }

    private void findErrorViews(View rootView) {
        mRootError = rootView.findViewById(R.id.root_error);
        mImgError = (ImageView) rootView.findViewById(R.id.iv_err_icon);
        mTxtErrorMsg = (TextView) rootView.findViewById(R.id.txt_error_msg);
        mTxtNoResult = (TextView) rootView.findViewById(R.id.txt_no_result_msg);
        mTxtErrorTitle = (TextView) rootView
                .findViewById(R.id.txt_no_result_title);
        mTxtRetry = rootView.findViewById(R.id.txt_retry);
        mTxtRetry.setOnClickListener(this);
    }

    private void handleApiError(final int errorCode, String message) {
        if (mSearchDataList.size() != 0) {
            return;
        }
        hideError();
        switch (errorCode) {
            case ERROR_CODES.NO_INTERNET: {
                if (mSearchDataList.isEmpty()) {
                    showError(ERROR_CODES.NO_INTERNET);
                } else {
                    NetworkManager.showNetworkNotAvailableToast(mActivity);
                }
                break;
            }
            default: {
                showError(ERROR_CODES.SHIT_HAPPENED);
            }
        }
    }

    public String getSearchedQuery() {
        return mEdtSearch.getText().toString();
    }

    private Runnable refreshTask = new Runnable() {
        public void run() {
            //do something
            if (mRefreshUrl == null)
                return;
            refreshSearch();
            mHandler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    private void showNewFeedsText(boolean pVisibility) {
        if (pVisibility) {
            if (mImgNewFeeds.getVisibility() == View.VISIBLE) {
                return;
            }
            mIsBubbleVisible = true;
            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.simple_grow);
            mImgNewFeeds.setVisibility(View.VISIBLE);
            mImgNewFeeds.startAnimation(animation);
        } else {
            if (mImgNewFeeds.getVisibility() != View.VISIBLE) {
                return;
            }
            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.simple_grow_rev);
            mImgNewFeeds.startAnimation(animation);
            mImgNewFeeds.setVisibility(View.GONE);
        }
    }
}

