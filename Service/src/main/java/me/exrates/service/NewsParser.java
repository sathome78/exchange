package me.exrates.service;

import me.exrates.model.dto.news.FeedWrapper;

public interface NewsParser {
    FeedWrapper getFeeds(int offset, int count, int index);
}
