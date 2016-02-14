package git.bpr10.trendingtweets.config;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import git.bpr10.trendingtweets.R;
import git.bpr10.trendingtweets.utils.Constants;
import git.bpr10.trendingtweets.utils.Constants.ERROR_CODES;
import git.bpr10.trendingtweets.utils.JsonUtils;

/**
 * Created by Bedprakash on 2/11/2016.
 */
public class NetworkManager {

    private static final String LOG_TAG = NetworkManager.class.getSimpleName();
    private static RequestQueue mRequestQueue;
    private static final RetryPolicy RETRY_POLICY_GET = new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    public static final RetryPolicy RETRY_POLICY_GET_SHORT = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private static final RetryPolicy RETRY_POLICY_POST = new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private static final RetryPolicy RETRY_POLICY_POST_SHORT = new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private static final NetworkResponse RESPONSE_NO_INTERNET = new NetworkResponse(ERROR_CODES.NO_INTERNET, "Network Not Available".getBytes(), null, false);
    // volley objects - end


    public static final int DEFAULT_PAGE_SIZE = 10;

    public static final void init(Context context) {

        mRequestQueue = Volley.newRequestQueue(context);
    }

    private static void dispatchToQueue(Request pRequest, Context pContext) {
        if (!isNetworkConnected(pContext) && pRequest.getMethod() != Request.Method.GET) {
            // non-GET request should not return cached response in case of no network
            pRequest.deliverError(new VolleyError(RESPONSE_NO_INTERNET));
            return;
        }
        switch (pRequest.getMethod()) {
            case Request.Method.POST: {
                pRequest.setRetryPolicy(RETRY_POLICY_POST);
                break;
            }
            case Request.Method.GET:
            default: {
                pRequest.setRetryPolicy(RETRY_POLICY_GET);
                break;
            }
        }
        mRequestQueue.add(pRequest);
    }

    private static Request<String> bundleToVolleyRequestWithSoftTtl(
            final Context context, int what, final Object newRequest,
            String url, final NetworkCallback mListener) {
        // start code for logging and analytics
        if (AppConfig.DEBUG) {
            Log.d(LOG_TAG, "req obj: " + JsonUtils.jsonify(newRequest));
        }
        StringBuffer buffer = new StringBuffer(url);
        buffer.append(" response--");
        final String url_recieved = buffer.toString();
        // end
        Request<String> tempRequest = new JsonRequest<String>(what, url,
                JsonUtils.jsonify(newRequest), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AppConfig.DEBUG) {
                    Log.d(LOG_TAG, "URL: " + url_recieved);
                    Log.d(LOG_TAG, "Response: " + response);
                }
                if (mListener != null) {
                    mListener.onResponseSuccess(response);
                }
            }
        },
                new VolleyErrorListenerImpl(context, url_recieved, mListener)) {
            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                Response<String> mResponse;
                if (response.statusCode == 200) {
                    String responseBody = new String(response.data);
                    mResponse = Response.success(
                            responseBody,
                            parseIgnoreCacheHeaders(response, this.getUrl(),
                                    Constants.VolleyTtl.SOFT_TTL_1,
                                    Constants.VolleyTtl.HARD_TTL_1));

                } else {
                    parseNetworkError(new VolleyError(response));
                    mResponse = Response.error(new VolleyError(response));

                }
                return mResponse;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headersMap = new HashMap<String, String>();
                headersMap.put("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAF4%2BkQAAAAAAqNRXibX5ZGL%2B8TBwCtUaj0f4rTk%3DycbwcfXz8S6LBy22x4dmiQzxJHdnufmPL8bYsche7XsQ9gzZsX");
                return headersMap;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        return tempRequest;
    }

    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response,
                                                      String url, long softttl, long hardttl) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;
        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        // entry expires
        // completely
        final long softExpire = now + softttl;
        final long ttl = now + hardttl;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

    /**
     * Parse date in RFC1123 format, and return its value as epoch
     */
    public static long parseDateAsEpoch(String dateStr) {
        try {
            // Parse date in RFC1123 format if this header contains one
            return DateUtils.parseDate(dateStr).getTime();
        } catch (DateParseException e) {
            // Date in invalid format, fallback to 0
            return 0;
        }
    }

    public static boolean isNetworkConnected(Context pContext) {
        ConnectivityManager cm = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static boolean isNetworkConnectedWithMessage(Activity pActivity) {
        ConnectivityManager cm = (ConnectivityManager) pActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            showToast(pActivity, R.string.no_internet_body);
            return false;
        }
        return true;
    }

    /**
     * @param pActivity
     * @param pResId
     */
    public static void showToast(Activity pActivity, int pResId) {
        showToast(pActivity, pActivity.getString(pResId));
    }

    /**
     * @param pActivity
     * @param pMessage
     * @note prefer {@link NetworkManager#showToast(Activity, int)} instead
     */
    public static void showToast(final Activity pActivity, final String pMessage) {
        if (pMessage == null || pMessage.trim().length() == 0 || pActivity.isFinishing()) {
            return;
        }
        pActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pActivity.isFinishing()) {
                    return;
                }
                Toast.makeText(pActivity, pMessage.trim(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showNetworkNotAvailableToast(final Activity pActivity) {

        pActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pActivity.isFinishing()) {
                    return;
                }
                showToast(pActivity, R.string.no_internet_body);
            }
        });
    }

    public static void searchTweet(Context context, String pUrl, NetworkCallback callback) {
        StringBuilder url = new StringBuilder(pUrl);
        url.append("&count=" + DEFAULT_PAGE_SIZE);
        Request<String> volleyTypeRequest = null;
        volleyTypeRequest = bundleToVolleyRequestWithSoftTtl(
                context, Request.Method.GET, null, url.toString(), callback);
        volleyTypeRequest.setTag(callback);
        volleyTypeRequest.setShouldCache(true);
        dispatchToQueue(volleyTypeRequest, context);
    }

}
