package com.hyunro.wtwt.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyunro.wtwt.MainActivity;
import com.hyunro.wtwt.R;
import com.hyunro.wtwt.mypage.MyOutfitsActivity;
import com.hyunro.wtwt.util.WeatherAdapter;

import java.util.Date;
import java.util.Map;

import static com.hyunro.wtwt.MainActivity.outfit;

public class DetailActivity extends AppCompatActivity {
    String documentId;
    String token = MainActivity.token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View detail_delete = findViewById(R.id.detail_delete);
        detail_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("의상 게시물 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (token.equals("1eGwRyYHF5dnbx557pWn9q4bzYf2")) {
                                    Toast.makeText(DetailActivity.this, "TEST계정의 게시물은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("outfit").document(documentId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(DetailActivity.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DetailActivity.this, "잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }})
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                            }})
                        .show();

                return false;
            }
        });


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        documentId = (String)bundle.get("documentId");
        String senderActivity = (String)bundle.get("senderActivity");
        Map<String, Object> info = outfit.get(documentId);
        if (senderActivity.equals("MyOutfitsActivity")) {
            detail_delete.setVisibility(View.VISIBLE);
            info = MyOutfitsActivity.myOutfit.get(documentId);
        }


        // info를 ui에 뿌려주면 된다.

        ImageButton detail_backButton = findViewById(R.id.settings_backButton);
        detail_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView detail_image = findViewById(R.id.detail_image);
        detail_image.setImageBitmap((Bitmap)info.get("photo"));

        TextView detail_nickname = findViewById(R.id.detail_nickname);
        TextView detail_ageGender = findViewById(R.id.detail_ageGender);
        TextView detail_date = findViewById(R.id.detail_date);
        TextView detail_location = findViewById(R.id.detail_location);

        detail_nickname.setText((String)info.get("nickname"));
        String gender = "남";
        if(info.get("gender").equals("F")) gender = "여";
        detail_ageGender.setText(info.get("age")+"/"+gender);
        detail_location.setText((String)info.get("location"));
        String[] yoilArray = { "일", "월", "화", "수", "목", "금", "토" };
        Date uploadDate = ((Timestamp)info.get("uploadDate")).toDate();
        detail_date.setText(uploadDate.getMonth()+1+"/"+uploadDate.getDate()+" "+yoilArray[uploadDate.getDay()]);

        Map<String, String> skyText = WeatherAdapter.skyText;
        ImageView detail_amSkyImage = findViewById(R.id.detail_amSkyImage);
        TextView detail_amSkyText = findViewById(R.id.detail_amSkyText);
        TextView detail_amTemp = findViewById(R.id.detail_amTemp);
        TextView detail_amHumidity = findViewById(R.id.detail_amHumidity);

        ImageView detail_pmSkyImage = findViewById(R.id.detail_pmSkyImage);
        TextView detail_pmSkyText = findViewById(R.id.detail_pmSkyText);
        TextView detail_pmTemp = findViewById(R.id.detail_pmTemp);
        TextView detail_pmHumidity = findViewById(R.id.detail_pmHumidity);

        String detail_amSkyCode = (String)info.get("AM_SKY")+(String)info.get("AM_PTY");
        int detail_amSkyImageId = getResources().getIdentifier( "ic_weather_1"+detail_amSkyCode, "drawable",this.getPackageName());
        detail_amSkyImage.setImageResource(detail_amSkyImageId);
        detail_amSkyText.setText(skyText.get(detail_amSkyCode));
        detail_amTemp.setText(info.get("AM_TMN").toString()+"˚C");
        detail_amHumidity.setText(info.get("AM_REH").toString()+"%");

        String detail_pmSkyCode = (String)info.get("PM_SKY")+(String)info.get("PM_PTY");
        int detail_pmSkyImageId = getResources().getIdentifier( "ic_weather_1"+detail_pmSkyCode, "drawable",this.getPackageName());
        detail_pmSkyImage.setImageResource(detail_pmSkyImageId);
        detail_pmSkyText.setText(skyText.get(detail_pmSkyCode));
        detail_pmTemp.setText(info.get("PM_TMX").toString()+"˚C");
        detail_pmHumidity.setText(info.get("PM_REH").toString()+"%");


        TextView detail_outer = findViewById(R.id.detail_outer);
        TextView detail_top = findViewById(R.id.detail_top);
        TextView detail_bottom = findViewById(R.id.detail_bottom);
        TextView detail_shoes = findViewById(R.id.detail_shoes);
        TextView detail_description = findViewById(R.id.detail_description);
        detail_outer.setText((String)info.get("outer"));
        detail_top.setText((String)info.get("top"));
        detail_bottom.setText((String)info.get("bottom"));
        detail_shoes.setText((String)info.get("shoes"));
        detail_description.setText((String)info.get("description"));



    }

    @Override
    public void onBackPressed(){
        finish();
    }







}
