package com.hyunro.layout.location;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunro.layout.R;

import java.util.ArrayList;

public class LocAdapter extends RecyclerView.Adapter<LocAdapter.ViewHolder> implements OnItemClickListener {
    ArrayList<String> list = new ArrayList<>();
    OnItemClickListener listener;
    private static String keyword;
    LocAdapter(){
        Log.d("Constructor : ","Default Constructor");
    }
    LocAdapter(String keyword){
        keyword = keyword;
        Log.d("Constructor : ","Constructor with keyword");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_loc, parent, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String firstLoc = list.get(position);
        holder.setItem(firstLoc);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(String firstLoc) {
        list.add(firstLoc);
    }
    public void setItems(ArrayList<String> firstLocList) {
        this.list = firstLocList;
    }
    public String getItem(int position) {
        return list.get(position);
    }
    public void setItem(int position, String firstLoc) {
        list.set(position, firstLoc);
        return;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.locSelected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 이 뷰 홀더에 표시할 아이템이 어댑터에서 몇 번째인지 정보 반환(index 반환)
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(String item) {
            if(keyword != null) {
                //                spannable이 적용이 안된다. 오류남.
                // 문제1. 이 분기로 넘어오지 않는다. keyword를 계속 null로 인식한다.
                final SpannableStringBuilder sb = new SpannableStringBuilder(item);
                final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(200, 0, 0));
//                sb.setSpan(fcs, item.indexOf(keyword), keyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sb.setSpan(fcs, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                textView.setText(sb);
            } else {
//                문제2.   spannable이 적용이 안된다. 오류남.
//                SpannableStringBuilder sb = new SpannableStringBuilder(item);
//                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(200, 0, 0));
//                sb.setSpan(fcs, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                textView.setText(sb, TextView.BufferType.SPANNABLE);
              textView.setText(item);
            }
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

}
