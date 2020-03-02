package com.hyunro.layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.hyunro.layout.mypage.MyOutfitsActivity;
import com.hyunro.layout.mypage.UserUpdateActivity;
import com.hyunro.layout.util.SettingsActivity;


public class Fragment_203 extends Fragment {

    private OnFragmentInteractionListener mListener;

    public Fragment_203() {

    }

    public static Fragment_203 newInstance(String param1, String param2) {
        Fragment_203 fragment = new Fragment_203();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_203, container, false);
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
        void onFragmentInteraction203(Uri uri);
    }
    @Override
    public void onStart() {
        super.onStart();
        View fragment203_myInfo = getActivity().findViewById(R.id.fragment203_myInfo);
        fragment203_myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserUpdateActivity.class);
                startActivity(intent);
            }
        });

        View fragment203_myOutfits = getActivity().findViewById(R.id.fragment203_myOutfits);
        fragment203_myOutfits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                String token = mainActivity.token;
                Intent intent = new Intent(getActivity(), MyOutfitsActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        View fragment203_settings = getActivity().findViewById(R.id.fragment203_settings);
        fragment203_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
