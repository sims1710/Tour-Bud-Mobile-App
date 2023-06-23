package com.example.tourbud5.TouristPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.tourbud5.R;
import com.example.tourbud5.Adaptors.TourListingAdaptor;
import com.example.tourbud5.Auth.TourProfileActivity;
import com.example.tourbud5.model.Tour;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class NearbyTours extends AppCompatActivity {
    private String TAG="nearbyTours";

    EditText searchbar;
    Button searchbtn;
    Button viewEnrolledBtn;
    Button mapBtn;
    Button homeButtonTourist;

    //recycler view
    RecyclerView nearbyToursRecyclerView;
    TourListingAdaptor adapter;

    //to save data
    Location currentLocation;
    private final ArrayList<Tour> list=new ArrayList<>();
    private final ArrayList<String> idList=new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_tours);

        //get widget references
        searchbtn=findViewById(R.id.searchbtn);
        viewEnrolledBtn=findViewById(R.id.viewEnrolledBtn);
        nearbyToursRecyclerView=findViewById(R.id.nearbyToursRecyclerView);
        mapBtn=findViewById(R.id.touristMapBtn);
        homeButtonTourist=findViewById(R.id.homeBtnTourist);

        //get current location to sort based on distance
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation= new Location("");


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            currentLocation.setLatitude(lastKnownLocation.getLatitude());
                            currentLocation.setLongitude(lastKnownLocation.getLongitude());
                            getListingAndDisplay();

                        }
                    }
                }
            });


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }



        searchbtn.setOnClickListener(param->{
            Intent myIntent = new Intent(NearbyTours.this, NewTagPage.class);
             //Optional parameters
            startActivity(myIntent);
        });

        viewEnrolledBtn.setOnClickListener(param->{
            Intent myIntent = new Intent(NearbyTours.this, EnrolledTours.class);

            startActivity(myIntent);
        });

        mapBtn.setOnClickListener(param->{
                    Intent myIntent = new Intent(NearbyTours.this, TouristMap.class);

                    startActivity(myIntent);
                }

        );
        homeButtonTourist.setOnClickListener(
                param->{
                    Intent myIntent = new Intent(NearbyTours.this, TourProfileActivity.class);

                    startActivity(myIntent);
                }
        );


    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(NearbyTours.this, TourProfileActivity.class);
        startActivity(myIntent);
    }

    private void getListingAndDisplay(){
        nearbyToursRecyclerView.setLayoutManager(new LinearLayoutManager(NearbyTours.this));
        nearbyToursRecyclerView.setHasFixedSize(true);

        //get data from firestore
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        dbRef.get()
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

                            //sort the idList and list of tours based on dist
                            ArrayList<Tour> sortedList=new ArrayList<Tour>();
                            ArrayList<String> sortedIdList=new ArrayList<String>();
                            ArrayList<Pair> pairs=sortBasedOnDist(idList, list,currentLocation);
                            for (int i = 0; i < list.size(); i++) {
                                sortedList.add(pairs.get(i).tourObj);
                                sortedIdList.add(pairs.get(i).id);

                            }



                            //pass sorted list data to adaptor to bind data to the recycler view
                            adapter= new TourListingAdaptor(sortedList,sortedIdList, getApplication(), NearbyTours.this, ViewTour.class);
                            nearbyToursRecyclerView.setAdapter(adapter);




                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public static ArrayList<Pair> sortBasedOnDist(ArrayList<String> idList, ArrayList<Tour> list,Location currentLocation) {
        Log.d("sort","list size"+list.size());
        ArrayList<Pair> pairs= new ArrayList<Pair>();
        for (int i = 0; i < list.size(); i++) {
            Log.d("sort","obj "+list.get(i));
            pairs.add(new Pair(list.get(i),idList.get(i),currentLocation));

        }

        Collections.sort(pairs);
        return pairs;


    }

    //comparable is used to compare the distance away from user's current location
        static  class Pair implements Comparable<Pair>
        {
            Tour tourObj;
            String id;
            Location currentLocation;

            Pair (Tour tourObj, String id,Location currentLocation) //constructor
            {
                this.tourObj = tourObj;
                this.id = id;
                this.currentLocation=currentLocation;

            }
            public int compareTo(Pair other) //making it only compare tourObj dist from currentLocation
            {
                Location thisLocation = new Location("");
                thisLocation.setLatitude(this.tourObj.getMeetlocation().getLat());
                thisLocation.setLongitude(this.tourObj.getMeetlocation().getLng());
                float thisDistance = currentLocation.distanceTo(thisLocation);
                thisDistance = thisDistance/1000;

                Location otherLocation = new Location("");
                otherLocation.setLatitude(other.tourObj.getMeetlocation().getLat());
                otherLocation.setLongitude(other.tourObj.getMeetlocation().getLng());
                float otherDistance = currentLocation.distanceTo(thisLocation);
                otherDistance = otherDistance/1000;


                return (int)(thisDistance - otherDistance);
            }
        }
}