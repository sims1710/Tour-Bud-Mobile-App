package com.example.tourbud5.TouristPages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.model.Tour;
import com.example.tourbud5.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewTour extends AppCompatActivity {
    private String TAG="viewTour";
    private ArrayList<String> tags;
    private ArrayList<String> rsvp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tour);

        //get widget references
        TextView titleTV = findViewById(R.id.titleTV);
        TextView descriptionTV = findViewById(R.id.descriptionTV);
        TextView placesTV = findViewById(R.id.placesTV);
        TextView dateTimeTV = findViewById(R.id.dateTimeTV);
        ImageView imgView = findViewById(R.id.tourImgView);
        Button registerBtn = findViewById(R.id.registerBtn);
        ChipGroup chipGroup = findViewById(R.id.chipGroup3);
        TextView tourGuideName=findViewById(R.id.tourguide_name);
        TextView tourGuideDescription=findViewById(R.id.tourguide_descriptionTV);

        Intent intent = getIntent();
        String tourId = intent.getStringExtra("tourId");

        //get data
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        DocumentReference docRef = dbRef.document(tourId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Tour tour = documentSnapshot.toObject(Tour.class);

                    //bind data
                titleTV.setText(tour.getTitle());
                descriptionTV.setText(tour.getDescription());
                placesTV.setText(tour.getPlaceName());
                dateTimeTV.setText(tour.getDate().toString());
                Picasso.get().load(Uri.parse(tour.getUri())).into(imgView);
                tags=tour.getTags();
                rsvp=tour.getRSVP();


                //creating chips and placing them in chip group
                for (String tag : tags) {
                    Chip chip = new Chip(ViewTour.this);
                    chip.setText(tag);
                    chip.setChipBackgroundColorResource(R.color.chip_light);
                    chip.setTextColor(getResources().getColor(R.color.white));
                    chipGroup.addView(chip);

                }

                //retrieve tour guide data
                String owner=tour.getOwner();
                CollectionReference dbRefUser = FirebaseFirestore.getInstance().collection("Users");
                DocumentReference docRefUser = dbRefUser.document(owner);

                docRefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String name=user.getFirstName();
                        String description=user.getDescription();

                        //bind tour guide data to widget
                        tourGuideName.setText(name);
                        tourGuideDescription.setText(description);

                    }

                });

                String currentUser= SharedPreferenceManager.getUserId(getApplicationContext());


                if(rsvp.contains(currentUser) ){ //conditional rendering of button based on whether user is enrolled

                    //if enrolled
                    registerBtn.setText("withdraw");
                    registerBtn.setOnClickListener(param -> {

                        //add user to RSVP list
                        try{
                            rsvp.remove(currentUser);
                            dbRef.document(tourId).update("rsvp",rsvp);
                        }catch (Exception e){
                            Log.e(TAG,e.getMessage());
                        }
                        Intent myIntent = new Intent(ViewTour.this, EnrolledTours.class);

                        startActivity(myIntent);

                    });

                }else{

                    //if not enrolled
                    registerBtn.setOnClickListener(param -> {

                        //add user to RSVP list
                        try{
                            rsvp.add(currentUser);
                            dbRef.document(tourId).update("rsvp",rsvp);
                        }catch (Exception e){
                            Log.e(TAG,e.getMessage());
                        }
                        Intent myIntent = new Intent(ViewTour.this, EnrolledTours.class);

                        startActivity(myIntent);

                    });

                }



            }
        });


    }// end onCreate
}