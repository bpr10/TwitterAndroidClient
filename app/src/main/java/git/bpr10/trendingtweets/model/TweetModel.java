package git.bpr10.trendingtweets.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Bedprakash on 2/12/2016.
 */
public class TweetModel {


    @SerializedName("created_at")
    private String dateCreated;


    @SerializedName("id")
    private String id;


    @SerializedName("text")
    private String text;


    @SerializedName("in_reply_to_status_id")
    private String inReplyToStatusId;


    @SerializedName("in_reply_to_user_id")
    private String inReplyToUserId;


    @SerializedName("in_reply_to_screen_name")
    private String inReplyToScreenName;


    @SerializedName("user")
    private TwitterUser user;

    @SerializedName("entities")
    private Entities entities;

    @SerializedName("retweet_count")
    private int retweetCount;

    @SerializedName("favorite_count")
    private int favoriteCount;

    private String mediaUrl;

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getId() {
        return id;
    }


    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }


    public String getInReplyToStatusId() {
        return inReplyToStatusId;
    }


    public String getInReplyToUserId() {
        return inReplyToUserId;
    }


    public String getText() {
        return text;
    }


    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public void setInReplyToUserId(String inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void setUser(TwitterUser user) {
        this.user = user;
    }


    public TwitterUser getUser() {
        return user;
    }


    @Override
    public String toString() {
        return getText();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TweetModel)) {
            return false;
        }

        return this.id.equals(((TweetModel) o).id);
    }

    public String getMediaUrl() {
        if (mediaUrl != null) {
            return mediaUrl;
        }

        if (getEntities() == null || getEntities().getMedia() == null
                || getEntities().getMedia().isEmpty())
            return null;

        for (Media media : getEntities().getMedia()) {
            if (media.isPhoto()) {
                mediaUrl = media.getMediaUrl();
                break;
            }
        }
        return mediaUrl;
    }
}
