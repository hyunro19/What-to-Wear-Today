package com.hyunro.layout.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyunro.layout.MainActivity;
import com.hyunro.layout.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String token;
    String email;
    String displayName;
    String gender;
    int year;
    int month;
    int day;
    String nickname;
    EditText editTextNickname;
    DatePicker datePickerDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        token = currentUser.getUid();
        email = currentUser.getEmail();
        displayName = currentUser.getDisplayName();

//        TextView textViewUid = findViewById(R.id.register_uid);
//        textViewUid.append("UID : "+uid);

        TextView textViewEmail = findViewById(R.id.register_email);
        textViewEmail.setText(email);

        TextView textViewDisplayName = findViewById(R.id.register_displayName);
        textViewDisplayName.setText(displayName);

        editTextNickname = findViewById(R.id.register_nickname);


        datePickerDateOfBirth = findViewById(R.id.register_dateOfBirthPicker);




        RadioGroup register_genderRadioGroup = (RadioGroup) findViewById(R.id.register_genderRadioGroup);
        register_genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.register_genderRadioMale:
                        gender = "M";
                        break;
                    case R.id.register_genderRadioFemale:
                        gender = "F";
                        break;
                }
            }
        });



        Button cancelButton = findViewById(R.id.register_cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "cancelButton", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                finish();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
            }
        });
        Button completeButton = findViewById(R.id.register_completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nickname = editTextNickname.getText().toString();
                year = datePickerDateOfBirth.getYear();
                month= datePickerDateOfBirth.getMonth()+1;
                day  = datePickerDateOfBirth.getDayOfMonth();

                if(nickname == null || nickname.equals("") ) {
                    Toast.makeText(RegisterActivity.this, "닉네임을 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (gender == null) {
                    Toast.makeText(RegisterActivity.this, "성별을 선택해주세요."+nickname, Toast.LENGTH_SHORT).show();
                    return;
                }


                String dateOfBirth = ""+year;
                if(month<10) dateOfBirth +="0";
                dateOfBirth += month;
                if(day<10) dateOfBirth +="0";
                dateOfBirth += day;

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> user = new HashMap<>();
                user.put("nickname", nickname);
                user.put("dateOfBirth", dateOfBirth);
                user.put("gender", gender);
                user.put("RegistrationDate", new Date());

                db.collection("users").document(token)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("RegisterActivity", "DocumentSnapshot successfully written!");
                            Toast.makeText(RegisterActivity.this, "등록 성공 : dateOfBirth : "+year+"/"+month+"/"+day+"\n성별 : "+gender+"\n닉네임 : "+nickname, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "등록 실패", Toast.LENGTH_SHORT).show();
                        }
                    });



            }
        });

    }
    @Override
    public void onBackPressed(){
        if(mAuth.getCurrentUser() != null) mAuth.signOut();
        finish();
    }
}
