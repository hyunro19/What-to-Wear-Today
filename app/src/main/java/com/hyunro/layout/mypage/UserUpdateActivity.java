package com.hyunro.layout.mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.hyunro.layout.R;
import com.hyunro.layout.login.RegisterActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserUpdateActivity extends AppCompatActivity {
    Map<String, Object> userInfo;
    EditText userUpdate_nickname;
    DatePicker datePickerDateOfBirth;
    RadioGroup userUpdate_genderRadioGroup;
    int year;
    int month;
    int day;
    String nickname;
    String gender;
    FirebaseFirestore db;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        String displayName = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        token = currentUser.getUid();

        TextView userUpdate_email = findViewById(R.id.userUpdate_email);
        TextView userUpdate_displayName = findViewById(R.id.userUpdate_displayName);
        userUpdate_email.setText(email);
        if(displayName != null && !displayName.equals("") ) userUpdate_displayName.setText(displayName);

        userUpdate_nickname = findViewById(R.id.userUpdate_nickname);
        datePickerDateOfBirth = findViewById(R.id.userUpdate_dateOfBirthPicker);
        userUpdate_genderRadioGroup = findViewById(R.id.userUpdate_genderRadioGroup);


        db = FirebaseFirestore.getInstance();
        db.collection("users").document(token)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    userInfo = document.getData();

                    String nickname = (String)userInfo.get("nickname");
                    userUpdate_nickname.setText(nickname);

                    String dateOfBirth = (String)userInfo.get("dateOfBirth");;
                    int year = Integer.parseInt( dateOfBirth.substring(0,4) );
                    int month = Integer.parseInt( dateOfBirth.substring(4,6) )-1;
                    int day = Integer.parseInt( dateOfBirth.substring(6,8) );
                    datePickerDateOfBirth.init(year, month, day,null);

                    int genderId = getResources().getIdentifier( "userUpdate_genderRadioMale", "id",getApplicationContext().getPackageName());
                    if(userInfo.get("gender").equals("F")) genderId = getResources().getIdentifier( "userUpdate_genderRadioFemale", "id",getApplicationContext().getPackageName());
                    userUpdate_genderRadioGroup.check(genderId);

                    Log.d("Success Read : ", "UserUpdateActivity : " + document.getData());
                } else {
                    Log.d("Failure Read : ", "UserUpdateActivity : " , task.getException());
                }
            }
        });

        RadioGroup userUpdate_genderRadioGroup = (RadioGroup) findViewById(R.id.userUpdate_genderRadioGroup);
        userUpdate_genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.userUpdate_genderRadioMale:
                        gender = "M";
                        break;
                    case R.id.userUpdate_genderRadioFemale:
                        gender = "F";
                        break;
                }
            }
        });

        // Update Button
        View userUpdate_completeButton = findViewById(R.id.userUpdate_completeButton);
        userUpdate_completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = userUpdate_nickname.getText().toString();
                year = datePickerDateOfBirth.getYear();
                month= datePickerDateOfBirth.getMonth()+1;
                day  = datePickerDateOfBirth.getDayOfMonth();

                if(nickname == null || nickname.equals("") ) {
                    Toast.makeText(UserUpdateActivity.this, "닉네임을 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (gender == null) {
                    Toast.makeText(UserUpdateActivity.this, "성별을 선택해주세요."+nickname, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(UserUpdateActivity.this, "등록 성공 : dateOfBirth : "+year+"/"+month+"/"+day+"\n성별 : "+gender+"\n닉네임 : "+nickname, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserUpdateActivity.this, "등록 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



        // Upper Back Button
        View userUpdate_backButton = findViewById(R.id.userUpdate_backButton);
        userUpdate_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lower Back Button(Cancel)
        View userUpdate_cancelButton = findViewById(R.id.userUpdate_cancelButton);
        userUpdate_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
