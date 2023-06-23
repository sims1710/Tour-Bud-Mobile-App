package com.example.tourbud5.DatabaseOperations;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbud5.TouristPages.ViewTour;
import com.example.tourbud5.model.Tour;
import com.example.tourbud5.Adaptors.TourListingAdaptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class readTour {
    private static String TAG="Read Tour";
    private static String BRANCH="Tours";




    //for firebase storage ; image files
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    //for firestore
    private static CollectionReference dbRef = FirebaseFirestore.getInstance().collection(BRANCH);


    public static void getEnrolledTours(String uid, ArrayList<String> idList, ArrayList<Tour> list, AppCompatActivity appCompatActivity, Activity activity, RecyclerView recyclerView){
        dbRef.whereArrayContains("rsvp", uid )
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
                            TourListingAdaptor adapter= new TourListingAdaptor(list,idList,activity,appCompatActivity, ViewTour.class);
                            recyclerView.setAdapter(adapter);




                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
