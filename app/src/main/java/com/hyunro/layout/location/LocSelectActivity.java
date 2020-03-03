package com.hyunro.layout.location;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunro.layout.R;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocSelectActivity
        extends AppCompatActivity
        implements Fragment_LocSelect.OnFragmentInteractionListener,
                    Fragment_LocSearch.OnFragmentInteractionListener,
                    AutoPermissionsListener {
    Map<String, Map<String, Map<String, String>>> locMap = new HashMap<>();
    List<String> locList = new ArrayList<>();
    String first = null;
    String second = null;
    String third = null;
    String xy = null;
    View firstSelected = null;
    View secondSelected = null;
    View thirdSelected = null;
    LocAdapter adapterSecond = new LocAdapter();
    LocAdapter adapterThird = new LocAdapter();

    FragmentManager manager;
    Fragment_LocSelect fragment_LocSelect;
    Fragment_LocSearch fragment_LocSearch;
    EditText searchLocEdit;

    private GpsTracker gpsTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        AssetManager assetManager = getAssets();

        manager = getSupportFragmentManager();
        fragment_LocSelect = (Fragment_LocSelect) manager.findFragmentById(R.id.fragment_location_select);
        fragment_LocSearch = new Fragment_LocSearch();
        try {
            InputStream inputData = assetManager.open("locationXY_combination.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputData, "UTF-8"));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;
                String[] tempContainer = new String[5];
                String[] lineSplit = line.split("&");
                if (lineSplit.length != 5) {
                    for (int i = 0; i < lineSplit.length; i++) {
                        tempContainer[i] = lineSplit[i];
                    }
                    lineSplit = tempContainer;
                } else {
                    locList.add(lineSplit[0]+" "+lineSplit[1]+" "+lineSplit[2]);
                }
                String locFirst = lineSplit[0];
                String locSecond = lineSplit[1];
                String locThird = lineSplit[2];
                String locXY = lineSplit[3] + lineSplit[4];

                if (locMap.get(locFirst) != null) {
                    if (locMap.get(locFirst).get(locSecond) != null) {
                        locMap.get(locFirst).get(locSecond).put(locThird, locXY);
                    } else {
                        locMap.get(locFirst).put(locSecond, new HashMap<String, String>());
                    }
                } else {
                    locMap.put(locFirst, new HashMap<String, Map<String, String>>());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Search
        searchLocEdit = findViewById(R.id.detail_title);
        searchLocEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {   }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }
            @Override
            public void afterTextChanged(Editable s) {
                listSearchResult(s.toString());
            }
        });

        // setOnFocusChangeListener
        searchLocEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if(!fragment_LocSearch.isAdded()) manager.beginTransaction().add(R.id.frameLocSelectSearch, fragment_LocSearch).commit();
                    if(fragment_LocSelect != null) manager.beginTransaction().hide(fragment_LocSelect).commit();
                    if(fragment_LocSearch != null) manager.beginTransaction().show(fragment_LocSearch).commit();
                } else {
                    if(fragment_LocSelect != null) manager.beginTransaction().show(fragment_LocSelect).commit();
                    if(fragment_LocSearch != null) manager.beginTransaction().hide(fragment_LocSearch).commit();
                }
            }
        });

        ImageButton backButton;
        backButton = findViewById(R.id.settings_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button locSetButton;
        locSetButton = findViewById(R.id.locSetButton);
        locSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setLocation();
            }
        });

        Button locFindButton;
        locFindButton = findViewById(R.id.locFindButton);
        locFindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gpsTracker = new GpsTracker(getApplicationContext());
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                if(address.startsWith("대한민국")){
                    String[] addressElements = address.split(" ");
                    first = addressElements[1];
                    second = addressElements[2];
                    third = addressElements[3];
                    setLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "위치 정보가 부정확합니다. 다른 방식으로 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        ImageButton locSearchButton = findViewById(R.id.locSearchButton);
//        locSearchButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String keyword = searchLocEdit.getText().toString();
//                if (keyword == null || keyword.equals("")) return;
//                listSearchResult(keyword);
//            }
//        });

        listFirstLoc();
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    private void setLocation(){
        if (first == null || second == null || third == null) {
            Toast.makeText(getApplicationContext(), "위치를 선택해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if(locMap.get(first).get(second).get(third)==null) {
                Toast.makeText(getApplicationContext(), "위치 정보가 부정확합니다. 다른 방식으로 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("firstLoc", first);
                editor.putString("secondLoc", second);
                editor.putString("thirdLoc", third);
                editor.putString("locXY", xy);
                editor.commit();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }

    private void listFirstLoc(){
        RecyclerView recyclerViewFirst = findViewById(R.id.firstLocRecycleLayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewFirst.setLayoutManager(layoutManager);
        final LocAdapter adapterFirst = new LocAdapter();
        for (String firstLoc : locMap.keySet()) {
            adapterFirst.addItem(firstLoc);
        }
        recyclerViewFirst.setAdapter(adapterFirst);

        adapterFirst.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LocAdapter.ViewHolder holder, View view, int position) {
                if (firstSelected != null) {
                    firstSelected.setBackgroundColor(0x00000000);
                }

                view.setBackgroundColor(Color.parseColor("#D81B60"));
                firstSelected = view;

                adapterThird.list.clear();
                adapterThird.notifyDataSetChanged();

                String item = adapterFirst.getItem(position);
                first = item;
                second = null;
                third = null;
                listSecondLoc();
            }
        });
    }

    private void listSecondLoc(){
        RecyclerView recyclerViewSecond = findViewById(R.id.secondLocRecycleLayout);
        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSecond.setLayoutManager(secondLayoutManager);
        adapterSecond.list.clear();
        for(String secondLoc : locMap.get(first).keySet()){
            adapterSecond.addItem(secondLoc);
        }
        recyclerViewSecond.setAdapter(adapterSecond);

        adapterSecond.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LocAdapter.ViewHolder holder, View view, int position) {
                if (secondSelected != null) {
                    secondSelected.setBackgroundColor(0x00000000);
                }
                view.setBackgroundColor(Color.parseColor("#D81B60"));
                secondSelected = view;

                String item = adapterSecond.getItem(position);
                second = item;
                third = null;
                listThirdLoc();

            }
        });
    }
    private void listThirdLoc(){
        RecyclerView recyclerViewThird = findViewById(R.id.thirdLocRecycleLayout);
        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewThird.setLayoutManager(secondLayoutManager);
        adapterThird.list.clear();
        for(String thirdLoc : locMap.get(first).get(second).keySet()){
            adapterThird.addItem(thirdLoc);
        }
        recyclerViewThird.setAdapter(adapterThird);

        adapterThird.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LocAdapter.ViewHolder holder, View view, int position) {
                if (thirdSelected != null) {
                    thirdSelected.setBackgroundColor(0x00000000);
                }
                view.setBackgroundColor(Color.parseColor("#D81B60"));
                thirdSelected = view;

                String item = adapterThird.getItem(position);
                third = item;
                xy = locMap.get(first).get(second).get(third);
            }
        });
    }

    RecyclerView recyclerViewSearch;
    LinearLayoutManager searchLayoutManager;
    LocAdapter adapterSearch;
    private void listSearchResult(String keyword){
        if(recyclerViewSearch==null || searchLayoutManager==null || adapterSearch==null) {
            recyclerViewSearch = findViewById(R.id.searchLocRecycleLayout);
            searchLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerViewSearch.setLayoutManager(searchLayoutManager);
            adapterSearch = new LocAdapter();
        }

        adapterSearch.clearItem();
        adapterSearch.setKeyword(keyword);
        for(String item : locList){
            if(item.contains(keyword)) {
                adapterSearch.addItem(item);
            }
        }
        recyclerViewSearch.setAdapter(adapterSearch);

        adapterSearch.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LocAdapter.ViewHolder holder, View view, int position) {
                String item = adapterSearch.getItem(position);
                String[] eachItem = item.split(" ");
                first = eachItem[0];
                second = eachItem[1];
                third = eachItem[2];
                xy = locMap.get(first).get(second).get(third);
                setLocation();
            }
        });
    }

    private String getCurrentAddress( double latitude, double longitude) {
        Locale locale = new Locale("KOR", "GB");
        Geocoder geocoder = new Geocoder(this, locale);

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }


    @Override
    public void onFragmentInteractionSelect(Uri uri) {}
    @Override
    public void onFragmentInteractionSearch(Uri uri) {}
    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onDenied(int i, String[] strings) {
        Log.d("LocSelectActivity", "permissions denied");
    }
    @Override
    public void onGranted(int i, String[] strings) {
        Log.d("LocSelectActivity", "permissions granted");
    }
}
