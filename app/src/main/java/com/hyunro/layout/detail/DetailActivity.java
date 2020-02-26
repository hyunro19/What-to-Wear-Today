package com.hyunro.layout.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyunro.layout.MainActivity;
import com.hyunro.layout.R;
import com.hyunro.layout.util.WeatherAdapter;

import java.util.Map;

import static com.hyunro.layout.MainActivity.outfit;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String documentId = (String)bundle.get("documentId");
        Map<String, Object> info = outfit.get(documentId);
        // info를 ui에 뿌려주면 된다.


        ImageButton detail_backButton = findViewById(R.id.detail_backButton);
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
        TextView detail_location = findViewById(R.id.detail_location);

        detail_nickname.setText((String)info.get("nickname"));
        detail_ageGender.setText((String)info.get("gender")+"/"+info.get("age"));
        detail_location.setText((String)info.get("location"));

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
