package com.bil.spacexlaunches;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.TimedText;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by boris on 17.02.18.
 */

class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<Launch> launches;
    private Map<String, Bitmap> patches = new HashMap<>();
    //RequestQueue imageQueue;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView c) {
            super(c);
            cardView = c;
        }
    }

    public CardAdapter(List<Launch> launches, Map<String, Bitmap> patches) {
        this.launches = launches;
        this.patches = patches;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_flight, parent, false);

        /*for (int i = 0; i<=launches.size(); i++){
            getPatch(launches.get(i).imageURL, i, (ImageView)parent.findViewById(R.id.label));
        }*/
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //imageQueue = Volley.newRequestQueue(holder.cardView.getContext());
        final CardView card = holder.cardView;
        ImageView flightLabel = card.findViewById(R.id.label);
        if (patches.containsKey(launches.get(position).imageURL))
            flightLabel.setImageBitmap(patches.get(launches.get(position).imageURL));
        else{
            flightLabel.setImageResource(R.mipmap.ic_launcher);
            //getPatch(launches.get(position).imageURL, position, flightLabel);
        }
        TextView name = card.findViewById(R.id.launch_name);
        name.setText(launches.get(position).getNam());
        TextView time = card.findViewById(R.id.launch_time);
        time.setText(launches.get(position).getDate().toString());
        TextView desc = card.findViewById(R.id.launch_desc);
        desc.setText(launches.get(position).getDescription());
        View.OnClickListener cardListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(launches.get(position).getArticle()));
                card.getContext().startActivity(articleIntent);
            }
        };
        card.setOnClickListener(cardListener);
    }

   /* void getPatch(final String mission_patch, final int pos, final ImageView view) {
        ImageRequest request = new ImageRequest(mission_patch,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        patches.put(mission_patch, response);
                        view.setImageBitmap(response);
                    }
                }, 1024, 1024, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        imageQueue.add(request);
    }*/

    @Override
    public int getItemCount() {
        if (launches != null) return launches.size();
        return 0;
    }
}