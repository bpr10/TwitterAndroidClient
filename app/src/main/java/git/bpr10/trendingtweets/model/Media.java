package git.bpr10.trendingtweets.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bedprakash on 2/12/2016.
 */
public class Media {
    public static final String PHOTO = "photo";

    @SerializedName("media_url")
    String mediaUrl;

    @SerializedName("type")
    String type;

    @SerializedName("medium")
    MediaDimentions medium;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MediaDimentions getMedium() {
        return medium;
    }

    public void setMedium(MediaDimentions medium) {
        this.medium = medium;
    }

    public boolean isPhoto() {
        return PHOTO.equals(type);
    }
}
