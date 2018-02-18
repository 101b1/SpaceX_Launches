package com.bil.spacexlaunches;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by boris on 17.02.18.
 */
//This class contains info about every launch, includes methods for getting/setting this info
public class Launch {

    private String name;
    private String description;
    private Date date;
    private Bitmap label;
    private String article;

    public Launch(){

    }

    public Launch(String name, String description, Date date, Bitmap label, String article){
        this.name = name;
        this.date = date;
        this.description = description;
        this.label = label;
        this.article = article;
    }

    String getNam(){
        return name;
    }

    String getDescription(){
        return description;
    }

    String getArticle(){
        return article;
    }

    Date getDate(){
        return date;
    }

    Bitmap getLabel(){
        return label;
    }

    void setNam(String name){
        this.name = name;
    }

    void setDescription(String des){
        this.description = des;
    }

    void setDat(Date date){
        this.date = date;
    }

    void setLabel(Bitmap label){
        this.label = label;
    }

    void setArticle(String art){ this.article = art;}
}
