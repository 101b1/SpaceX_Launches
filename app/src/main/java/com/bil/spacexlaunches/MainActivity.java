package com.bil.spacexlaunches;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements DownloadFragment.DownloadListener{
    //Variable, containing launches info
    List<Launch> launches = new ArrayList<>();
    //Queues for netwrk requests
    RequestQueue requestQueue;
    RequestQueue imageQueue;
    //Main JSON variable for later parsing to single JSON object
    JSONArray mainJSON;
    //Buffer var to save network responses
    Bitmap[] tmpPatch;
    final String api = "https://api.spacexdata.com/v2/launches?launch_year=2017";
    CardAdapter adapter;
    RecyclerView recyclerView;

    //Getting main JSON response from API, containing all launches
   /* private void getAPIResponse(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                api, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mainJSON = response;
                        //launches = new Launch[mainJSON.length()];
                        try {
                            parseResponse();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(0);
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
        tmp.setDat(new Date(object.getLong("launch_date_unix")));
        tmp.setDescription(object.getString("details"));
        //getLaunchLabel(object.getJSONObject("links").getString("mission_patch"), pos);
        tmp.imageURL = object.getJSONObject("links").getString("mission_patch");
        tmp.setNam(object.getJSONObject("rocket").getString("rocket_name"));
        tmp.setArticle(object.getJSONObject("links").getString("article_link"));
        return tmp;
    }
    //Requesting for launch patch
    void getLaunchLabel(String mission_patch, final int pos) {
        ImageRequest request = new ImageRequest(mission_patch,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        tmpPatch[pos] = response;
                        setLaunchPatches();
                    }
                }, 1024, 1024, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        imageQueue.add(request);
    }*/
    //Setting patches file source for every launch and finally viewing UI
   /* private void setLaunchPatches() {
        for(int i =0; i<launches.length; i++) launches[i].setLabel(tmpPatch[i]);
        adapter = new CardAdapter(launches);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initializing queues for API requests
        //requestQueue = Volley.newRequestQueue(this);
        //imageQueue = Volley.newRequestQueue(this);
        //Initializing main JSON variable
        //mainJSON = new JSONArray();
        setContentView(R.layout.activity_main);
        DownloadFragment fr = new DownloadFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fr);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
        //Starting to requesting for API
        //getAPIResponse();
        //recyclerView = findViewById(R.id.recycler);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        //adapter = new CardAdapter(launches);
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public void downloaded(List<Launch> dlLaunches, Map<String, Bitmap> dlPatches) throws FileNotFoundException {
        //IF EXISTS DB => IF NEW CONTENT =>  => REFRESH DB
        //                ELSE GET FROM DB / USE DB CURSOR IN ADAPTER???
        //ELSE CREATE DB
        LaunchesFragment fr = new LaunchesFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fr.setContent(dlLaunches, dlPatches);
        transaction.replace(R.id.fragment_container, fr);
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }
}
