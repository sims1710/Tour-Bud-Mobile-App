package com.example.tourbud5.DatabaseOperations;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.tourbud5.model.Tour;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

public class createTour extends templateCreateOrUpdateTour{
    String TAG="createTour";

    private Context context;
    private  Intent intent;
    private Window window;

    public createTour( Context context, Intent intent,Window window){
        this.context=context;
        this.intent=intent;
        this.window=window;



    }

    @Override
    protected void firestoreOperation(Tour tourObj){
        dbRef.add(tourObj)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        onCompletion(context,intent,window);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
    void onCompletion(Context context, Intent intent, Window window){
        context.startActivity(intent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    };



}
