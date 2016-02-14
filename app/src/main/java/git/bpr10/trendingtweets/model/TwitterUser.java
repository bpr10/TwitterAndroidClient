package git.bpr10.trendingtweets.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bedprakash on 2/12/2016.
 */

public class TwitterUser {

    @SerializedName("name")
    private String name;

    public String getHandle() {
        return "@"+handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    @SerializedName("screen_name")
    private String handle;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}