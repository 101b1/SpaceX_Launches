package com.bil.spacexlaunches;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;

/**
 * Created by boris on 23.04.18.
 */

public class LaunchesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "launches";
    private static final int DB_VERSION = 1;

    LaunchesDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        updateDb(sqLiteDatabase, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        updateDb(sqLiteDatabase, i, i1);
    }

    private void updateDb(SQLiteDatabase db, int oldVer, int newVer){
        if (oldVer < 1){
            db.execSQL("CREATE TABLE LAUNCHES (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "NAME TEXT, " +
                        "DESCRIPTION TEXT, " +
                        "DATE TEXT, " +
                        "ARTICLE TEXT, " +
                        "IMAGE_URL TEXT, " +
                        "IMAGE_PATH TEXT);");
        }
    }

    public void insertContent(List<Launch> launchList){
        SQLiteDatabase db = getWritableDatabase();
        for (Launch launch: launchList){
            ContentValues value = new ContentValues();
            value.put("NAME", launch.getNam());
            value.put("DESCRIPTION", launch.getDescription());
            value.put("DATE", Long.toString(launch.unixTime));
            value.put("ARTICLE", launch.getArticle());
            value.put("IMAGE_URL", launch.imageURL);
            value.put("IMAGE_PATH", launch.patchPath);
            db.insert(DB_NAME, null, value);
        }
        db.close();
    }

    public void deleteContent(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DB_NAME, null, null);
        db.close();
    }
}
