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
    private String article;
    public String imageURL;
    public String patchPath;
    public long unixTime;

    public Launch(){

    }

    public Launch(String name, String description, Date date, String article, String imageURL,
                  String path, long uTime){

        this.name = name;
        this.date = date;
        this.description = description;
        //this.label = label;
        this.article = article;
        this.imageURL = imageURL;
        this.patchPath = path;
        this.unixTime = uTime;

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

    String getPatchPath(){ return patchPath; }

   /* Bitmap getLabel(){
        return label;
    }*/

    void setNam(String name){
        this.name = name;
    }

    void setDescription(String des){
        this.description = des;
    }

    void setDat(Date date){
        this.date = date;
    }

    /*void setLabel(Bitmap label){
        this.label = label;
    }*/

    void setArticle(String art){ this.article = art;}

    void setPatchPath(String path){ this.patchPath = path;}
}
