package com.example.tourbud5.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.TourGuidePages.CreatedTours;
import com.example.tourbud5.TouristPages.NearbyTours;

public class TourProfileActivity extends AppCompatActivity {
    TextView text;
    Button tourGuideButton;
    Button touristButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_profile);

        text=findViewById(R.id.textView);
        tourGuideButton=findViewById(R.id.tourGuideButton);
        touristButton=findViewById(R.id.touristButton);

        text.setText(SharedPreferenceManager.getUserId(getApplicationContext()));

        tourGuideButton.setOnClickListener(param->{
            Intent i = new Intent(TourProfileActivity.this, CreatedTours.class);
            startActivity(i);

        });
        touristButton.setOnClickListener(param->{
            Intent i = new Intent(TourProfileActivity.this, NearbyTours.class);
            startActivity(i);

        });




    }
}