package com.hyunro.layout.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyunro.layout.MainActivity;
import com.hyunro.layout.R;

import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    View progressBar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        View googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(currentUser == null) {
                    signIn();
                } else {
                    token = currentUser.getUid();
                    checkCustomUserDataExists(token);
                }
            }
        });

        View sampleSignInButton = findViewById(R.id.sampleSignInButton);
        sampleSignInButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                sampleSignIn();
                return false;
            }
        });

        setProgressBar(R.id.progressBar);
    }

    DocumentSnapshot document;
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null)
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            return;
            // nudge to click SignInButton
        } else {
            token = currentUser.getUid();
            checkCustomUserDataExists(token);
            // If customerUserData exists, move on to MainActivity
            // If not, pop up RegisterActivity
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressBar();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        currentUser = mAuth.getCurrentUser();
                        token = currentUser.getUid();
                        checkCustomUserDataExists(token);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }

                        hideProgressBar();
                }
            });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sampleSignIn() {
        showProgressBar();

        mAuth.signInWithEmailAndPassword("mulcam1305@gmail.com", "mc13051305!")
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "TEST계정으로 로그인하셨습니다.", Toast.LENGTH_LONG).show();

                    currentUser = mAuth.getCurrentUser();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    hideProgressBar();
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "잠시 후 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                    hideProgressBar();

                }
                }
            });
    }

    private void checkCustomUserDataExists(String token) {
        showProgressBar();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(token)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    // Custom User Data Exists
                    Log.d("At LoginActivity, ", "Read User Data From FirebaseDB : " + document.getData());
                    hideProgressBar();
                    if(document.getData() != null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // Custom User Data X -> Registration Required
                    Log.d("At LoginActivity, ", "Cached get failed: ", task.getException());
                    hideProgressBar();
                }
            }
        });
    }


    public void setProgressBar(int resId) {
        progressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}

