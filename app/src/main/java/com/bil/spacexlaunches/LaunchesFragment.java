package com.bil.spacexlaunches;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class LaunchesFragment extends android.app.Fragment {


    public LaunchesFragment() {
        // Required empty public constructor
    }

    private List<Launch> launches = new ArrayList<>();
    private Map<String, Bitmap> patches = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView cardRecycler = (RecyclerView)inflater.inflate(R.layout.fragment_launches, container, false);
        CardAdapter adapter;
        adapter = new CardAdapter(launches, patches);
        cardRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        cardRecycler.setLayoutManager(layoutManager);
        return cardRecycler;
    }

    public void setContent(List<Launch> launches, Map<String, Bitmap> patches){
        this.launches = launches;
        this.patches = patches;
    }

}
