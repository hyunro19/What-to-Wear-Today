package com.hyunro.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hyunro.layout.util.WeatherAdapter;

import java.util.HashMap;
import java.util.Map;

import static com.hyunro.layout.MainActivity.firstLoc;

public class Fragment_202 extends Fragment {

    private OnFragmentInteractionListener mListener;

    public Fragment_202() {

    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_202 newInstance(String param1, String param2) {
        Fragment_202 fragment = new Fragment_202();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_202, container, false);
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
        void onFragmentInteraction202(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity mainActivity = (MainActivity)getActivity();
        Toast.makeText(mainActivity, "Fragment_202 onStart()", Toast.LENGTH_SHORT).show();

        View fragment_202_commercial = mainActivity.findViewById(R.id.fragment_202_commercial);
        fragment_202_commercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "광고문의 : hyunro91@gmail.com", Toast.LENGTH_SHORT).show();
            }
        });
        spread_fragment_202_label(mainActivity);

        spread_fragment_202_top_halfday("yesterdayAM", mainActivity.yesterdayAM, mainActivity);
        spread_fragment_202_top_halfday("yesterdayPM", mainActivity.yesterdayPM, mainActivity);
        spread_fragment_202_top_halfday("todayAM", mainActivity.today.get("0600"), mainActivity);
        spread_fragment_202_top_halfday("todayPM", mainActivity.today.get("1500"), mainActivity);
        spread_fragment_202_top_halfday("tomorrowAM", mainActivity.tomorrowAM, mainActivity);
        spread_fragment_202_top_halfday("tomorrowPM", mainActivity.tomorrowPM, mainActivity);

        spread_fragment_202_bottom(mainActivity);
    }

    private String dateFormating(String dateAsString, String Yoil) {
        String month = dateAsString.substring(4,6);
        String day = dateAsString.substring(6,8);
        if(month.startsWith("0")) month = month.substring(1);
        if(day.startsWith("0")) day = day.substring(1);

        return month+"/"+day+" "+Yoil;
    }

    public void spread_fragment_202_top_halfday(String distinction, Map<String, Object> inputMap, MainActivity mainActivity) {
        // distinction == yesterdayAM, todayPM, etc.
        int SkyImageViewId = getViewId(distinction+"Sky");
        int skyTextViewId = getViewId(distinction+"SkyText");
        int tempTextViewId = getViewId(distinction+"Temp");

        ImageView skyImageView = mainActivity.findViewById(SkyImageViewId);
        TextView skyTextView = mainActivity.findViewById(skyTextViewId);
        TextView tempTextView = mainActivity.findViewById(tempTextViewId);

        String skyCode = (String)inputMap.get("SKY")+(String)inputMap.get("PTY");
        int skyImageId = getResources().getIdentifier( "ic_weather_1"+skyCode, "drawable",getActivity().getPackageName());

        skyImageView.setImageResource(skyImageId);
        skyTextView.setText(WeatherAdapter.skyText.get(skyCode));
        tempTextView.setText((String)inputMap.get("TMN")+"˚C");
        if (distinction.contains("PM")) tempTextView.setText((String)inputMap.get("TMX")+"˚C");

    }
    private int getViewId(String viewName) {
        int id = getResources().getIdentifier(viewName, "id",getActivity().getPackageName());
        return id;
    }

    public void spread_fragment_202_bottom(MainActivity mainActivity) {

        String yesterdayDateAsString = mainActivity.yesterdayDateAsString;
        String todayDateAsString = mainActivity.todayDateAsString;
        Map<String, String> map = new HashMap<>();
        map.put("today", todayDateAsString);
        map.put("yesterday", yesterdayDateAsString);

        StorageReference storageRef = mainActivity.storageRef;
        FirebaseFirestore db = mainActivity.db;

        for(String key : map.keySet()) {

            // Read Photo from Storage
            final String temp = key;
            String token = mainActivity.token;
            StorageReference ref = storageRef.child("outfitPhoto/" + map.get(key) + "_" + token + ".jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    int outfitImage = getContext().getResources().getIdentifier(temp + "OutfitImage", "id", getContext().getPackageName());
                    ImageView myOutfitPhoto = getActivity().findViewById(outfitImage);
                    myOutfitPhoto.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("loglog", "spread_fragment_202_bottom");
                }
            });

            // Read Information from Database
            db.collection("outfit").document(map.get(key) + "_" + token)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.getData()!=null) {
                            Map<String, Object> data = document.getData();
                            String[] array = {"Outer", "Top", "Bottom", "Shoes"};
                            for(String item : array) {
                                int viewId = getContext().getResources().getIdentifier(temp + "Outfit"+item, "id", getContext().getPackageName());
                                TextView textView = getActivity().findViewById(viewId);
                                textView.setText((String)data.get(item.toLowerCase()));
                            }
                        }
                    } else {
                        Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                    }
                }
            });
        }
    }

    private void spread_fragment_202_label(MainActivity mainActivity) {
        String yesterdayDateAsString = mainActivity.yesterdayDateAsString;
        String yesterdayYoil = mainActivity.yesterdayYoil;
        TextView yesterdayDate = mainActivity.findViewById(R.id.yesterdayDate);
        yesterdayDate.setText(dateFormating(yesterdayDateAsString, yesterdayYoil));

        String todayDateAsString = mainActivity.todayDateAsString;
        String todayYoil = mainActivity.todayYoil;
        TextView todayDate = mainActivity.findViewById(R.id.todayDate);
        todayDate.setText(dateFormating(todayDateAsString, todayYoil));

        String tomorrowDateAsString = mainActivity.tomorrowDateAsString;
        String tomorrowYoil = mainActivity.tomorrowYoil;
        TextView tomorrowDate = mainActivity.findViewById(R.id.tomorrowDate);
        tomorrowDate.setText(dateFormating(tomorrowDateAsString, tomorrowYoil));
    }
}
