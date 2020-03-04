package com.hyunro.layout.upload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hyunro.layout.R;
import com.hyunro.layout.util.WeatherAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    String locXY;
    String token;
    String nickname;
    String dateOfBirth;
    String gender;
    Map<String, String> todayAM;
    Map<String, String> todayPM;
    EditText upload_description;
    TextView countCurrentText;
    Bitmap bitmap;
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.get("filePath") != null) {
            File filePath = (File)bundle.get("filePath");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream instream = null;
            try {
                instream = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(instream);
                ImageView imageView = findViewById(R.id.detail_image);
                imageView.setImageBitmap(bitmap);
                instream.close();
            } catch ( Exception e ) {
                e.printStackTrace();
            }


        } else if (bundle.get("fileUri") != null) {
            Uri fileUri;
            fileUri = (Uri)bundle.get("fileUri");

            ContentResolver resolver = getContentResolver();
            InputStream instream = null;
            try {
                instream = resolver.openInputStream(fileUri);
                bitmap = BitmapFactory.decodeStream(instream);
                ImageView imageView = findViewById(R.id.detail_image);
                imageView.setImageBitmap(bitmap);
                instream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageButton backButton;
        backButton = findViewById(R.id.settings_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button uploadButton;
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadOutfit();
            }
        });


        Spinner spinnerOuter = findViewById(R.id.detail_spinner_outer);
        String[] itemsOuter = {"코트", "블레이저", "가죽자켓", "숏패딩", "롱패딩", "가디건", "정장자켓"};
        ArrayAdapter<String> adapterOuter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,itemsOuter);
        adapterOuter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOuter.setAdapter(adapterOuter);

        Spinner spinnerTop = findViewById(R.id.detail_spinner_top);
        String[] itemsTop = {"정장셔츠", "캐쥬얼셔츠", "맨투맨", "후드티", "반팔티셔츠", "민소매"};
        ArrayAdapter<String> adapterTop = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,itemsTop);
        adapterTop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTop.setAdapter(adapterTop);

        Spinner spinnerBottom = findViewById(R.id.detail_spinner_bottom);
        String[] itemsBottom = {"긴_청바지", "반_청바지", "긴_면바지", "긴_반바지", "긴_린넨바지", "반_린넨바지", "짧은치마", "긴치마"};
        ArrayAdapter<String> adapterBottom = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,itemsBottom);
        adapterBottom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBottom.setAdapter(adapterBottom);

        Spinner spinnerShoes = findViewById(R.id.detail_spinner_shoes);
        String[] itemsShoes = {"운동화", "부츠", "슬리퍼", "쪼리", "가죽구두", "하이힐", "단화", "로퍼"};
        ArrayAdapter<String> adapterShoes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,itemsShoes);
        adapterShoes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShoes.setAdapter(adapterShoes);


        upload_description = findViewById(R.id.detail_description);
        upload_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {   }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }
            @Override
            public void afterTextChanged(Editable s) {
                int countText = upload_description.getText().length();
                countCurrentText = findViewById(R.id.countCurrentText);
                countCurrentText.setText(countText+"");
            }
        });

    }

    String location;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String firstLoc = pref.getString("firstLoc", "서울특별시");
        String secondLoc = pref.getString("secondLoc", "중구");
        String thirdLoc = pref.getString("thirdLoc", "신당동");
        location = firstLoc+" "+secondLoc+" "+thirdLoc;
        locXY = pref.getString("locXY", "60127");
        TextView locationContent = findViewById(R.id.upload_location);
        locationContent.setText(location);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString("token");
        todayAM = (Map)bundle.getSerializable("todayAM");
        todayPM = (Map)bundle.getSerializable("todayPM");

        Map<String, String> skyText = WeatherAdapter.skyText;

        ImageView upload_amSkyImage = findViewById(R.id.detail_amSkyImage);
        String todayAMSkyCode = todayAM.get("SKY")+todayAM.get("PTY");
        int todayAMSkyImageId = getResources().getIdentifier( "ic_weather_1"+todayAMSkyCode, "drawable",this.getPackageName());
        upload_amSkyImage.setImageResource(todayAMSkyImageId);
        TextView upload_amSkyText = findViewById(R.id.detail_amSkyText);

        upload_amSkyText.setText(skyText.get(todayAMSkyCode));
        TextView upload_amTemp = findViewById(R.id.detail_amTemp);
        upload_amTemp.setText(todayAM.get("TMN")+"˚C");
        TextView upload_amHumidity = findViewById(R.id.detail_amHumidity);
        upload_amHumidity.setText(todayAM.get("REH")+"%");

        ImageView upload_pmSkyImage = findViewById(R.id.detail_pmSkyImage);
        String todayPMSkyCode = todayAM.get("SKY")+todayAM.get("PTY");
        int todayPMSkyImageId = getResources().getIdentifier( "ic_weather_1"+todayPMSkyCode, "drawable",this.getPackageName());
        upload_pmSkyImage.setImageResource(todayPMSkyImageId);
        TextView upload_pmSkyText = findViewById(R.id.detail_pmSkyText);

        upload_pmSkyText.setText(skyText.get(todayPMSkyCode));
        TextView upload_pmTemp = findViewById(R.id.detail_pmTemp);
        upload_pmTemp.setText(todayPM.get("TMX")+"˚C");
        TextView upload_pmHumidity = findViewById(R.id.detail_pmHumidity);
        upload_pmHumidity.setText(todayAM.get("REH")+"%");



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(token)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Custom User Data Exists
                    nickname = document.getString("nickname");
                    gender = document.getString("gender");
                    dateOfBirth = document.getString("dateOfBirth");

                    Calendar now =  Calendar.getInstance();
                    int nowYear = now.get(Calendar.YEAR);
                    int nowMonth = now.get(Calendar.MONTH)+1;
                    int nowDay = now.get(Calendar.DAY_OF_MONTH);
                    int birthYear = Integer.parseInt(dateOfBirth.substring(0,4));
                    int birthMonth = Integer.parseInt(dateOfBirth.substring(4,6));
                    int birthDay = Integer.parseInt(dateOfBirth.substring(6,8));

                    age = nowYear-birthYear;
                    if(nowMonth<birthMonth) age -=1;
                    if(nowMonth==birthMonth && nowDay<birthDay) age -= 1;

                    TextView nicknameTextView = findViewById(R.id.detail_nickname);
                    nicknameTextView.setText(nickname);
                    TextView ageGenderTextView = findViewById(R.id.detail_ageGender);
                    String genderText = "남성";
                    if(gender.equals("F")) genderText = "여성";
                    ageGenderTextView.setText(age+"세/"+genderText);

                } else {
                    Log.d("UploadActivity", "Failed to read custom user data", task.getException());

                }
            }
        });

    } // onStart();

    public void uploadOutfit(){
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ref = format.format(now)+"_"+token;

        Map<String, Object> data = new HashMap<>();
        // user info
        data.put("uid",token);
        data.put("nickname", nickname);
        data.put("gender", gender);
        data.put("age",age);

        // outfit info
        String outer = ((Spinner)findViewById(R.id.detail_spinner_outer)).getSelectedItem().toString();
        String top = ((Spinner)findViewById(R.id.detail_spinner_top)).getSelectedItem().toString();
        String bottom = ((Spinner)findViewById(R.id.detail_spinner_bottom)).getSelectedItem().toString();
        String shoes = ((Spinner)findViewById(R.id.detail_spinner_shoes)).getSelectedItem().toString();
        String description = ((TextView)findViewById(R.id.detail_description)).getText().toString();
        data.put("outer",outer);
        data.put("top",top);
        data.put("bottom",bottom);
        data.put("shoes",shoes);
        data.put("description",description);

        // document info
        data.put("documentId", ref);
        data.put("uploadDate", new Date());

        // weather info
        data.put("locXY", locXY);
        data.put("location", location);
        // x랑 y를 따로 저장해서 int로 해야 인근 범위를 비교할 수 있겠다...
        // 아니면 String 두개로 쪼개서 parseInt +-1값까지 == 비교를 해줘야...
        // (x==17 || x==18 || x==19) 이런 식으로
        data.put("AM_SKY",todayAM.get("SKY"));
        data.put("AM_PTY",todayAM.get("PTY"));
        data.put("AM_REH",Double.parseDouble(todayAM.get("REH")));
        data.put("AM_TMN",Double.parseDouble(todayAM.get("TMN")));

        data.put("PM_SKY",todayPM.get("SKY"));
        data.put("PM_PTY",todayPM.get("PTY"));
        data.put("PM_REH",Double.parseDouble(todayPM.get("REH")));
        data.put("PM_TMX",Double.parseDouble(todayPM.get("TMX")));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("outfit").document(ref)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UploadActivity", "Successfully wrote outfit info");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UploadActivity", "Failed to write outfit info");
                    }
                });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference outfitPhotoRef = storageRef.child("outfitPhoto/"+ref+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();
        UploadTask uploadTask = outfitPhotoRef.putBytes(photoData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UploadActivity", "Successfully wrote outfit photo");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("UploadActivity", "Failed to write outfit photo");
                finish();
            }
        });

    }



}
