package git.bpr10.trendingtweets.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import git.bpr10.trendingtweets.config.AppConfig;
import git.bpr10.trendingtweets.model.TweetModel;

/**
 * Created by Bedprakash on 2/11/2016.
 */
public class MyUtilities {

    private static final String LOG_TAG = MyUtilities.class.getSimpleName();

    private static final int ONE_HOUR = 3600;
    private static final int ONE_DAY = 3600 * 24;

    public static boolean isNearEnd(int firstVisibleItem, int visibleItemCount,
                                    int totalItemCount) {
        return totalItemCount - (firstVisibleItem + visibleItemCount) < 3
                && totalItemCount - 1 != 0 && totalItemCount > visibleItemCount;
    }

    /**
     * appends unique items from pNewList at end of pBaseList
     *
     * @param pBaseList
     * @param pNewList
     */
    public static int append(ArrayList<TweetModel> pBaseList, ArrayList<TweetModel> pNewList) {
        return append(pBaseList, pNewList, -1);
    }

    /**
     * appends unique items from pNewList at the given index of pBaseList or at end of pBaseList
     *
     * @param pBaseList
     * @param pNewList
     * @param pAddAtIndex
     * @return number of new posts added
     */
    public static int append(ArrayList<TweetModel> pBaseList, ArrayList<TweetModel> pNewList, int pAddAtIndex) {
        if (pBaseList == null || pNewList == null || pNewList.isEmpty()) {
            return 0;
        }
        if (AppConfig.DEBUG) {
            Log.i(LOG_TAG, "append() Base: " + pBaseList.size() + " New: " + pNewList.size() + ", AddAt: " + pAddAtIndex);
        }
        for (int i = 0; i < pNewList.size(); i++) {
            TweetModel newItem = pNewList.get(i);
            if (pBaseList.contains(newItem)) {
                pNewList.remove(i);
                i--;
            }
        }
        if (pAddAtIndex >= 0 && pAddAtIndex <= pBaseList.size()) {
            pBaseList.addAll(pAddAtIndex, pNewList);
        } else {
            pBaseList.addAll(pNewList);
        }
        return pNewList.size();
    }

    public static String getTweetTime(String dateCreated) {

        try {
            return getProperTimeString(getTwitterDate(dateCreated).getTime()/1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date getTwitterDate(String date) throws ParseException {

        final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
        sf.setLenient(true);
        return sf.parse(date);
    }

    public static String getProperTimeString(long timeInSeconds) {
        return getProperTimeString(System.currentTimeMillis() / 1000,
                timeInSeconds);
    }

    public static String getProperTimeString(long currentSecs,
                                             long timeInSeconds) {
        long diff = currentSecs - timeInSeconds;
        if (diff < 0) {
            return "0m";
        } else if (0 <= diff && diff < ONE_HOUR) {
            // less then an hour
            int min = (int) diff / 60;
            return min + "m";
        } else if (ONE_HOUR <= diff && diff < ONE_DAY) {
            int hour = (int) diff / 3600;
            return hour + "h";
        } else if (ONE_DAY <= diff && diff < ONE_DAY * 7) {
            int d = (int) diff / ONE_DAY;
            return d + "d";
        } else {
            Calendar cal = Calendar.getInstance(Locale.US);
            cal.setTimeInMillis(timeInSeconds * 1000);
            Calendar calCurrent = Calendar.getInstance(Locale.US);
            String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                    Locale.US);
            String dateSting = cal.getDisplayName(Calendar.DATE,
                    Calendar.SHORT, Locale.US);
            dateSting = cal.getTime().getDate() + "";
            String yr = cal.getDisplayName(Calendar.YEAR, Calendar.SHORT,
                    Locale.US);
            yr = cal.getTime().getYear() + "";
            String yrnow = calCurrent.getDisplayName(Calendar.DATE,
                    Calendar.SHORT, Locale.US);
            yrnow = calCurrent.getTime().getYear() + "";
            String finaldate = dateSting + " " + month;
            if (!yrnow.equals(yr)) {
                finaldate += " " + yr;
            }
            return finaldate;
        }
    }

    public static int dpToPx(Context mCtx, int dp) {
        DisplayMetrics displayMetrics = mCtx.getResources().getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * @param pActivity
     * @param pView     or null
     */
    public static void showKeyboard(Activity pActivity, View pView) {
        if (pView == null) {
            pView = pActivity.getWindow().getCurrentFocus();
        } else {
            /**
             * For {@link EditText}, a call to {@link View#requestFocus()} will
             * open the keyboard as per inputType set for {@link EditText}
             */
            pView.requestFocus();
        }
        if (pView != null) {
            InputMethodManager imm = (InputMethodManager) pActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(pView, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    /**
     * @param pView
     * @param pActivity
     */
    public static void hideKeyboard(View pView, Activity pActivity) {
        if (pView == null) {
            pView = pActivity.getWindow().getCurrentFocus();
        }
        if (pView != null) {
            InputMethodManager imm = (InputMethodManager) pActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(pView.getWindowToken(), 0);
            }
        }
    }

}
