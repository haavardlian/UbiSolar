package com.sintef_energy.ubisolar.model;

/**
 * Created by baier on 5/2/14.
 */
public class NewsFeed {

    private String newsId;
    private String newsContent;

    public NewsFeed (String newsId, String newsContent) {
        this.newsId = newsId;
        this.newsContent =newsContent;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public NewsFeed (String newsContent) {
        this.newsContent = newsContent;

    }
}
