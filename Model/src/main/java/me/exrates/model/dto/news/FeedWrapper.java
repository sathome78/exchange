package me.exrates.model.dto.news;

import java.util.Collections;
import java.util.List;

public class FeedWrapper {

    private List<Feed> feeds;
    private int count;

    public FeedWrapper(List<Feed> feeds, int count) {
        this.feeds = feeds;
        this.count = count;
    }

    public static FeedWrapper ofNull() {
        return new FeedWrapper(Collections.emptyList(), 0);

    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
