package com.hyunro.layout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    TextView yesterdayTextView;// = findViewById(R.id.yesterdayTextView);
    TextView todayTextView;// = findViewById(R.id.todayTextView);
    TextView tomorrowTextView;// = findViewById(R.id.tomorrowTextView);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        fragment_1 = (Fragment_1) manager.findFragmentById(R.id.fragment1);
        fragment_201 = (Fragment_201) manager.findFragmentById(R.id.fragment2);
        fragment_3 = (Fragment_3) manager.findFragmentById(R.id.fragment3);
        // 중간 Fragment 전환
        fragment_202 = new Fragment_202();
        fragment_203 = new Fragment_203();
        final Map<String, Object> yesterdayAM = new HashMap<>();
        final Map<String, Object> yesterdayPM = new HashMap<>();
        final Map<String, Map<String, Object>> today = new HashMap<>();
        final Map<String, Object> tomorrowAM = new HashMap<>();
        final Map<String, Object> tomorrowPM = new HashMap<>();
        String AM = "0300";
        String PM = "1500";





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
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_201).commit();
            }
        });
        final ImageButton MidBtmBtn;
        MidBtmBtn = findViewById(R.id.MidBtmBtn);
        MidBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "MidBtmBtn", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_202).commit();
            }
        });
        final ImageButton RightBtmBtn;
        RightBtmBtn = findViewById(R.id.RightBtmBtn);
        RightBtmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "RightBtmBtn", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_203).commit();
            }
        });



        TextView readDBtest = findViewById(R.id.readDBtest);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        TimeZone timezone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(timezone);

        Calendar calendar = Calendar.getInstance();
        Date todayDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date yesterdayDate = calendar.getTime();

        String todayDateAsString = dateFormat.format(todayDate);
        String tomorrowDateAsString = dateFormat.format(tomorrowDate);
        String yesterdayDateAsString = dateFormat.format(yesterdayDate);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String firstLoc = pref.getString("firstLoc", "서울특별시");
        String secondLoc = pref.getString("secondLoc", "중구");
        String thirdLoc = pref.getString("thirdLoc", "신당동");
        locXY = pref.getString("locXY", "60127");
        TextView locSelected = findViewById(R.id.locSelected);
        locSelected.setText(firstLoc+" "+secondLoc+" "+thirdLoc);

        yesterdayTextView = findViewById(R.id.yesterdayTextView);
        todayTextView = findViewById(R.id.todayTextView);
        tomorrowTextView = findViewById(R.id.tomorrowTextView);



        // 오늘 날씨 3시간 단위로 읽어오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("weather").document(locXY).collection(todayDateAsString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                today.put(document.getId(), document.getData());
                            }
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
                    for(String key : document.getData().keySet()){
                        yesterdayAM.put(key, document.getData().get(key));
                    }
                    spread(yesterdayTextView, yesterdayAM);
                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
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
                    for(String key : document.getData().keySet()){
                        yesterdayPM.put(key, document.getData().get(key));
                    }
                    spread(yesterdayTextView, yesterdayPM);
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
                    for(String key : document.getData().keySet()){
                        tomorrowAM.put(key, document.getData().get(key));
                        Log.d("tomorrowAM input", "key : "+ key);
                    }
                    spread(tomorrowTextView, tomorrowAM);
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
                    for(String key : document.getData().keySet()){
                        tomorrowPM.put(key, document.getData().get(key));
                    }
                    spread(tomorrowTextView, tomorrowPM);
                    Log.d("ReadFromFirebase : ", "Cached document data: " + document.getData());
                } else {
                    Log.d("ReadFromFirebase : ", "Cached get failed: ", task.getException());
                }
            }
        });




//        for(String key : yesterdayAM.keySet()){
//            yesterdayTextView.append(yesterdayAM.keySet().size()+"");
//            yesterdayTextView.append("AM : "+key+" : "+yesterdayAM.get(key));
//        }
//        for(String key : yesterdayPM.keySet()){
//            yesterdayTextView.append("PM : "+key+" : "+yesterdayPM.get(key));
//        }
//        for(String key : tomorrowAM.keySet()){
//            yesterdayTextView.append("AM : "+key+" : "+tomorrowAM.get(key));
//        }
//        for(String key : tomorrowPM.keySet()){
//            yesterdayTextView.append("PM : "+key+" : "+tomorrowPM.get(key));
//        }


//
//        todayTextView.append();
//
//        tomorrowTextView.append();





        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }
    protected void spread(TextView textview, Map<String, Object> map) {
        for(String key : map.keySet()){
            textview.append("AM : "+key+" : "+map.get(key));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

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

    ImageView imageView;
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
            startActivity(intent);

        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, UploadActivity.class);
            Uri fileUri = data.getData();
            intent.putExtra("fileUri", fileUri);
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
}
