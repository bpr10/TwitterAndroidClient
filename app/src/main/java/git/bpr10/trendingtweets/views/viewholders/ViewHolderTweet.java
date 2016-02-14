package git.bpr10.trendingtweets.views.viewholders;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import git.bpr10.trendingtweets.R;
import git.bpr10.trendingtweets.model.TweetModel;
import git.bpr10.trendingtweets.utils.Constants;
import git.bpr10.trendingtweets.utils.MyUtilities;
import git.bpr10.trendingtweets.utils.RoundedTransformation;
import git.bpr10.trendingtweets.views.activities.HomeActivity;
import git.bpr10.trendingtweets.views.activities.MediaView;

/**
 * Created by Bedprakash on 2/12/2016.
 */
public class ViewHolderTweet extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final HomeActivity mHomeActivity;

    private TextView mtxtTweet;
    private TextView mtxtUserName;
    private TextView mtxtUserHandle;
    private TextView mtxtTweetTime;

    private ImageView mImgProfilePic;
    private ImageView mImgTweetMedia;
    private View mRootMedia;

    TweetModel mtweetModel;

    public ViewHolderTweet(HomeActivity pHomeActivity, View view) {
        super(view);

        mHomeActivity = pHomeActivity;

        mtxtTweet = (TextView) view.findViewById(R.id.txt_tweet_text);
        mtxtTweetTime = (TextView) view.findViewById(R.id.txt_time);
        mtxtUserHandle = (TextView) view.findViewById(R.id.txt_user_handle);
        mtxtUserName = (TextView) view.findViewById(R.id.txt_user_name);
        mImgProfilePic = (ImageView) view.findViewById(R.id.img_profile_pic);
        mImgTweetMedia = (ImageView) view.findViewById(R.id.img_media);

        mImgTweetMedia.setOnClickListener(this);

        mRootMedia = view.findViewById(R.id.root_img_media);
    }

    public void populateViews(TweetModel tweet) {
        mtweetModel = tweet;

        mtxtTweetTime.setText(MyUtilities.getTweetTime(tweet.getDateCreated()));
        mtxtTweet.setText(tweet.getText());

        mImgProfilePic.setImageDrawable(null);

        if (tweet.getUser() != null) {
            mtxtUserName.setText(tweet.getUser().getName());
            mtxtUserHandle.setText(tweet.getUser().getHandle());
            Picasso.with(mHomeActivity).load(tweet.getUser().getProfileImageUrl())
                    .transform(new RoundedTransformation(MyUtilities.dpToPx(mHomeActivity, 1), 0))
                    .into(mImgProfilePic);
        } else {
            mtxtUserName.setText("");
            mtxtUserHandle.setText("");
        }

        if (tweet.getMediaUrl() != null) {
            mImgTweetMedia.setImageDrawable(null);
            mRootMedia.setVisibility(View.VISIBLE);
            Picasso.with(mHomeActivity).load(tweet.getMediaUrl()).into(mImgTweetMedia);
        } else {
            mRootMedia.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_media: {
                Intent i = new Intent(mHomeActivity, MediaView.class);
                i.putExtra(Constants.Keys.MEDIA, mtweetModel.getMediaUrl());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mImgTweetMedia != null) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mHomeActivity, mImgTweetMedia, mImgTweetMedia.getTransitionName());
                    mHomeActivity.startActivity(i, options.toBundle());
                } else {
                    mHomeActivity.startActivity(i);
                }

            }
        }
    }
}
