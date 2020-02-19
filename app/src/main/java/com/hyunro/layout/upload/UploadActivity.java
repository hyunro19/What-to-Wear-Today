package com.hyunro.layout.upload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hyunro.layout.R;
import com.hyunro.layout.location.LocSelectActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class UploadActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.get("file") != null) {
            File file;
            file = (File)bundle.get("file");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            ImageView imageView = findViewById(R.id.imageToUpload);
            imageView.setImageBitmap(bitmap);
        } else if (bundle.get("fileUri") != null) {
            Uri fileUri;
            fileUri = (Uri)bundle.get("fileUri");

            ContentResolver resolver = getContentResolver();
            InputStream instream = null;
            try {
                instream = resolver.openInputStream(fileUri);
                Bitmap bitmap = BitmapFactory.decodeStream(instream);
                ImageView imageView = findViewById(R.id.imageToUpload);
                imageView.setImageBitmap(bitmap);
                instream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageButton backButton; // 뒤로가기 버튼▼
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton locSelectButton; // 위치 설정 버튼▼
        locSelectButton = findViewById(R.id.locationSelectButton);
        locSelectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocSelectActivity.class);
                // 현재 위치 정보 가져가서 select default로 두는 것 고려
                startActivity(intent);
            }
        });

        Button cancelButton; // 취소 버튼▼
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button uploadButton; // 업로드 버튼▼
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(UploadActivity.this, "Upload Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        String[] items = {"mike", "angel", "crow", "john", "ginnie", "sally", "cohen", "rice"};

        Spinner spinnerTest = findViewById(R.id.spinnerTest);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTest.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String firstLoc = pref.getString("firstLoc", "서울특별시");
        String secondLoc = pref.getString("secondLoc", "중구");
        String thirdLoc = pref.getString("thirdLoc", "신당동");
//        String locXY = pref.getString("locXY", "60127");
        TextView locationContent = findViewById(R.id.locationContent);
        locationContent.setText(firstLoc+" "+secondLoc+" "+thirdLoc);
    }


}
