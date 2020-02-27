package com.hyunro.layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hyunro.layout.detail.DetailActivity;
import com.hyunro.layout.util.OnOutfitClickListener;
import com.hyunro.layout.util.OutfitAdapter;
import com.hyunro.layout.util.WeatherAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Fragment_201 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment_201() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_201 newInstance(String param1, String param2) {
        Fragment_201 fragment = new Fragment_201();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View temp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_201, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
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
        spread_Fragment_201_weather();
    }

    public void onStop(){
        super.onStop();

    }
    public void onResume(){
        super.onResume();
//        spread_Fragment_201();
    }

    RecyclerView todayWeatherRecyclerView;
    LinearLayoutManager layoutManager;
    WeatherAdapter weatherAdapter;

    public void spread_Fragment_201_weather(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.today.isEmpty()) return;
        Map<String, Map<String, Object>> today = mainActivity.today;

        Log.d("spread_Fragment_201", "today Map.keySet()? "+(mainActivity.today.keySet()));
        todayWeatherRecyclerView = mainActivity.findViewById(R.id.todayWeatherRecyclerView);
        layoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        todayWeatherRecyclerView.setLayoutManager(layoutManager);

        weatherAdapter = new WeatherAdapter(mainActivity);
        weatherAdapter.notifyDataSetChanged();
        Set set = today.keySet();
        List list = new ArrayList(set);
        Collections.sort(list);
        for(Object key : list) {
            weatherAdapter.addItem(today.get(key));
        }

        todayWeatherRecyclerView.setAdapter(weatherAdapter);
//        Log.d("sperad_Framgnet_201","weatherAdapter==null ? "+(weatherAdapter==null));



    }

    RecyclerView outfitRecyclerView;
    LinearLayoutManager layoutManagerOutfit;
    OutfitAdapter outfitAdapter;
    public void spread_Fragment_201_outfit(){
        MainActivity mainActivity = (MainActivity) getActivity();
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
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("documentId", documentId);
                intent.putExtra("senderActivity", "MainActivity");
                startActivity(intent);
//                Toast.makeText(getContext(), "아이템 선택됨 : "+item.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
