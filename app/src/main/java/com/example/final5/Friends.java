package com.example.final5;

public class Friends {
    private  String status, image,name,uid;

    public Friends(String bio, String image, String name, String uid) {
        this.status = bio;
        this.image = image;
        this.name = name;
       // this.uid = uid;
    }
    public  Friends(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
