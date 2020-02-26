package com.hyunro.layout.location;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.location.LocationManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class LocSelectActivity extends AppCompatActivity
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
    final LocAdapter adapterSecond = new LocAdapter();
    final LocAdapter adapterThird = new LocAdapter();

    FragmentManager manager;
    Fragment_LocSelect fragment_LocSelect;
    Fragment_LocSearch fragment_LocSearch;
    EditText searchLocEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        // initialize asset manager
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

                // not null 인 경우가 훨씬 많으므로 분기 순위를 다르게 적용시키는게 연산이 더 빠르다.

                if (locMap.get(locFirst) != null) {
                    if (locMap.get(locFirst).get(locSecond) != null) { // 3번째는 그냥 put
                        locMap.get(locFirst).get(locSecond).put(locThird, locXY);
                    } else { // 두 번째가 null
                        locMap.get(locFirst).put(locSecond, new HashMap<String, String>());
                    }
                } else { // 첫 번째가 null
                    locMap.put(locFirst, new HashMap<String, Map<String, String>>());
                    // 두 번째, 첫 번째가 null인 행은 map에 자리만 만들어 놓고 넘어감.
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("여기까지3", e.getMessage());
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("여기까지4", e.getMessage());
            Toast.makeText(this, "AllException", Toast.LENGTH_SHORT).show();
        }


        // Search
        searchLocEdit = findViewById(R.id.detail_title);
        searchLocEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(getApplicationContext(), searchLocEdit.getText().toString(), Toast.LENGTH_SHORT).show();
                listSearchResult(searchLocEdit.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }
            @Override
            public void afterTextChanged(Editable s) {  }
        });

        // setOnFocusChangeListener
        searchLocEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(), locList.size()+"", Toast.LENGTH_SHORT).show();
                    manager.beginTransaction().replace(R.id.frameLocSelectSearch, fragment_LocSearch).addToBackStack(null).commit();
                    manager.executePendingTransactions();
                } else {
                    Toast.makeText(getApplicationContext(), "Lose Focus", Toast.LENGTH_SHORT).show();
                    manager.beginTransaction().remove(fragment_LocSearch).commit();
                }

            }
        });

        ImageButton backButton; // 뒤로가기 버튼▼
        backButton = findViewById(R.id.detail_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button locSetButton; // 완료 버튼▼
        locSetButton = findViewById(R.id.locSetButton);
        locSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setLocation();
            }
        });

        Button locFindButton; // GPS 버튼▼
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
//                    Toast.makeText(getApplicationContext(), "현재위치 \n위도 " + latitude + "\n경도 " + longitude+ " 주소 "+address, Toast.LENGTH_LONG).show();
                first = addressElements[1];
                second = addressElements[2];
                third = addressElements[3];
                setLocation();
                } else {
//                    Toast.makeText(getApplicationContext(), "위치 정보가 부정확합니다. 다른 방식으로 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageButton locSearchButton; // 돋보기 버튼▼
        locSearchButton = findViewById(R.id.locSearchButton);
        locSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listSearchResult(searchLocEdit.getText().toString());
            }
        });


        listFirstLoc();
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    public void setLocation(){
        if (first == null || second == null || third == null) {
            Toast.makeText(getApplicationContext(), "위치를 선택해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if(locMap.get(first).get(second).get(third)==null) {
                Toast.makeText(getApplicationContext(), "위치 정보가 부정확합니다. 다른 방식으로 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("firstLoc", first); //First라는 key값으로 infoFirst 데이터를 저장한다.
                editor.putString("secondLoc", second); //Second라는 key값으로 infoSecond 데이터를 저장한다.
                editor.putString("thirdLoc", third); //Second라는 key값으로 infoSecond 데이터를 저장한다.
                editor.putString("locXY", xy);
                editor.commit();
                Toast.makeText(getApplicationContext(), "set : " + first + "" + second + "" + third + "" + locMap.get(first).get(second).get(third), Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    public void listFirstLoc(){
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
                view.setBackgroundColor(Color.rgb(200, 0, 0));
                firstSelected = view;

                adapterThird.list.clear();
                adapterThird.notifyDataSetChanged();

                String item = adapterFirst.getItem(position);
                Toast.makeText(getApplicationContext(), "slct1 : " + item, Toast.LENGTH_SHORT).show();
                first = item;
                second = null;
                third = null;
                listSecondLoc();

            }
        });
    }

    public void listSecondLoc(){
        RecyclerView recyclerViewSecond = findViewById(R.id.secondLocRecycleLayout);
        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSecond.setLayoutManager(secondLayoutManager);
//        final LocAdapter adapterSecond = new LocAdapter();
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
                view.setBackgroundColor(Color.rgb(200,0,0));
                secondSelected = view;

                String item = adapterSecond.getItem(position);
                Toast.makeText(getApplicationContext(), "slct2 : "+first+item, Toast.LENGTH_SHORT).show();
                second = item;
                third = null;
                listThirdLoc();

            }
        });
    }
    public void listThirdLoc(){
        RecyclerView recyclerViewThird = findViewById(R.id.thirdLocRecycleLayout);
        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewThird.setLayoutManager(secondLayoutManager);
//        final LocAdapter adapterThird = new LocAdapter();
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
                view.setBackgroundColor(Color.rgb(200,0,0));
                thirdSelected = view;

                String item = adapterThird.getItem(position);
                Toast.makeText(getApplicationContext(), "slct3 : "+first+second+item, Toast.LENGTH_SHORT).show();
                third = item;
                xy = locMap.get(first).get(second).get(third);
            }
        });
    }

    public void listSearchResult(String keyword){
        RecyclerView recyclerViewSearch = findViewById(R.id.searchLocRecycleLayout);
        Toast.makeText(this, keyword, Toast.LENGTH_SHORT).show();
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearch.setLayoutManager(searchLayoutManager);
        final LocAdapter adapterSearch = new LocAdapter(keyword);
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
                Toast.makeText(getApplicationContext(), "slct3 : "+item, Toast.LENGTH_SHORT).show();
                String[] eachItem = item.split(" ");
                first = eachItem[0];
                second = eachItem[1];
                third = eachItem[2];
                xy = locMap.get(first).get(second).get(third);
                setLocation();
            }
        });
    }

    @Override
    public void onFragmentInteractionSelect(Uri uri) {}
    @Override
    public void onFragmentInteractionSearch(Uri uri) {}

    @Override
    public void onBackPressed(){
        finish();
    }


//    class GPSListener implements LocationListener {
//        public void onLocationChanged(Location location) {
//            Double latitude = location.getLatitude();
//            Double longitude = location.getLongitude();
//            Toast.makeText(getApplicationContext(), "Location... ltd : "+latitude+", lng : "+longitude, Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {   }
//        @Override
//        public void onProviderEnabled(String provider) {    }
//        @Override
//        public void onProviderDisabled(String provider) {   }
//    }

    @Override
    public void onDenied(int i, String[] strings) {
        Toast.makeText(this, "permissions denied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, String[] strings) {
        Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show();
    }

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getApplicationContext(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(getApplicationContext(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getApplicationContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Locale locale = new Locale("KOR", "GB");
        Geocoder geocoder = new Geocoder(this, locale);

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
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


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
