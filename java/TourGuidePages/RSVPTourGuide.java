package com.example.tourbud5.TourGuidePages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.tourbud5.R;
import com.example.tourbud5.Adaptors.UserListingAdaptor;
import com.example.tourbud5.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class RSVPTourGuide extends AppCompatActivity {
    private String TAG="RSVP";
    UserListingAdaptor adapter;
    private final ArrayList<User> list=new ArrayList<>();
    private final ArrayList<String> idList=new ArrayList<>();
    private RecyclerView RSVPTourGuideRecyclerView;
    private ArrayList<String> RSVP;
    private String tourName;
    private TextView tourNameTV;


    public  ArrayList<String> removeFromRSVP(String userId) {  //so that any card can access and modify the same instance of RSVP
        this.RSVP.remove(userId);
        return this.RSVP; // retrieve the updated list so that it can be stored in database
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsvptour_guide);

        Intent intent = getIntent();
        RSVP=intent.getStringArrayListExtra("RSVP");
        tourName=intent.getStringExtra("tourName");
        String tourId=intent.getStringExtra("tourId");

        tourNameTV=findViewById(R.id.tour_name);
        tourNameTV.setText(tourName);




        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Users");

if(! RSVP.isEmpty()) {

    //set up recyclerview
    RSVPTourGuideRecyclerView=findViewById(R.id.RSVPTourGuideRecyclerView);
    RSVPTourGuideRecyclerView.setLayoutManager(new LinearLayoutManager(RSVPTourGuide.this));
    RSVPTourGuideRecyclerView.setHasFixedSize(true);

    dbRef.whereIn(FieldPath.documentId(), Arrays.asList(RSVP.toArray())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                Log.d(TAG, "successfully fetched by tag");


                for (QueryDocumentSnapshot document : task.getResult()) {

                    idList.add(document.getId());
                    list.add(document.toObject(User.class));
                    Log.d(TAG, document.getId() + " => " + document.toObject(User.class));
                }

                //pass list data to adaptor to bind data to the recycler view
                adapter = new UserListingAdaptor(list, idList, getApplication(), tourId, RSVPTourGuide.this);
                RSVPTourGuideRecyclerView.setAdapter(adapter);


            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    });
}



    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(RSVPTourGuide.this, CreatedTours.class);
        startActivity(myIntent);
    }
}