package com.bil.spacexlaunches;

import android.content.Intent;
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

import java.util.Date;
import java.util.List;

/**
 * Created by boris on 17.02.18.
 */

class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{

    private Launch[] launches;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView c){
            super(c);
            cardView = c;
        }
    }


    public CardAdapter(Launch[] launches){
        this.launches = launches;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       CardView card = (CardView) LayoutInflater.from(parent.getContext())
               .inflate(R.layout.card_flight, parent, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView card = holder.cardView;
        ImageView flightLabel = card.findViewById(R.id.label);
        flightLabel.setImageBitmap(launches[position].getLabel());
        TextView name = card.findViewById(R.id.launch_name);
        name.setText(launches[position].getNam());
        TextView time = card.findViewById(R.id.launch_time);
        time.setText(launches[position].getDate().toString());
        TextView desc = card.findViewById(R.id.launch_desc);
        desc.setText(launches[position].getDescription());
        View.OnClickListener cardListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(launches[position].getArticle()));
                card.getContext().startActivity(articleIntent);
            }
        };
        card.setOnClickListener(cardListener);
    }

    @Override
    public int getItemCount() {
        if(launches != null) return launches.length;
        return 0;
    }


}