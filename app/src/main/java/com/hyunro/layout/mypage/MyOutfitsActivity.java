package com.hyunro.layout.mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hyunro.layout.MainActivity;
import com.hyunro.layout.R;
import com.hyunro.layout.detail.DetailActivity;
import com.hyunro.layout.util.OnOutfitClickListener;
import com.hyunro.layout.util.OutfitAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyOutfitsActivity extends AppCompatActivity {

    public static Map<String, Map<String, Object>> myOutfit = new HashMap<>();
    String token;
    Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myoutfits);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = (String)bundle.get("token");
        query = db.collection("outfit").whereEqualTo("uid", token).orderBy("uploadDate",Query.Direction.DESCENDING).limit(1);
        downloadOutfitInfo(token);


        View myoutfits_backButton = findViewById(R.id.myoutfits_backButton);
        myoutfits_backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View myoutfits_readMore = findViewById(R.id.myoutfits_readMore);
        myoutfits_readMore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                downloadOutfitInfo(token);
                return false;
            }
        });
    }






    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // outfit data 가져오기
    // filtering 필요
    public void downloadOutfitInfo(String token) {
        final String tempToken = token;
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // Get the last visible document
                        List<DocumentSnapshot> datas = documentSnapshots.getDocuments();
                        Map<String, Map<String, Object>> tempOutfit = new HashMap<>();
                        for(DocumentSnapshot data : datas) {
                            Log.d("paging : ", data.getData().get("documentId")+"");
                            Map<String, Object> map = data.getData();
                            tempOutfit.put(data.getId(), map);
                            myOutfit.put(data.getId(), map);
                        }
                        downloadOutfitPhoto(tempOutfit);
                        // Construct a new query starting at this document,
                        // get the next 25 cities.
                        try {
                            DocumentSnapshot lastVisible = datas.get(documentSnapshots.size() -1);
                            query = db.collection("outfit").whereEqualTo("uid", tempToken).orderBy("uploadDate",Query.Direction.DESCENDING)
                                    .startAfter(lastVisible)
                                    .limit(1);
                            findViewById(R.id.myOutfitsEmpty).setVisibility(View.GONE);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Toast.makeText(MyOutfitsActivity.this, "읽어올 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                        }


                        // Use the query for pagination
                        // ...
                    }
                });
    }
//    public void downloadOutfitInfo(String token) {
//        query.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.w("Outfit Read", "Complete getting documents.", task.getException());
//                                Map<String, Object> data = document.getData();
//                                myOutfit.put(document.getId(), data);
//                            }
//
//                            // weatherByThreeHours
//                            downloadOutfitPhoto();
//
//                        } else {
//                            Log.w("Outfit Read", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    int count;
    public void downloadOutfitPhoto(Map<String, Map<String, Object>> tempMap){
        final Map<String, Map<String, Object>> tempOutfit = tempMap;
        for(String key : tempOutfit.keySet()) {
            final String documentId = key;
            StorageReference islandRef = storageRef.child("outfitPhoto/"+documentId+".jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    tempOutfit.get(documentId).put("photo", bitmap);
                    Log.d("downloadOutfitPhoto", "successful for "+documentId);
                    count += 1;
                    if(count == tempOutfit.keySet().size()) {
                        count = 0;
                        spread_outfit(tempOutfit);
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

    RecyclerView outfitRecyclerView;
    LinearLayoutManager layoutManagerOutfit;
    OutfitAdapter outfitAdapter;
    public void spread_outfit(Map<String, Map<String, Object>> tempOutfit){
        if(tempOutfit.isEmpty()) return;
        if(outfitRecyclerView == null) outfitRecyclerView = findViewById(R.id.myoutfitsRecyclerView);
        if(layoutManagerOutfit == null) {
            layoutManagerOutfit = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            outfitRecyclerView.setLayoutManager(layoutManagerOutfit);
        }
        if(outfitAdapter == null) {
            outfitAdapter = new OutfitAdapter(this);
            outfitRecyclerView.setAdapter(outfitAdapter);
        }
        for(String key : tempOutfit.keySet()) {
            outfitAdapter.addItem(tempOutfit.get(key));
        }
        outfitAdapter.notifyDataSetChanged();
//        Set set = myOutfit.keySet();
//        List list = new ArrayList(set);
//        Collections.sort(list);
//        for(Object key : list) {
//            outfitAdapter.addItem(myOutfit.get(key));
//        }


        outfitAdapter.setOnOutfitClickListener(new OnOutfitClickListener() {
            @Override
            public void onOutfitClick(OutfitAdapter.ViewHolder holder, View view, int position) {
                HashMap<String, Object> info = (HashMap)outfitAdapter.getItem(position);
                String documentId = (String)info.get("documentId");
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("documentId", documentId);
                intent.putExtra("senderActivity", "MyOutfitsActivity");
                startActivity(intent);
//              Toast.makeText(getContext(), "아이템 선택됨 : "+item.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed(){
        finish();
    }

}
