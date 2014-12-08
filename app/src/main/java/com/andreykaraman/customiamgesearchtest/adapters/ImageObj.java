package com.andreykaraman.customiamgesearchtest.adapters;

public class ImageObj {
    String thumbUrl;
    String title;
    String fullUrl;
    boolean bookmarked;

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }


    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String url) {
        this.thumbUrl = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}