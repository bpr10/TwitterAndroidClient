package git.bpr10.trendingtweets.views.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;

import git.bpr10.trendingtweets.R;
import git.bpr10.trendingtweets.config.NetworkCallback;
import git.bpr10.trendingtweets.config.NetworkManager;
import git.bpr10.trendingtweets.model.SearchResult;
import git.bpr10.trendingtweets.model.TweetModel;
import git.bpr10.trendingtweets.utils.Constants;
import git.bpr10.trendingtweets.utils.JsonUtils;
import git.bpr10.trendingtweets.utils.MyUtilities;

public class HomeActivity extends AppCompatActivity {
    public static final String LOG_TAG = HomeActivity.class.getSimpleName();

    private static final String TAG_SEARCH_FRAGMENT = "SEARCH_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentManager manager = getSupportFragmentManager();
        SearchFragment fragment;
        fragment = (SearchFragment) manager.findFragmentByTag(TAG_SEARCH_FRAGMENT);

        if (fragment == null) {
            fragment = new SearchFragment();
            manager.beginTransaction().replace(R.id.root_fragment, fragment, TAG_SEARCH_FRAGMENT).commit();
        }
    }
}
