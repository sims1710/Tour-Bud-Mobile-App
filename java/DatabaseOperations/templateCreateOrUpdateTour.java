package com.example.tourbud5.DatabaseOperations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.tourbud5.model.Tour;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public abstract class templateCreateOrUpdateTour {

    private String BRANCH="Tours";




    //for firebase storage ; image files
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    //for firestore
    CollectionReference dbRef = FirebaseFirestore.getInstance().collection(BRANCH);



    //either updates or create new entry in firestore after uploading new image
    //called in create tour or edit tour(when image is changed)
    public void updateStorageThenFirestore(Tour tourObj){

        String uriStr=tourObj.getUri();
        StorageReference tourImgRef = storageRef.child("images/Tours/"+uriStr);

        //start uploading image
        UploadTask uploadTask = tourImgRef.putFile(Uri.parse(uriStr));
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d("StorageDBManager:uploadTourImg", "failed to upload ");
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                Log.d("StorageDBManager:uploadTourImg", "success in upload, getting download url");
                return tourImgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

            //on successfully upload image , save tour to database
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    tourObj.setUri(downloadUri.toString());
                    firestoreOperation(tourObj);
                    Log.d("StorageDBManager:uploadTourImg", "success in getting download url ");





                } else {
                    // Handle failures
                    // ...
                    Log.d("StorageDBManager:uploadTourImg", "failed in getting download url");
                }

            }
        });

    }

    abstract void firestoreOperation(Tour tourObj);

    abstract void onCompletion(Context context, Intent intent, Window window);





}
