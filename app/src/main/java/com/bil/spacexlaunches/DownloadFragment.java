package com.bil.spacexlaunches;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
                        if (prefs.contains("json_hash")){
                            int hash = 0;
                            prefs.getInt("json_hash", hash);
                            if(hash == mainJSON.hashCode()){
                                try {
                                    listener.downloaded(null, null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }

                        }//else{
                        prefs.edit().putInt("json_hash", mainJSON.hashCode()).apply();
                        try {
                            parseResponse();
                        }catch (JSONException e) {
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
        //setLaunchPatches();
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
    //
    //THIS MUST BE IN A SEPARATE THREAD!!!
    //
    private void savePatchesToStorage(){
        File dir = getContext().getFilesDir();
        if (dir.listFiles().length != 0) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
        for (Launch launch: launches){
            File file = new File(dir, launch.getNam()+".png");
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
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener = (DownloadListener)activity;
    }


}
