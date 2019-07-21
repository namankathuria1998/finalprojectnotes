package com.example.hp.finalprojectnotes;

import java.io.Serializable;

public class Notes implements Serializable{

    public Notes() {
    }


  String photo,title,actualcontent,date,key;


    public Notes(String photo, String title, String actualcontent, String date, String key) {
        this.photo = photo;
        this.title = title;
        this.actualcontent = actualcontent;
        this.date = date;
        this.key = key;
    }

    public String getActualcontent() {
        return actualcontent;
    }

    public String getKey() {
        return key;
    }

    public void setActualcontent(String actualcontent) {
        this.actualcontent = actualcontent;
    }

    public String getDate() {
        return date;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTitle() {
        return title;
    }
}
