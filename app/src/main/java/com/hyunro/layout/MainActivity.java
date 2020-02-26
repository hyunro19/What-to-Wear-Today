package com.hyunro.layout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hyunro.layout.location.LocSelectActivity;
import com.hyunro.layout.upload.UploadActivity;
import com.hyunro.layout.util.WeatherAdapter;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    FirebaseAuth mAuth;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        token = mAuth.getCurrentUser().getUid();
        Log.d("MainActivity","in onCreate() method, token = "+token);

        manager = getSupportFragmentManager();
        fragment_1 = (Fragment_1) manager.findFragmentById(R.id.fragment1);
        fragment_201 = (Fragment_201) manager.findFragmentById(R.id.fragment2);
        fragment_3 = (Fragment_3) manager.findFragmentById(R.id.fragment3);
        // 중간 Fragment 전환
        fragment_202 = new Fragment_202();
        fragment_203 = new Fragment_203();






        ImageButton location_select; // 위치 설정 버튼▼
        location_select = findViewById(R.id.locationSelectButton);
        location_select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocSelectActivity.class);
                // 현재 위치 정보 가져가서 select default로 두는 것 고려
                startActivity(intent);
            }
        });

        ImageButton image_upload;
        image_upload = findViewById(R.id.uploadImage);
        image_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence[] items = {"카메라", "갤러리"};
                // context에 getapplicationContext()를 하니까 오류 나는데 MainActivity.this하니까 오류 안나내;;; 왜지???
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("오늘 나의 의상 공유하기")
                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index){
                                if(index == 0) {
                                    // 카메라, intent는 MainActivity -> 카메라 -> 다시 MainActivity -> UploadActivity 순서 (액티비티간 이동이므로)
                                    takePicture();
                                } else if (index == 1) {
                                    // 갤러리, intent는 MainActivity -> 갤러리 -> 다시 MainActivity -> UploadActivity 순서 (액티비티간 이동이므로)
                                    openGallery();
                                }
                                Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
        });

        // 하단 버튼 Fragment 전환
        final ImageButton LeftBtmBtn;
        LeftBtmBtn = findViewById(R.id.LeftBtmBtn);
        LeftBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "LeftBtmBtn", Toast.LENGTH_SHORT).show();
                if(fragment_201 != null) {
                    manager.beginTransaction().show(fragment_201).commit();
                }
                if(fragment_202 != null) {
                    manager.beginTransaction().hide(fragment_202).commit();
                }
                if(fragment_203 != null) {
                    manager.beginTransaction().hide(fragment_203).commit();
                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_201).commit();
            }
        });
        final ImageButton MidBtmBtn;
        MidBtmBtn = findViewById(R.id.MidBtmBtn);
        MidBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!fragment_202.isAdded()) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_202).commit();
                    Log.d("Check added", "fragment202 added");
                }
                Toast.makeText(MainActivity.this, "MidBtmBtn", Toast.LENGTH_SHORT).show();
                if(fragment_201 != null) {
                    manager.beginTransaction().hide(fragment_201).commit();
                }
                if(fragment_202 != null) {
                    manager.beginTransaction().show(fragment_202).commit();
                }
                if(fragment_203 != null) {
                    manager.beginTransaction().hide(fragment_203).commit();
                }

            }
        });
        final ImageButton RightBtmBtn;
        RightBtmBtn = findViewById(R.id.RightBtmBtn);
        RightBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!fragment_203.isAdded()) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_203).commit();
                    Log.d("Check added", "fragment203 added");
                }
                Toast.makeText(MainActivity.this, "RightBtmBtn", Toast.LENGTH_SHORT).show();
                if(fragment_201 != null) {
                    manager.beginTransaction().hide(fragment_201).commit();
                }
                if(fragment_202 != null) {
                    manager.beginTransaction().hide(fragment_202).commit();
                }
                if(fragment_203 != null) {
                    manager.beginTransaction().show(fragment_203).commit();
                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_203).commit();
            }
        });

        TextView readDBtest = findViewById(R.id.readDBtest);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        TimeZone timezone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timezone);

        String[] yoilArray = { "일", "월", "화", "수", "목", "금", "토" };
        Calendar calendar = Calendar.getInstance();
        Date todayDate = calendar.getTime();
        todayYoil = yoilArray[calendar.get(Calendar.DAY_OF_WEEK)-1];
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDate = calendar.getTime();
        tomorrowYoil = yoilArray[calendar.get(Calendar.DAY_OF_WEEK)-1];
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date yesterdayDate = calendar.getTime();
        yesterdayYoil = yoilArray[calendar.get(Calendar.DAY_OF_WEEK)-1];
        Log.d("요일", yesterdayYoil+"\t"+todayYoil+"\t"+tomorrowYoil);

        todayDateAsString = dateFormat.format(todayDate);
        tomorrowDateAsString = dateFormat.format(tomorrowDate);
        yesterdayDateAsString = dateFormat.format(yesterdayDate);

//        가로세로 dp 구하기
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics ();
//        display.getMetrics(outMetrics);
//        float density  = getResources().getDisplayMetrics().density;
//        float dpHeight = outMetrics.heightPixels / density;
//        float dpWidth  = outMetrics.widthPixels / density;
//        Log.d("TAGTAG : ", "dpHeight : "+dpHeight);
//        Log.d("TAGTAG : ", "dpWidth : "+dpWidth);



        AutoPermissions.Companion.loadAllPermissions(this, 101);
    } // onCreate()

    public String dateFormating(String dateAsString, String Yoil) {
        String month = dateAsString.substring(4,6);
        String day = dateAsString.substring(6,8);
        if(month.startsWith("0")) month = month.substring(1);
        if(day.startsWith("0")) day = day.substring(1);

        return month+"/"+day+" "+Yoil;
    }


    FirebaseFirestore db;
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String firstLoc = pref.getString("firstLoc", "서울특별시");
        String secondLoc = pref.getString("secondLoc", "중구");
        String thirdLoc = pref.getString("thirdLoc", "신당동");
        locXY = pref.getString("locXY", "60127");
        TextView currentLocation = findViewById(R.id.currentLocation);
        currentLocation.setText(firstLoc+" "+secondLoc+" "+thirdLoc);


        db = FirebaseFirestore.getInstance();
        // outfit data 가져오기
        // filtering 필요
        db.collection("outfit").orderBy("uploadDate", Query.Direction.DESCENDING).limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.w("Outfit Read", "Complete getting documents.", task.getException());
                                Map<String, Object> data = document.getData();
                                outfit.put(document.getId(), data);
                            }

                            // weatherByThreeHours
                            downloadOutfitPhoto();

                        } else {
                            Log.w("Outfit Read", "Error getting documents.", task.getException());
                        }
                    }
                });



        // 오늘 날씨 3시간 단위로 읽어오기
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

                } else {
                    Log.w("데이터 읽기", "Error getting documents.", task.getException());
                }
                }
            });

        db.collection("weather").document(locXY).collection(yesterdayDateAsString).document(AM)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()!=null) {
                        for(String key : document.getData().keySet()){
                            yesterdayAM.put(key, document.getData().get(key));
                        }
                    }
                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
                    Log.d("fragment202 null? : ", (yesterdayAM.keySet())+"");
//                    fragment_202.spread_fragment_202_yesterday();
                } else {
                    Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                }
            }
        });
        db.collection("weather").document(locXY).collection(yesterdayDateAsString).document(PM)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()!=null) {
                        for(String key : document.getData().keySet()){
                            yesterdayPM.put(key, document.getData().get(key));
                        }
                    }


                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
                } else {
                    Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                }
            }
        });
        db.collection("weather").document(locXY).collection(tomorrowDateAsString).document(AM)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()!=null) {
                        for(String key : document.getData().keySet()){
                            tomorrowAM.put(key, document.getData().get(key));
                        }
                    }
                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
                } else {
                    Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                }
            }
        });
        db.collection("weather").document(locXY).collection(tomorrowDateAsString).document(PM)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData() != null ) {
                        for(String key : document.getData().keySet()){
                            tomorrowPM.put(key, document.getData().get(key));
                        }
                    }
                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
                } else {
                    Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                }
            }
        });



    }

    RecyclerView todayWeatherRecyclerView;
    LinearLayoutManager layoutManager;
    WeatherAdapter weatherAdapter;

    public void spread_Fragment_201(){
//        Log.d("sperad_Framgnet_201","weatherAdapter==null ? "+(weatherAdapter==null));
        todayWeatherRecyclerView = findViewById(R.id.todayWeatherRecyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        todayWeatherRecyclerView.setLayoutManager(layoutManager);
        weatherAdapter = new WeatherAdapter(this);
        Set set = today.keySet();
        List list = new ArrayList(set);
        Collections.sort(list);
        for(Object key : list) {
//            Log.d((String)key, today.get(key)+"spread_Fragment_201 in MainActivity");
            weatherAdapter.addItem(today.get(key));
        }
        todayWeatherRecyclerView.setAdapter(weatherAdapter);
    }

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

    File file;
    public void takePicture() {
        if(file==null) {
            file = createFile();
        }

        Uri fileUri = FileProvider.getUriForFile(this, "com.hyunro.layout.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }
    public void openGallery() {
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
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra("file", file);
            intent.putExtra("token", token);
            intent.putExtra("todayAM", (HashMap)today.get(AM));
            intent.putExtra("todayPM", (HashMap)today.get(PM));
            startActivity(intent);

        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, UploadActivity.class);
            Uri fileUri = data.getData();
            intent.putExtra("fileUri", fileUri);
            intent.putExtra("token", token);
            intent.putExtra("todayAM", (HashMap)today.get(AM));
            intent.putExtra("todayPM", (HashMap)today.get(PM));
            startActivity(intent);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onGranted(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(token.equals("")) mAuth.signOut();
    }


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    int count;
    public void downloadOutfitPhoto(){
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
}
