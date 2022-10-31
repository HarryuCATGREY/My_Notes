package com.example.whim;

import java.util.ArrayList;
import java.util.Date;

public class postmodel {

    // must match the firebase storage topic names: eg. title --> title
    private String title;
    private String content;
    private String image;
    private String time;
    private String location;
    private Date timestamp;
    private String imagename;
    private ArrayList<String> likedusers;
    private int  numlikes;
    private String uid;


    public postmodel(){

    }



    public postmodel(int numlikes, String title, String content, String image, String time, String location, ArrayList<String> likedusers, Date timestamp, String imagename, String uid){
        this.title = title;
        this.content = content;
        this.image = image;
        this.time = time;
        this.location = location;
        this.likedusers = likedusers;
        this.timestamp = timestamp;
        this.imagename = imagename;
        this.uid = uid;
        this.numlikes = numlikes;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public ArrayList<String> getLikedusers() {
        return likedusers;
    }

    public int getNumlikes() {
        return numlikes;
    }

    public String getUid() {
        return uid;
    }


    public String getImagename() {
        return imagename;
    }
}
