package com.example.tourbud5.TouristPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.tourbud5.R;
import com.example.tourbud5.Adaptors.TourListingAdaptor;
import com.example.tourbud5.model.Tour;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchByTag extends AppCompatActivity {
    TourListingAdaptor adapter;
    RecyclerView recyclerView;
    private final ArrayList<Tour> list=new ArrayList<>();
    private final ArrayList<String> idList=new ArrayList<>();
    private String TAG="searchByTag";
    private String tag;
    private TextView tagName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_tag);
        Intent intent = getIntent();
        tag = intent.getStringExtra("tag");
        tagName=findViewById(R.id.tagName);
        tagName.setText(tag);

        recyclerView = (RecyclerView)findViewById(R.id.searchByTagRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchByTag.this));
        recyclerView.setHasFixedSize(true);

        //get data from firestore
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        dbRef.whereArrayContains("tags",tag )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "successfully fetched by tag");


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idList.add(document.getId());
                                list.add(document.toObject(Tour.class));
                                Log.d(TAG, document.getId() + " => " + document.toObject(Tour.class));
                            }
                            //pass list data to adaptor to bind data to the recycler view
                            adapter= new TourListingAdaptor(list,idList, getApplication(),SearchByTag.this, ViewTour.class);
                            recyclerView.setAdapter(adapter);




                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        Log.d(TAG, "num elements"+list.size());







    }
}