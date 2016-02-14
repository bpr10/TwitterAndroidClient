package git.bpr10.trendingtweets.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Bedprakash on 2/12/2016.
 */
public class Entities {
    @SerializedName("media")
    private ArrayList<Media> media;

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }
}
