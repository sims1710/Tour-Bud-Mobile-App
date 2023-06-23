package com.example.tourbud5.TourGuidePages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.tourbud5.Adaptors.CreateTourListingAdaptor;
import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.Auth.TourProfileActivity;
import com.example.tourbud5.model.Tour;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CreatedTours extends AppCompatActivity {

    private String TAG="createdTours";

    //widgets
    Button createTourBtn;
    Button tourGuideMapBtn;
    Button homeBtn;

    //recycler view
    CreateTourListingAdaptor adapter;
    RecyclerView createdTourRecyclerView;

    //to store fetched data
    private final ArrayList<Tour> list=new ArrayList<>();
    private final ArrayList<String> idList=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_tours);

        //fetch references to widgets
        createTourBtn=(Button)findViewById(R.id.createTourbtn);
        tourGuideMapBtn=(Button)findViewById(R.id.tourGuideMapBtn);
        homeBtn=(Button) findViewById(R.id.homeBtn);
        createdTourRecyclerView = (RecyclerView)findViewById(R.id.createdTourRecycleView);

        //set up recycler view display format
        createdTourRecyclerView.setLayoutManager(new LinearLayoutManager(CreatedTours.this));
        createdTourRecyclerView.setHasFixedSize(true);

        fetchAndDisplayTours();

        //set onclick listeners for buttons
        createTourBtn.setOnClickListener(param->{
            Intent myIntent = new Intent(CreatedTours.this, CreateTour.class);

            startActivity(myIntent);
        });

        tourGuideMapBtn.setOnClickListener(param->{
            Intent myIntent = new Intent(CreatedTours.this, TourGuideMap.class);

            startActivity(myIntent);
        });
        homeBtn.setOnClickListener(param->{
            Intent myIntent = new Intent(CreatedTours.this, TourProfileActivity.class);

            startActivity(myIntent);
        });



    }//end of onCreate

    //override android back button
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(CreatedTours.this, TourProfileActivity.class);
        startActivity(myIntent);
    }


   // called in OnCreate method
    private void fetchAndDisplayTours(){

        //reference to firestore
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");

        //retrieve data
        dbRef.whereEqualTo("owner", SharedPreferenceManager.getUserId(getApplicationContext()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //store fetched data in list
                                idList.add(document.getId());
                                list.add(document.toObject(Tour.class));
                                Log.d(TAG, document.getId() + " => " + document.toObject(Tour.class));

                            }

                            //pass list of data to adaptor to bind data to the recycler view
                            adapter= new CreateTourListingAdaptor(list,idList, getApplication(), CreatedTours.this);
                            createdTourRecyclerView.setAdapter(adapter);




                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    } // end of fetchAndDisplayTour()


}
