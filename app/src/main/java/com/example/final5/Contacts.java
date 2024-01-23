package com.example.final5;

public class Contacts {
    String name, image, status, uid;
    public  Contacts(){

    }
    public Contacts (String name, String image, String status, String uid){
        this.name = name;
        this.image = image;
        this.status = status;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getbio() {
        return status;
    }

    public void setbio(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
