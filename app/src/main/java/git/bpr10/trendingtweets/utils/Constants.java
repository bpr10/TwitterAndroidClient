package git.bpr10.trendingtweets.utils;

/**
 * Created by Bedprakash on 2/10/2016.
 */
public class Constants {

    public static final String TWITTER_API_BASE_URL = "https://api-twitter-com-scg2irn5xj2b.runscope.net/1.1/search/tweets.json";
    public interface Keys {
        String URL = "url";
        String NETWORK_RECIEVER = "reciever";
        String REST_METHOD_TYPE = "rest_method";
        String REST_METHOD_GET = "get";
        String REST_METHOD_POST = "post";
        String ERROR_CODE = "error_code";
        String ERROR_MSG = "error_msg";
        String RESPONSE = "response";
        java.lang.String KEY_SEARCH = "search";
        java.lang.String FILTER = "filter";
        String MEDIA = "media";
    }


    public interface ResultCodes {

        int STATUS_FINISHED = 101;
        int STATUS_RUNNING = 102;
        int STATUS_ERROR = 103;


    }

    public interface ERROR_CODES {
        int SHIT_HAPPENED = 111;
        int NO_INTERNET = 112;
        int EMPTY_RESULTS = 113;
    }

    public interface VolleyTtl {
        long SOFT_TTL_1 = 1 * 1000;
        long HARD_TTL_1 = 24 * 60 * 60 * 1000;
    }
}
