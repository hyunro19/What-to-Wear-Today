package com.hyunro.wtwt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunro.wtwt.detail.DetailActivity;
import com.hyunro.wtwt.util.OnOutfitClickListener;
import com.hyunro.wtwt.util.OutfitAdapter;
import com.hyunro.wtwt.util.WeatherAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Fragment_Mid1 extends Fragment {
    private OnFragmentInteractionListener mListener;

    public Fragment_Mid1() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_201, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction201(Uri uri);
    }

    public void onStart(){
        super.onStart();
    }

    public void onStop(){
        super.onStop();

    }
    public void onResume(){
        super.onResume();
    }

    RecyclerView todayWeatherRecyclerView;
    LinearLayoutManager layoutManagerWeather;
    WeatherAdapter weatherAdapter;

    public void spread_Fragment_201_weather(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.today.isEmpty()) return;
        Map<String, Map<String, Object>> today = mainActivity.today;

        todayWeatherRecyclerView = mainActivity.findViewById(R.id.todayWeatherRecyclerView);
        layoutManagerWeather = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        todayWeatherRecyclerView.setLayoutManager(layoutManagerWeather);

        weatherAdapter = new WeatherAdapter(mainActivity);
        weatherAdapter.notifyDataSetChanged();
        Set set = today.keySet();
        List list = new ArrayList(set);
        Collections.sort(list);
        for(Object key : list) {
            weatherAdapter.addItem(today.get(key));
        }

        todayWeatherRecyclerView.setAdapter(weatherAdapter);
    }

    RecyclerView outfitRecyclerView;
    LinearLayoutManager layoutManagerOutfit;
    OutfitAdapter outfitAdapter;
    public void spread_Fragment_201_outfit(){
        final MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.outfit.isEmpty()) return;
        Map<String, Map<String, Object>> outfit = mainActivity.outfit;
        outfitRecyclerView = mainActivity.findViewById(R.id.outfitRecyclerView);
        layoutManagerOutfit = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        outfitRecyclerView.setLayoutManager(layoutManagerOutfit);

        outfitAdapter = new OutfitAdapter(mainActivity);
        outfitAdapter.notifyDataSetChanged();
        Set set = outfit.keySet();
        List list = new ArrayList(set);
        Collections.sort(list);
        for(Object key : list) {
            outfitAdapter.addItem(outfit.get(key));
        }
        outfitRecyclerView.setAdapter(outfitAdapter);

        outfitAdapter.setOnOutfitClickListener(new OnOutfitClickListener() {
            @Override
            public void onOutfitClick(OutfitAdapter.ViewHolder holder, View view, int position) {
                HashMap<String, Object> info = (HashMap)outfitAdapter.getItem(position);
                String documentId = (String)info.get("documentId");
                Intent intent = new Intent(mainActivity, DetailActivity.class);
                intent.putExtra("documentId", documentId);
                intent.putExtra("senderActivity", "MainActivity");
                startActivity(intent);
            }
        });
    }

}
