package com.hyunro.wtwt.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hyunro.wtwt.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
    Boolean isNicknameOnly = false;
    Boolean isNicknameAvailable = false;

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
        editTextNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {   }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }
            @Override
            public void afterTextChanged(Editable s) {
                isNicknameOnly = false;
                if(!Pattern.matches("^[a-zA-Z0-9]{4,12}$", s)) {
                    ((TextView)findViewById(R.id.register_nickname_availabilityCheck)).setTextColor(Color.parseColor("#D81B60"));
                    isNicknameAvailable = false;
                } else {
                    ((TextView)findViewById(R.id.register_nickname_availabilityCheck)).setTextColor(Color.parseColor("#000000"));
                    isNicknameAvailable = true;
                }
            }
        });

        View register_nickname_checkButton = findViewById(R.id.register_nickname_checkButton);
        register_nickname_checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = editTextNickname.getText().toString();
                if(!isNicknameAvailable) {
                    Toast.makeText(RegisterActivity.this, "유효한 닉네임을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("nickname", nickname)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().isEmpty()){
                                        isNicknameOnly = true;
                                        Toast.makeText(RegisterActivity.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        isNicknameOnly = false;
                                        Toast.makeText(RegisterActivity.this, "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        datePickerDateOfBirth = findViewById(R.id.register_dateOfBirthPicker);
        datePickerDateOfBirth.init(1990, 0, 1,null);



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

        View backButton = findViewById(R.id.register_backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) mAuth.signOut();
                finish();
            }
        });
        View completeButton = findViewById(R.id.register_completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nickname = editTextNickname.getText().toString();
                year = datePickerDateOfBirth.getYear();
                month= datePickerDateOfBirth.getMonth()+1;
                day  = datePickerDateOfBirth.getDayOfMonth();


                if(!isNicknameAvailable) {
                    Toast.makeText(RegisterActivity.this, "유효한 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isNicknameOnly) {
                    Toast.makeText(RegisterActivity.this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (gender == null) {
                    Toast.makeText(RegisterActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, "가입을 환영합니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "가입에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
