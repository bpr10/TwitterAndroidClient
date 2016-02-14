package git.bpr10.trendingtweets.config;

/**
 * Created by Bedprakash on 2/10/2016.
 */
public interface NetworkCallback {
    public void onResponseError(int error_code, String errorMsg);

    public void onResponseSuccess(String response);


}

