package git.bpr10.trendingtweets.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Bedprakash on 2/12/2016.
 */
public class SearchResult {

    @SerializedName("statuses")
    private ArrayList<TweetModel> statuses;

    @SerializedName("search_metadata")
    private Metadata metadata;


    public ArrayList<TweetModel> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<TweetModel> statuses) {
        this.statuses = statuses;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
