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

public class updateTour extends templateCreateOrUpdateTour{

    private String TAG="updateTour";
    private String tourId;
    private Context context;
    private  Intent intent;
    private Window window;


    public updateTour(String tourId, Context context, Intent intent, Window window){

        this.tourId=tourId;
        this.context=context;
        this.intent=intent;
        this.window=window;


    }

    @Override
    public void firestoreOperation(Tour tourObj){
        DocumentReference docRef = dbRef.document(tourId);

        docRef.set(tourObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        onCompletion(context,intent,window);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }
    void onCompletion(Context context,Intent intent,Window window){

        context.startActivity(intent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    };
}
