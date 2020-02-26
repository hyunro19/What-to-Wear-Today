package com.hyunro.layout.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunro.layout.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.ViewHolder> implements OnOutfitClickListener {
    public ArrayList<Map<String, Object>> outfitRecyclerArray = new ArrayList<>();
    OnOutfitClickListener listener;
    Context context;

    public OutfitAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_outfit, parent, false);

        return new OutfitAdapter.ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = outfitRecyclerArray.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return outfitRecyclerArray.size();
    }

    public void addItem(Map<String, Object> item) {
        outfitRecyclerArray.add(item);
    }

    public void setItems(ArrayList<Map<String, Object>> items) {
        this.outfitRecyclerArray = items;
    }

    public Map<String, Object> getItem(int position) {
        return outfitRecyclerArray.get(position);
    }

    public void setItem(int position, Map<String, Object> item) {
        outfitRecyclerArray.set(position, item);
        return;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView outfitRecycle_photo;
        TextView outfitRecycle_nickname;
        TextView outfitRecycle_ageGender;
        TextView outfitRecycle_location;
        TextView outfitRecycle_outer;
        TextView outfitRecycle_top;
        TextView outfitRecycle_bottom;
        TextView outfitRecycle_shoes;

        public ViewHolder(View itemView, final OnOutfitClickListener listener) {
            super(itemView);

            outfitRecycle_photo = itemView.findViewById(R.id.outfitRecycle_photo);
            outfitRecycle_nickname = itemView.findViewById(R.id.outfitRecycle_nickname);
            outfitRecycle_ageGender = itemView.findViewById(R.id.outfitRecycle_ageGender);
            outfitRecycle_location = itemView.findViewById(R.id.outfitRecycle_location);
            outfitRecycle_outer = itemView.findViewById(R.id.outfitRecycle_outer);
            outfitRecycle_top = itemView.findViewById(R.id.outfitRecycle_top);
            outfitRecycle_bottom = itemView.findViewById(R.id.outfitRecycle_bottom);
            outfitRecycle_shoes = itemView.findViewById(R.id.outfitRecycle_shoes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onOutfitClick(ViewHolder.this, view, position);
                    }
                }
            });

        }

        public void setItem(Map<String, Object> outfit) {

            outfitRecycle_photo.setImageBitmap((Bitmap)outfit.get("photo"));

            outfitRecycle_nickname.setText((String)outfit.get("nickname"));
            outfitRecycle_ageGender.setText((String)outfit.get("gender")+"/"+outfit.get("age"));
            outfitRecycle_location.setText((String)outfit.get("location"));
            outfitRecycle_outer.setText((String)outfit.get("outer"));
            outfitRecycle_top.setText((String)outfit.get("top"));
            outfitRecycle_bottom.setText((String)outfit.get("bottom"));
            outfitRecycle_shoes.setText((String)outfit.get("shoes"));

        }
    }
    public void setOnOutfitClickListener(OnOutfitClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onOutfitClick(ViewHolder holder, View view, int position) {
        if(listener != null) {
            listener.onOutfitClick(holder, view, position);
        }
    }
}