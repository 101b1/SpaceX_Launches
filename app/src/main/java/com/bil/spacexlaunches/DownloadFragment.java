package com.bil.spacexlaunches;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends android.app.Fragment {

    List<Launch> launches = new ArrayList<>();
    Map<String, Bitmap> patches = new HashMap<>();
    RequestQueue requestQueue;
    RequestQueue imageQueue;
    //Main JSON variable for later parsing to single JSON object
    JSONArray mainJSON;
    //Buffer var to save network responses
    final String api = "https://api.spacexdata.com/v2/launches?launch_year=2017";
    private DownloadListener listener;
    SharedPreferences prefs;

    public DownloadFragment() {
        // Required empty public constructor
    }

    static interface DownloadListener{
        void downloaded(List<Launch> dlLaunches, Map<String, Bitmap> dlPatches) throws FileNotFoundException;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestQueue = Volley.newRequestQueue(container.getContext());
        imageQueue = Volley.newRequestQueue(container.getContext());
        prefs = getActivity()
                .getSharedPreferences("preferences", Context.MODE_PRIVATE);

        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        getAPIResponse();
    }

    private void getAPIResponse(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                api, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mainJSON = response;
                        //
                        //CHECK RESPONSE HASH & SAVED HASH
                        //IF NO SAVED HASH => SAVE
                        //IF SAVED == RESPONSE, LISTENER.DOWNLOADED()
                        //ELSE CONTINUE
                        //
                        try {
                        if (prefs.contains("json_hash")){
                            int hash = prefs.getInt("json_hash", 0);
                            int dlhash = stringHash(mainJSON.toString());
                            LaunchesDatabaseHelper dbHelper = new LaunchesDatabaseHelper(getContext());
                                if(hash == dlhash){
                                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                                    Cursor cursor =  db.query(dbHelper.getDatabaseName(), null, null,
                                            null, null, null, null);
                                    if(cursor.moveToFirst()){
                                        while(!cursor.isAfterLast()){
                                            launches.add(new Launch(cursor.getString(1), //NAME
                                                    cursor.getString(2), //DESC
                                                    new Date(Long.parseLong(cursor.getString(3))),//DATE
                                                    cursor.getString(4),//ARTIC
                                                    cursor.getString(5),//URL
                                                    cursor.getString(6),//PATH
                                                    Long.parseLong(cursor.getString(3))));
                                            //File file  = new File(cursor.getString(5));
                                            //FileInputStream  fis = new FileInputStream(file.getAbsolutePath());
                                            Bitmap tmpBit = BitmapFactory.decodeFile(cursor.getString(6));
                                            patches.put(cursor.getString(5), tmpBit);
                                            cursor.moveToNext();
                                        }
                                    }
                                    db.close();
                                    listener.downloaded(launches, patches);
                                    return;
                            }
                        } try {
                                prefs.edit().putInt("json_hash", stringHash(mainJSON.toString())).apply();
                                parseResponse();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                        //}

                        //launches = new Launch[mainJSON.length()];

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(error.toString(),error.toString());
            }
        });
        requestQueue.add(request);
    }


    void parseResponse() throws JSONException {
        //tmpPatch = new Bitmap[mainJSON.length()];
        for (int i=0; i<mainJSON.length(); i++){
            launches.add(parseObject(mainJSON.getJSONObject(i), i));
        }
        //saveLaunchInfo(launches);
    }

    private int stringHash(String str){
        int result = 1;
        for (int i = 0; i < str.length(); i++){
            result += 8*str.charAt(i);
        }
        return result;
    }

    //Parsing every single launch information from JSON object
    Launch parseObject(JSONObject object, int pos) throws JSONException {
        Launch tmp = new Launch();
        tmp.unixTime = object.getLong("launch_date_unix");
        tmp.setDat(new Date(tmp.unixTime));
        tmp.setDescription(object.getString("details"));
        //getLaunchLabel(object.getJSONObject("links").getString("mission_patch"), pos);
        tmp.imageURL = object.getJSONObject("links").getString("mission_patch");
        getPatch(tmp.imageURL);
        tmp.setNam(object.getJSONObject("rocket").getString("rocket_name"));
        tmp.setArticle(object.getJSONObject("links").getString("article_link"));
        return tmp;
    }

    void getPatch(final String mission_patch) {
        ImageRequest request = new ImageRequest(mission_patch,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        patches.put(mission_patch, response);
                        if (patches.size() == launches.size())
                        {
                            //call fragment transaction
                            savePatchesToStorage();
                            try {
                                listener.downloaded(launches, patches);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 1024, 1024, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        imageQueue.add(request);
    }

//    private void saveLaunchInfo(final List<Launch> launches){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                LaunchesDatabaseHelper helper = new LaunchesDatabaseHelper(getContext());
//                SQLiteDatabase db = helper.getWritableDatabase();
//                db.close();
//                helper.deleteContent();
//                helper.insertContent(launches);
//            }
//        });
//        thread.start();
//    }

    private void savePatchesToStorage(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = getContext().getFilesDir();
                if (dir.listFiles().length != 0) {
                    for (File file : dir.listFiles()) {
                        file.delete();
                    }
                }
                for (Launch launch: launches){
                    File file = new File(dir, launch.getNam() + launches.indexOf(launch)+".png");
                    launch.patchPath = file.getPath();
                    try{
                        FileOutputStream fos = new FileOutputStream(file);
                        patches.get(launch.imageURL).compress(Bitmap.CompressFormat.PNG, 90,
                                fos);
                        fos.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                LaunchesDatabaseHelper helper = new LaunchesDatabaseHelper(getContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                helper.deleteContent(db);
                helper.insertContent(db, launches);
                db.close();
            }
        });
        thread.start();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener = (DownloadListener)activity;
    }

}
