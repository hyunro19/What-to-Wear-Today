package com.hyunro.layout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hyunro.layout.location.LocSelectActivity;
import com.hyunro.layout.upload.UploadActivity;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements Fragment_1.OnFragmentInteractionListener,
        Fragment_201.OnFragmentInteractionListener,
        Fragment_202.OnFragmentInteractionListener,
        Fragment_203.OnFragmentInteractionListener,
        Fragment_3.OnFragmentInteractionListener,
        AutoPermissionsListener {

    Fragment_1 fragment_1;
    Fragment_201 fragment_201;
    Fragment_202 fragment_202;
    Fragment_203 fragment_203;
    Fragment_3 fragment_3;
    FragmentManager manager;
    String locXY;
    public static String firstLoc;

    public static Map<String, Map<String, Object>> outfit = new HashMap<>();
    Map<String, Object> yesterdayAM = new HashMap<>();
    Map<String, Object> yesterdayPM = new HashMap<>();
    Map<String, Map<String, Object>> today = new HashMap<>();
    Map<String, Object> tomorrowAM = new HashMap<>();
    Map<String, Object> tomorrowPM = new HashMap<>();

    String AM = "0600";
    String PM = "1500";

    String todayYoil;
    String tomorrowYoil;
    String yesterdayYoil;

    String todayDateAsString;
    String tomorrowDateAsString;
    String yesterdayDateAsString;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        token = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        manager = getSupportFragmentManager();
        fragment_1 = (Fragment_1) manager.findFragmentById(R.id.fragment1);
        fragment_201 = (Fragment_201) manager.findFragmentById(R.id.fragment2);
        fragment_3 = (Fragment_3) manager.findFragmentById(R.id.fragment3);
        // 중간 Fragment 전환
        fragment_202 = new Fragment_202();
        fragment_203 = new Fragment_203();

        ImageButton location_select;
        location_select = findViewById(R.id.locationSelectButton);
        location_select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocSelectActivity.class);
                startActivityForResult(intent, 112);
            }
        });

        ImageButton image_upload;
        image_upload = findViewById(R.id.uploadImage);
        image_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence[] items = {"카메라", "갤러리"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("오늘 나의 의상 공유하기")
                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index){
                                if(index == 0) {
                                    takePicture();
                                } else if (index == 1) {
                                    openGallery();
                                }
                            }
                        });
                builder.show();
            }
        });

        final View LeftBtmBtn;
        final View MidBtmBtn;
        final View RightBtmBtn;
        LeftBtmBtn = findViewById(R.id.LeftBtmBtn);
        MidBtmBtn = findViewById(R.id.MidBtmBtn);
        RightBtmBtn = findViewById(R.id.RightBtmBtn);

        LeftBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(fragment_201 != null) manager.beginTransaction().show(fragment_201).commit();
                if(fragment_202 != null) manager.beginTransaction().hide(fragment_202).commit();
                if(fragment_203 != null) manager.beginTransaction().hide(fragment_203).commit();
            }
        });

        MidBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!fragment_202.isAdded()) manager.beginTransaction().add(R.id.container, fragment_202).commit();

                if(fragment_201 != null) manager.beginTransaction().hide(fragment_201).commit();
                if(fragment_202 != null) manager.beginTransaction().show(fragment_202).commit();
                if(fragment_203 != null) manager.beginTransaction().hide(fragment_203).commit();

            }
        });

        RightBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!fragment_203.isAdded()) manager.beginTransaction().add(R.id.container, fragment_203).commit();

                if(fragment_201 != null) manager.beginTransaction().hide(fragment_201).commit();
                if(fragment_202 != null) manager.beginTransaction().hide(fragment_202).commit();
                if(fragment_203 != null) manager.beginTransaction().show(fragment_203).commit();
            }
        });

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        TimeZone timezone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timezone);
        String[] yoilArray = { "일", "월", "화", "수", "목", "금", "토" };

        Calendar calendar = Calendar.getInstance();
        Date todayDate = calendar.getTime();
        todayYoil = yoilArray[calendar.get(Calendar.DAY_OF_WEEK)-1];
        todayDateAsString = dateFormat.format(todayDate);

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDate = calendar.getTime();
        tomorrowYoil = yoilArray[calendar.get(Calendar.DAY_OF_WEEK)-1];
        tomorrowDateAsString = dateFormat.format(tomorrowDate);

        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date yesterdayDate = calendar.getTime();
        yesterdayYoil = yoilArray[calendar.get(Calendar.DAY_OF_WEEK)-1];
        yesterdayDateAsString = dateFormat.format(yesterdayDate);

        updateLocation();
        // → downloadWeatehrByThreeHours (today)
        // → downloadWeatehrForHalfDay (4times : YesterdayAM, YesterdayPM, TomorrowAM, TomorrowPM)
        // → downloadOutfitInfo → downloadOutfitPhoto

        agreeReceivingAlarm();
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    } // onCreate()

    @Override
    public void onStart() {
        super.onStart();
    }
    private String dateFormating(String dateAsString, String Yoil) {
        String month = dateAsString.substring(4,6);
        String day = dateAsString.substring(6,8);
        if(month.startsWith("0")) month = month.substring(1);
        if(day.startsWith("0")) day = day.substring(1);

        return month+"/"+day+" "+Yoil;
    }

    private void updateLocation() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        firstLoc = pref.getString("firstLoc", "서울특별시");
        String secondLoc = pref.getString("secondLoc", "중구");
        String thirdLoc = pref.getString("thirdLoc", "신당동");
        locXY = pref.getString("locXY", "60127");
        TextView currentLocation = findViewById(R.id.currentLocation);
        currentLocation.setText(firstLoc+" "+secondLoc+" "+thirdLoc);

        if(!firstLoc.equals("서울특별시")) {
            Toast.makeText(this, "현재 서울특별시의 날씨 정보만 제공됩니다.", Toast.LENGTH_LONG).show();
            return;
        }

        // after setting location, start downloading weather & outfit from FirebaseDB
        downloadWeatherByThreeHours();
        downloadWeatherForHalfDay("yesterdayAM", locXY, yesterdayDateAsString, AM, yesterdayAM);
        downloadWeatherForHalfDay("yesterdayPM", locXY, yesterdayDateAsString, PM, yesterdayPM);
        downloadWeatherForHalfDay("tomorrowAM", locXY, tomorrowDateAsString, AM, tomorrowAM);
        downloadWeatherForHalfDay("tomorrowPM", locXY, tomorrowDateAsString, PM, tomorrowPM);
        downloadOutfitInfo();
    }

    private void downloadWeatherByThreeHours() {
        db.collection("weather").document(locXY).collection(todayDateAsString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                if(document.getId().equals("0000")) data.put("fcstDate", dateFormating(todayDateAsString, todayYoil));
                                String fcstTime = document.getId().substring(0,2);
                                if(fcstTime.startsWith("0")) fcstTime = fcstTime.substring(1,2);
                                data.put("fcstTime",fcstTime);
                                today.put(document.getId(), data);
                            }
                            // weatherByThreeHours
                            fragment_201.spread_Fragment_201_weather();
                            if(fragment_202.isAdded()) fragment_202.spread_fragment_202_top_halfday("todayAM", today.get(AM), MainActivity.this);
                            if(fragment_202.isAdded()) fragment_202.spread_fragment_202_top_halfday("todayPM", today.get(PM), MainActivity.this);
                        } else {
                            Log.w("데이터 읽기", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void downloadWeatherForHalfDay(final String distinction, String locXY, String dateAsString, String ampm, Map<String, Object> inputMap) {
        final Map<String, Object> innerMap = inputMap;
        db.collection("weather").document(locXY).collection(dateAsString).document(ampm)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()!=null) {
                        for(String key : document.getData().keySet()){
                            innerMap.put(key, document.getData().get(key));
                        }
                    }
                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
                    if(fragment_202.isAdded()) fragment_202.spread_fragment_202_top_halfday(distinction, innerMap, MainActivity.this);
                } else {
                    Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                }
            }
        });
    }



    private void downloadOutfitInfo(){
        db.collection("outfit").orderBy("uploadDate", Query.Direction.DESCENDING).limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                outfit.put(document.getId(), data);
                            }
                            Log.w("Outfit Read", "Complete getting documents.", task.getException());
                            downloadOutfitPhoto();
                        } else {
                            Log.w("Outfit Read", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    int count;
    private void downloadOutfitPhoto(){
        for(String key : outfit.keySet()) {
            final String documentId = key;
            StorageReference islandRef = storageRef.child("outfitPhoto/"+documentId+".jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    outfit.get(documentId).put("photo", bitmap);
                    Log.d("downloadOutfitPhoto", "successful for "+documentId);
                    count += 1;
                    if(count == outfit.keySet().size()) {
                        count = 0;
                        fragment_201.spread_Fragment_201_outfit();
                        Log.d("downloadOutfitPhoto", "successful for all");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }

    // Fragment Change
    @Override
    public void onFragmentInteraction1(Uri uri) {}
    @Override
    public void onFragmentInteraction201(Uri uri) {}
    @Override
    public void onFragmentInteraction202(Uri uri) {}
    @Override
    public void onFragmentInteraction203(Uri uri) {}
    @Override
    public void onFragmentInteraction3(Uri uri) {}


    // Upload
    File file;
    private void takePicture() {
        if(file==null) file = createFile();

        Uri fileUri = FileProvider.getUriForFile(this, "com.hyunro.layout.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 102);
    }
    private File createFile() {
        String filename = new Date().toString()+"wtwt_ootd.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, filename);

        return outFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            // from Camera
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra("file", file);
            intent.putExtra("token", token);
            intent.putExtra("todayAM", (HashMap)today.get(AM));
            intent.putExtra("todayPM", (HashMap)today.get(PM));
            startActivity(intent);

        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            // from Gallery
            Intent intent = new Intent(this, UploadActivity.class);
            Uri fileUri = data.getData();
            intent.putExtra("fileUri", fileUri);
            intent.putExtra("token", token);
            intent.putExtra("todayAM", (HashMap)today.get(AM));
            intent.putExtra("todayPM", (HashMap)today.get(PM));
            startActivity(intent);

        } else if (requestCode == 112 && resultCode == RESULT_OK) {
            // after Location Select
            // 뒤로 버튼 눌렸을때는 RESULT_OK가 아니도록 설정
            updateLocation();
        }
    }

    private void agreeReceivingAlarm() {
        SharedPreferences defultPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor defaultEditor = defultPref.edit();

        Boolean isExists_agreeDailyAlarm = defultPref.contains("agreeDailyAlarm");
        if(!isExists_agreeDailyAlarm) {
            defaultEditor.putBoolean("agreeDailyAlarm", true);
        }
        Boolean isExists_agreeMarketingAlarm = defultPref.contains("agreeMarketingAlarm");
        if(!isExists_agreeMarketingAlarm) {
            defaultEditor.putBoolean("isExists_agreeMarketingAlarm", true);
        }
    }

    // Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }
    @Override
    public void onDenied(int requestCode, String[] permissions) {
        Log.d("MainActivity", "permissions denied : " + permissions.length);
    }
    @Override
    public void onGranted(int requestCode, String[] permissions) {
        Log.d("MainActivity", "permissions granted : " + permissions.length);
    }


    // When Exit
    private long lastTimeBackPressed;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this,"'뒤로' 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        String sampleToken = "1eGwRyYHF5dnbx557pWn9q4bzYf2";
        if(token.equals(sampleToken)) mAuth.signOut();
    }
}
