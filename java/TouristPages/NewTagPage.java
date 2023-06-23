package com.example.tourbud5.TouristPages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.tourbud5.R;
import com.example.tourbud5.model.Tour;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class NewTagPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tag_page);

        //get widget reference
        ChipGroup chipGroup = findViewById(R.id.tagChipGroup);

        //creating chips and placing them in chip group
        for (String tag : Tour.possibleTags){
            Chip chip = new Chip(NewTagPage.this);
            chip.setText(tag);
            chip.setChipBackgroundColorResource(R.color.chip_light);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setOnClickListener((param -> {
                Intent myIntent = new Intent(NewTagPage.this, SearchByTag.class);
                myIntent.putExtra("tag", tag); //Optional parameters
                startActivity(myIntent);
                //intent to SearchByTag
            }));
            chipGroup.addView(chip);

        }

    }
}