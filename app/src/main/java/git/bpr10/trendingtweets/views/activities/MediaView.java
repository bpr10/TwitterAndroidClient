package git.bpr10.trendingtweets.views.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import git.bpr10.trendingtweets.R;
import git.bpr10.trendingtweets.model.Media;
import git.bpr10.trendingtweets.model.TweetModel;
import git.bpr10.trendingtweets.utils.Constants;
import git.bpr10.trendingtweets.utils.JsonUtils;
import git.bpr10.trendingtweets.utils.MyUtilities;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MediaView extends AppCompatActivity{

    private GestureDetector mDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_view);

        ImageView mImgMedia = (ImageView) findViewById(R.id.img_detailed);
        Picasso.with(this).load(getIntent().getExtras().getString(Constants.Keys.MEDIA)).into(mImgMedia);

        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
