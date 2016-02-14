package git.bpr10.trendingtweets.views;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewGroup.LayoutParams;

import android.widget.ImageView;

import java.util.ArrayList;

import git.bpr10.trendingtweets.R;
import git.bpr10.trendingtweets.config.AppConfig;
import git.bpr10.trendingtweets.model.TweetModel;
import git.bpr10.trendingtweets.views.activities.HomeActivity;
import git.bpr10.trendingtweets.views.viewholders.ViewHolderTweet;

/**
 * Created by Bedprakash on 2/12/2016.
 */
public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = TweetsAdapter.class.getSimpleName();
    private ArrayList<TweetModel> mDataList;
    private HomeActivity mHomeActivity;

    private boolean mShowLoader;
    protected ImageView mImgLoader;
    private AnimationDrawable mAnimLoader;

    /**
     * @param pHomeActivity
     * @param pDataList
     */
    public TweetsAdapter(HomeActivity pHomeActivity, ArrayList<TweetModel> pDataList) {
        mDataList = pDataList;
        this.mHomeActivity = pHomeActivity;
    }


    private interface ViewType {
        int LOADER = 0;
        int TWEET = 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDataList.size()) {
            return ViewType.LOADER;
        }
        return ViewType.TWEET;
    }


    @Override
    public final int getItemCount() {
        if (mShowLoader) {
            return mDataList.size() + 1;
        } else {
            return mDataList.size();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViewType.TWEET) {
            View view = LayoutInflater.from(mHomeActivity).inflate(R.layout.item_list_tweet, parent, false);
            return new ViewHolderTweet(mHomeActivity, view);
        } else {
            return new ViewHolderLoader(new ImageView(mHomeActivity));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderTweet) {
            ((ViewHolderTweet) holder).populateViews(mDataList.get(position));
        }
    }

    /**
     * @param pShowLoader
     */
    public final void setLoaderState(boolean pShowLoader) {
        mShowLoader = pShowLoader;
        updateLoaderState();
        notifyDataSetChanged();
    }

    /**
     * @return mShowLoader
     */
    public final boolean isLoading() {
        return mShowLoader;
    }

    /**
     * This method uses  mShowLoader
     */
    protected final void updateLoaderState() {
        if (AppConfig.DEBUG) {
            Log.v(LOG_TAG, "updateLoaderState() " + mShowLoader);
        }
        if (mImgLoader == null) {
            return;
        }
        if (mShowLoader) {
            mImgLoader.setVisibility(View.VISIBLE);
            mAnimLoader.start();
        } else {
            mImgLoader.setVisibility(View.GONE);
            mAnimLoader.stop();
        }
    }

    protected class ViewHolderLoader extends RecyclerView.ViewHolder {

        /**
         * @param pImgLoader
         */
        public ViewHolderLoader(ImageView pImgLoader) {
            super(pImgLoader);
            mImgLoader = pImgLoader;

            mImgLoader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            mImgLoader.setImageResource(R.drawable.loader_general);
            mAnimLoader = (AnimationDrawable) mImgLoader.getDrawable();

            mAnimLoader.start();
        }
    }

    /**
     * @param pShowLoader
     */
    public final void updateOnlyLoderStateItem(boolean pShowLoader) {
        mShowLoader = pShowLoader;
        updateLoaderState();
        notifyItemChanged(mDataList.size());
    }
}
