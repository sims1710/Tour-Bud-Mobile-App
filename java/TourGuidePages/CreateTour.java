package com.example.tourbud5.TourGuidePages;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tourbud5.DatabaseOperations.createTour;

import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.model.Tour;
import com.example.tourbud5.model.TourBuilder;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.ChipGroup;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateTour extends AppCompatActivity {

    private String TAG="CreateTour";

    //widgets
    private EditText titleET;
    private EditText descriptionET;
    private EditText placesET;
    private EditText dateET;
    private EditText timeET;
    private ImageButton  imgBtn;
    private Button  createBtn;
    private ChipGroup chipGroup;
    private ProgressBar spinner;


    private String [] tags=Tour.possibleTags;
    private final TourBuilder.tourBuilder builderTour=new TourBuilder.tourBuilder();

    //to store fetched data
    private Date date=new Date();
    private LatLng latlng;
    private String placeName;
    private String uriStr;
    private ArrayList<String> selectedTags=new ArrayList<String>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        //get references to widgets
        titleET = findViewById(R.id.title);
        descriptionET = findViewById(R.id.description);
        placesET = findViewById(R.id.inputPlaces);
        dateET = findViewById(R.id.inputDate);
        timeET = findViewById(R.id.inputTime);
        imgBtn = findViewById(R.id.addImage);
        createBtn = findViewById(R.id.createTour);
        chipGroup = findViewById(R.id.chipGroup);
        spinner=(ProgressBar)findViewById(R.id.progressBarCreateTour);
        spinner.setVisibility(View.GONE);


        prefillLocationField(); //gets any data through intent and set it in text view
        //addChips(); //add chips to chip group

        FormUtils.createFormChip(tags, selectedTags,  chipGroup, CreateTour.this, getResources());

        //for location autocomplete, places API
               Places.initialize(getApplicationContext(), "AIzaSyByiMBmoWzBDJ7L5SRIHCzl6CXg34qrgKw");
               PlacesClient placesClient = Places.createClient(this);



        //set focus for dateET
        dateET.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            FormUtils.initializeDatePicker(CreateTour.this,dateET,date).show();
                        }
                    }}
        );



      //set focus for timeET
        timeET.setOnClickListener((param) -> {
            FormUtils.initializeTimePicker(CreateTour.this,timeET,date).show();
        });

        //assign location autocomplete intent to placesET
        placesET.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            autoCompleteLocation();

                        }
                    }}
        );

        createBtn.setOnClickListener((param)->{
                    if(isEmpty(titleET) || isEmpty(descriptionET) || isEmpty(dateET) || isEmpty(placesET) || isEmpty(timeET) ||uriStr==null){
                        Toast.makeText(this, "all fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        spinner.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        createTourThread();
                    }

        });

        //set callback for image upload button
        imgBtn.setOnClickListener(param -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });


    }//end of onCreate



    //prefill Location field
    private void prefillLocationField(){
        //pre-fill the location field if set through map
        Intent intent = getIntent();
        latlng = intent.getParcelableExtra("location");

        if(latlng !=null  ){

            try {
                List<Address> addresses = new ArrayList<>();
                Geocoder geocoder = new Geocoder(CreateTour.this, Locale.getDefault());
                addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude,1);
                android.location.Address address = addresses.get(0);
                placeName=address.getAddressLine(0);
                placesET.setText(placeName);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    //launching the google location search
    // used in autoCompleteLocation below
    final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);

                        // Write a method to read the address components from the Place
                        // and populate the form with the address components
                        Log.d(TAG, "Place: " + place.getAddressComponents());

                        //send output to editable text
                        placesET.setText(place.getAddress());
                        placeName=place.getAddress();

                        latlng=place.getLatLng();


                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });


    //for autocompleting location field
    private void autoCompleteLocation(){

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG, Place.Field.VIEWPORT,Place.Field.NAME, Place.Field.ADDRESS);

        // Build the autocomplete intent with field, country, and type filters applied
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("SG")
                .setTypesFilter(new ArrayList<String>() {{
                    add(TypeFilter.ADDRESS.toString().toLowerCase());
                }})
                .build(CreateTour.this);
        startAutocomplete.launch(intent);
    };


    //callback for image button
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    Picasso.get().load(uri).into(imgBtn);
                    uriStr=uri.toString();

                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });




    private void createTourThread(){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // YOU ARE IN BACKGROUND THREAD

                try {


                    Intent myIntent = new Intent(CreateTour.this, CreatedTours.class);
                    Tour tour=builderTour.setOwner(SharedPreferenceManager.getUserId(getApplicationContext()))
                            .setTitle(titleET.getText().toString())
                            .setDescription(descriptionET.getText().toString())
                            .setDate(date)
                            .setMeetlocation(latlng)
                            .setPlaceName(placeName)
                            .setUri(uriStr)
                            .setTags(selectedTags)
                            .build();

                    new createTour(CreateTour.this,myIntent,getWindow()).updateStorageThenFirestore(tour);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // YOU ARE IN THE UI (MAIN) THREAD
                            Toast.makeText(CreateTour.this,
                                    "creating", Toast.LENGTH_SHORT).show();


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace(); // Display a toast
                }


            }});
    }
    private  boolean isEmpty(EditText txt) {
        // check if field contains any characters, not including whitespaces (.trim())
        if (txt.getText().toString().trim().length() > 0)
            return false;
        return true;
    }


}