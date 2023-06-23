package com.example.tourbud5.TourGuidePages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;


import com.example.tourbud5.BuildConfig;
import com.example.tourbud5.R;
import com.example.tourbud5.TouristPages.ViewTour;
import com.example.tourbud5.model.Tour;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

public class TourGuideMap extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener,OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private static final String TAG = TourGuideMap.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;
    Geocoder geocoder;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location at SUTD and zoom if no location provided
    private final LatLng defaultLocation = new LatLng(1.3413, 103.9638);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int EDIT_REQUEST = 1;
    public boolean editMode = true;

    //geocoder




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise the most recent renderer, to allow google cloud connection
        MapsInitializer.initialize(getApplicationContext(), Renderer.LATEST, this);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        geocoder = new Geocoder(this, Locale.getDefault());

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_tour_guide_map);

        //add toggle button
        SwitchMaterial toggle=findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is checked.
                editMode = true;
            } else {
                // The switch isn't checked.
                editMode = false;
            }
            MapEditable();
        });

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Handles a click on the menu option to get a place.
     */


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        // Initialize the map
        this.map = map;

        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        addTourLocations();

        map.setOnInfoWindowClickListener(this);



    }

    /**
     * handle click on infowindow
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        String tourId=(String)marker.getTag();
        Intent myIntent = new Intent(TourGuideMap.this, ViewTour.class);
        myIntent.putExtra("tourId", tourId); //Optional parameters
        TourGuideMap.this.startActivity(myIntent);

    }

    /**
     * Gets the location of tours and display on map
     */

    private void addTourLocations(){
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        dbRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "successfully fetched by tag");


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Tour tourObj= document.toObject(Tour.class);

                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(tourObj.getMeetlocation().getLat(),tourObj.getMeetlocation().getLng()))
                                        .title(tourObj.getTitle())
                                        .snippet(tourObj.getDescription())
                                        .alpha(0.9f)
                                        .icon(BitmapDescriptorFactory.defaultMarker(42))
                                )
                                .setTag(document.getId());

                                Log.d(TAG, document.getId() + " => " + document.toObject(Tour.class));
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        // If there is no valid map, show blank NOTE CHECK API_KEY
        if (map == null) {
            return;
        }
        try {
            // If user has allowed location tracking
            if (locationPermissionGranted) {
                // Indicate their position on map
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                // Otherwise, request for user location
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Place marker on map spot
     */
    //TODO: REMOVE FOR TOURIST
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EDIT_REQUEST) : {
                // check validity of location
                if (resultCode == Activity.RESULT_OK) {
                    // add marker
                    MarkerOptions markerOptions = data.getParcelableExtra("marker");
                    map.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(42)).alpha(0.9f));
                }
                break;
            }
        }
    }

    /**
     * Check if user is allowed to edit the map
     */
    //TODO: REMOVE FOR TOURIST
    private void MapEditable(){
        // If edit mode is on
        if (editMode)
        {
            // triggers when user clicks on mpa
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                // Takes in the latlng of the place clicked
                public void onMapClick(final LatLng latLng) {
                    // Switch to EditActivity
                    Intent edit = new Intent(TourGuideMap.this, CreateTour.class);
                    edit.putExtra("location", latLng);




                    TourGuideMap.this.startActivityForResult(edit, EDIT_REQUEST);
                }
            });
        }
        else {
            // disable the map listener
            map.setOnMapClickListener(null);
        }
    }

    @Override
    public void onMapsSdkInitialized(@NonNull Renderer renderer) {

    }
}