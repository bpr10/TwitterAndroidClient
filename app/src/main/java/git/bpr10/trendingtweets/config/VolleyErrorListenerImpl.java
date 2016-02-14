package git.bpr10.trendingtweets.config;

import android.content.Context;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

import git.bpr10.trendingtweets.utils.Constants.ERROR_CODES;

/**
 * @author sachin.gupta
 */
public class VolleyErrorListenerImpl implements ErrorListener {

    private final static String LOG_TAG = VolleyErrorListenerImpl.class
            .getSimpleName();

    private final static String CATEGORY_ERROR = "CATEGORY_ERROR";

    private Context mContext;
    private String mApiActionPath;
    private NetworkCallback mRequestListener;

    /**
     * @param pContext
     * @param pRequestListener
     */
    public VolleyErrorListenerImpl(Context pContext, String pApiActionPath,
                                   NetworkCallback pRequestListener) {
        this.mContext = pContext;
        this.mApiActionPath = pApiActionPath;
        this.mRequestListener = pRequestListener;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, mApiActionPath);

        int errorCode;
        String message;

        if (error instanceof NoConnectionError) {
            Log.e(LOG_TAG, "Error: " + error);
            errorCode = ERROR_CODES.NO_INTERNET;
            message = "Network Not Available";
        } else {
            Log.e(LOG_TAG, "Error: " + error);
            message = error.getMessage();
            errorCode = ERROR_CODES.SHIT_HAPPENED;
        }

        Log.e(LOG_TAG, "Error: " + errorCode + ", " + message);

        if (mRequestListener != null) {
            mRequestListener.onResponseError(errorCode, message);
        }
    }
}
