package com.example.tourbud5.TourGuidePages;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tourbud5.DatabaseOperations.updateTour;
import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.model.Tour;
import com.example.tourbud5.model.TourBuilder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTour extends AppCompatActivity {
    private String TAG="CreateTour";

    //widgets
    private EditText titleET;
    private EditText descriptionET;
    private EditText placesET;
    private EditText dateET;
    private EditText timeET;
    private ImageButton imgBtn;
    private Button  createBtn;
    private ChipGroup chipGroup;
    private ProgressBar spinner;

    //to store data
    private String [] tags=Tour.possibleTags;
    private final TourBuilder.tourBuilder builderTour=new TourBuilder.tourBuilder();
    private Date date;
    private LatLng latlng;
    private String placeName;
    private String uriStr;
    private String newUriStr;
    private Boolean uriStrIsChanged =false;
    private ArrayList<String> selectedTags=new ArrayList<String>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tour);

        titleET = findViewById(R.id.title2);
        descriptionET = findViewById(R.id.description2);
        placesET = findViewById(R.id.inputPlaces2);
        dateET = findViewById(R.id.inputDate2);
        timeET = findViewById(R.id.inputTime2);
        imgBtn = findViewById(R.id.imageButton3);
        createBtn = findViewById(R.id.createTour2);
        chipGroup = findViewById(R.id.chipGroup2);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        Intent intent = getIntent();
        String tourId = intent.getStringExtra("tourId");

        //for getting and overwriting data
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        DocumentReference docRef = dbRef.document(tourId);


        //retrieve tour and autofill fields
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Tour tour = documentSnapshot.toObject(Tour.class);

                latlng=new com.google.android.gms.maps.model.LatLng(tour.getMeetlocation().getLat(), tour.getMeetlocation().getLng());
                uriStr =tour.getUri();
                date=tour.getDate();
                selectedTags=tour.getTags();
                placeName=tour.getPlaceName();

                //pre fill form fields
                titleET.setText(tour.getTitle());
                descriptionET.setText(tour.getDescription());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tour.getDate());
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
                String formattedDate = dateFormat.format(calendar.getTime());
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                String formattedTime = timeFormat.format(calendar.getTime());
                dateET.setText(formattedDate);
                timeET.setText(formattedTime);
                placesET.setText(tour.getPlaceName());

                Picasso.get().load(Uri.parse(tour.getUri())).into(imgBtn);

                FormUtils.createFormChip(tags, selectedTags,  chipGroup, EditTour.this, getResources());




            }
        });
        //for location autocomplete, places API
        Places.initialize(getApplicationContext(), "AIzaSyByiMBmoWzBDJ7L5SRIHCzl6CXg34qrgKw");
        PlacesClient placesClient = Places.createClient(this);



        //set imgBtn
        imgBtn.setOnClickListener(param -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });



        //set focus for dateET
        dateET.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            FormUtils.initializeDatePicker(EditTour.this,dateET,date).show();

                        } }
                });



        //set focus for timeET
        timeET.setOnClickListener((param) -> {
            FormUtils.initializeTimePicker(EditTour.this,timeET,date).show();
        });

        //assign location autocomplete intent to placesET
        placesET.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            startAutocompleteIntent();

                        } }
                });



        createBtn.setOnClickListener((param)->{
                    if(isEmpty(titleET) || isEmpty(descriptionET) || isEmpty(dateET) || isEmpty(placesET) || isEmpty(timeET)){
                        Toast.makeText(this, "all fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        spinner.setVisibility(View.VISIBLE);
                        updateTourThread(tourId);}


        });



    }


    //location autocomplete intent initialization
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

    //for location field autocomplete
    private void startAutocompleteIntent(){

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
                .build(this);
        startAutocomplete.launch(intent);
    };



    //photo picker initialization
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    Picasso.get().load(uri).into(imgBtn);
                    newUriStr =uri.toString();
                    uriStrIsChanged =true;
                    uriStr=uri.toString();

                } else {

                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void updateTourThread(String tourId){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // YOU ARE IN BACKGROUND THREAD

                try {
                    Tour tour=builderTour.setOwner(SharedPreferenceManager.getUserId(getApplicationContext()))
                            .setTitle(titleET.getText().toString())
                            .setDescription(descriptionET.getText().toString())
                            .setDate(date)
                            .setMeetlocation(latlng)
                            .setPlaceName(placeName)
                            .setUri(uriStr)
                            .setTags(selectedTags)
                            .build();


                    Intent myIntent = new Intent(EditTour.this, CreatedTours.class);


                    if(uriStrIsChanged){  //if image is changed need to upload new image
                        //uploadImgAndTour(docRef,storageRef);
                        Log.i("image","change in image");
                        new updateTour(tourId,EditTour.this,myIntent,getWindow()).updateStorageThenFirestore(tour);

                    }else{
                        //if image is not changed, no need to upload new image, save tour to firestore
                        //uploadTour(docRef,uriStr);
                        new updateTour(tourId,EditTour.this,myIntent,getWindow()).firestoreOperation(tour);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // YOU ARE IN THE UI (MAIN) THREAD

                            Toast.makeText(EditTour.this, "updating", Toast.LENGTH_SHORT).show();


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