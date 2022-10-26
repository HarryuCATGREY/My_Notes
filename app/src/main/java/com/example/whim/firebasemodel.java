package com.example.whim;

public class firebasemodel {

    // must match the firebase storage topic names: eg. title --> title
    private String title;
    private String content;
    private String image;
    private String time;
    private String location;

    public firebasemodel(){

    }

    public firebasemodel(String title, String content, String image, String time, String location){
        this.title = title;
        this.content = content;
        this.image = image;
        this.time = time;
        this.location = location;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }
}

