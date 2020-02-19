package com.hyunro.layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.hyunro.layout.util.WeatherAdapter;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_202.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_202#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_202 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment_202() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_202 newInstance(String param1, String param2) {
        Fragment_202 fragment = new Fragment_202();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_202, container, false);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction202(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity mainActivity = (MainActivity)getActivity();
        Map<String, Object> yesterdayAM = mainActivity.yesterdayAM;
        Map<String, Object> yesterdayPM = mainActivity.yesterdayPM;
        Map<String, Map<String, Object>> today = mainActivity.today;
        Map<String, Object> tomorrowAM = mainActivity.tomorrowAM;
        Map<String, Object> tomorrowPM = mainActivity.tomorrowPM;

        Map<String, String> skyText = WeatherAdapter.skyText;
//        Map<String, String> skyText = new HashMap<String, String>() {{
//            put("10", "맑음");
//            put("11", "비");
//            put("12","비/눈");
//            put("13","눈");
//            put("14","소나기");
//            put("30","구름많음");
//            put("31","구름많고\n비");
//            put("32","구름많고\n비/눈");
//            put("33","구름많고\n눈");
//            put("34","구름많고\n소나기");
//            put("40","흐림");
//            put("41","흐리고\n비");
//            put("42","흐리고\n비/눈");
//            put("43","흐리고\n눈");
//            put("44","흐리고\n소나기");
//        }};

        // 어제
        String yesterdayDateAsString = mainActivity.yesterdayDateAsString;
        String yesterdayYoil = mainActivity.yesterdayYoil;
        TextView yesterdayDate = mainActivity.findViewById(R.id.yesterdayDate);
        yesterdayDate.setText(dateFormating(yesterdayDateAsString, yesterdayYoil));

        ImageView yesterdayAMSky = mainActivity.findViewById(R.id.yesterdayAMSky);
        String yesterdayAMSkyCode = (String)yesterdayAM.get("SKY")+(String)yesterdayAM.get("PTY");
        int yesterdayAMSkyImageId = getResources().getIdentifier( "ic_weather_1"+yesterdayAMSkyCode, "drawable",getActivity().getPackageName());
        yesterdayAMSky.setImageResource(yesterdayAMSkyImageId);
        TextView yesterdayAMSkyText = mainActivity.findViewById(R.id.yesterdayAMSkyText);
        yesterdayAMSkyText.setText(skyText.get(yesterdayAMSkyCode));
        TextView yesterdayAMTemp = mainActivity.findViewById(R.id.yesterdayAMTemp);
        yesterdayAMTemp.setText((String)yesterdayAM.get("TMN")+"˚C");

        ImageView yesterdayPMSky = mainActivity.findViewById(R.id.yesterdayPMSky);
        String yesterdayPMSkyCode = (String)yesterdayPM.get("SKY")+(String)yesterdayPM.get("PTY");
        int yesterdayPMSkyImageId = getResources().getIdentifier( "ic_weather_1"+yesterdayPMSkyCode, "drawable",getActivity().getPackageName());
        yesterdayPMSky.setImageResource(yesterdayPMSkyImageId);
        TextView yesterdayPMSkyText = mainActivity.findViewById(R.id.yesterdayPMSkyText);
        yesterdayPMSkyText.setText(skyText.get(yesterdayPMSkyCode));
        TextView yesterdayPMTemp = mainActivity.findViewById(R.id.yesterdayPMTemp);
        yesterdayPMTemp.setText((String)yesterdayPM.get("TMX")+"˚C");


        // 오늘
        String todayDateAsString = mainActivity.todayDateAsString;
        String todayYoil = mainActivity.todayYoil;
        TextView todayDate = mainActivity.findViewById(R.id.todayDate);
        todayDate.setText(dateFormating(todayDateAsString, todayYoil));
        Map<String, Object> todayAM = today.get("0600");
        Map<String, Object> todayPM = today.get("1500");


        ImageView todayAMSky = mainActivity.findViewById(R.id.todayAMSky);
        TextView todayAMSkyText = mainActivity.findViewById(R.id.todayAMSkyText);
        String todayAMSkyCode = (String)todayAM.get("SKY")+(String)todayAM.get("PTY");
        int todayAMSkyImageId = getResources().getIdentifier( "ic_weather_1"+todayAMSkyCode, "drawable",getActivity().getPackageName());
        todayAMSky.setImageResource(todayAMSkyImageId);
        todayAMSkyText.setText(skyText.get(todayAMSkyCode));
        TextView todayAMTemp = mainActivity.findViewById(R.id.todayAMTemp);
        todayAMTemp.setText((String)todayAM.get("TMN")+"˚C");

        ImageView todayPMSky = mainActivity.findViewById(R.id.todayPMSky);
        TextView todayPMSkyText = mainActivity.findViewById(R.id.todayPMSkyText);
        String todayPMSkyCode = (String)todayPM.get("SKY")+(String)todayPM.get("PTY");
        int todayPMSkyImageId = getResources().getIdentifier( "ic_weather_1"+todayPMSkyCode, "drawable",getActivity().getPackageName());
        todayPMSky.setImageResource(todayPMSkyImageId);
        todayPMSkyText.setText(skyText.get(todayPMSkyCode));
        TextView todayPMTemp = mainActivity.findViewById(R.id.todayPMTemp);
        todayPMTemp.setText((String)todayPM.get("TMX")+"˚C");


        // 내일
        String tomorrowDateAsString = mainActivity.tomorrowDateAsString;
        String tomorrowYoil = mainActivity.tomorrowYoil;
        TextView tomorrowDate = mainActivity.findViewById(R.id.tomorrowDate);
        tomorrowDate.setText(dateFormating(tomorrowDateAsString, tomorrowYoil));

        ImageView tomorrowAMSky = mainActivity.findViewById(R.id.tomorrowAMSky);
        TextView tomorrowAMSkyText = mainActivity.findViewById(R.id.tomorrowAMSkyText);
        String tomorrowAMSkyCode = (String)tomorrowAM.get("SKY")+(String)tomorrowAM.get("PTY");
        int tomorrowAMSkyImageId = getResources().getIdentifier( "ic_weather_1"+tomorrowAMSkyCode, "drawable",getActivity().getPackageName());
        tomorrowAMSky.setImageResource(tomorrowAMSkyImageId);
        tomorrowAMSkyText.setText(skyText.get(tomorrowAMSkyCode));
        TextView tomorrowAMTemp = mainActivity.findViewById(R.id.tomorrowAMTemp);
        tomorrowAMTemp.setText((String)tomorrowAM.get("TMN")+"˚C");

        ImageView tomorrowPMSky = mainActivity.findViewById(R.id.tomorrowPMSky);
        TextView tomorrowPMSkyText = mainActivity.findViewById(R.id.tomorrowPMSkyText);
        String tomorrowPMSkyCode = (String)tomorrowPM.get("SKY")+(String)tomorrowPM.get("PTY");
        int tomorrowPMSkyImageId = getResources().getIdentifier( "ic_weather_1"+tomorrowPMSkyCode, "drawable",getActivity().getPackageName());
        tomorrowPMSky.setImageResource(tomorrowPMSkyImageId);
        tomorrowPMSkyText.setText(skyText.get(tomorrowPMSkyCode));
        TextView tomorrowPMTemp = mainActivity.findViewById(R.id.tomorrowPMTemp);
        tomorrowPMTemp.setText((String)tomorrowPM.get("TMX")+"˚C");


    }

    private String dateFormating(String dateAsString, String Yoil) {
        String month = dateAsString.substring(4,6);
        String day = dateAsString.substring(6,8);
        if(month.startsWith("0")) month = month.substring(1);
        if(day.startsWith("0")) day = day.substring(1);

        return month+"/"+day+" "+Yoil;
    }

}
