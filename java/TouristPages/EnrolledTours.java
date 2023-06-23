package com.example.tourbud5.TouristPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.Adaptors.TourListingAdaptor;
import com.example.tourbud5.model.Tour;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EnrolledTours extends AppCompatActivity {
    private String TAG="enrolledTours";

    //for recyclerview
    RecyclerView enrolledToursRecyclerView;
    TourListingAdaptor adapter;

    //to store data fetched
    private final ArrayList<Tour> list=new ArrayList<>();
    private final ArrayList<String> idList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_tours);

        enrolledToursRecyclerView=findViewById(R.id.enrolledToursRecyclerView);

        enrolledToursRecyclerView.setLayoutManager(new LinearLayoutManager(EnrolledTours.this));
        enrolledToursRecyclerView.setHasFixedSize(true);

        String uid= SharedPreferenceManager.getUserId(getApplicationContext());


        //get data from firestore
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        dbRef.whereArrayContains("rsvp",SharedPreferenceManager.getUserId(getApplicationContext()) )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "successfully fetched ");


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idList.add(document.getId());
                                list.add(document.toObject(Tour.class));
                                Log.d(TAG, document.getId() + " => " + document.toObject(Tour.class));
                            }



                           //pass  list data to adaptor to bind data to the recycler view
                            adapter= new TourListingAdaptor(list,idList, getApplication(), EnrolledTours.this, ViewTour.class);
                            enrolledToursRecyclerView.setAdapter(adapter);




                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



    }
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(EnrolledTours.this, NearbyTours.class);
        startActivity(myIntent);
    }
    }
