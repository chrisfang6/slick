package net.chris.ui.slick.model;

import java.io.Serializable;

public class HomeMenuItem implements Serializable {

    private String imageUri;

    private String title;

    private String summary;

    public HomeMenuItem(String imageUri, String title, String summary) {
        setImageUri(imageUri);
        setTitle(title);
        setSummary(summary);
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static HomeMenuItem create(String imageRes, String title, String summary) {
        return new HomeMenuItem(imageRes, title, summary);
    }

}
