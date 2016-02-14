package git.bpr10.trendingtweets.config;

import android.app.Application;

/**
 * Created by Bedprakash on 2/11/2016.
 */
public class TweetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.init(getApplicationContext());
    }
}
