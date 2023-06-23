package com.example.tourbud5.Adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbud5.R;
import com.example.tourbud5.TourGuidePages.RSVPTourGuide;
import com.example.tourbud5.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserListingAdaptor extends RecyclerView.Adapter<UserListingAdaptor.ViewHolder>{
    private String TAG="userListingAdaptor";

  //to store data
    private ArrayList<User> localDataSet;
    private AppCompatActivity currentActivity;
    private Class nextActivity;
    private ArrayList<String> idList;
    private String tourId;
    private RSVPTourGuide rsvpTourGuideClassInstance;
    Context context;


    public UserListingAdaptor(ArrayList<User> localDataSet, ArrayList<String> idList,
                              Context context, String tourId, RSVPTourGuide rsvpTourGuideClassInstance)
    {
        this.localDataSet = localDataSet;
        this.idList=idList;
        this.context = context;
        this.tourId=tourId;
        this.rsvpTourGuideClassInstance=rsvpTourGuideClassInstance;

    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {



        //from userListingCard.xml
        private TextView userNameTV;
        private  Button rejectBtn;



        public ViewHolder(View view) {
            super(view);

            userNameTV = (TextView) view.findViewById(R.id.userNameTV);

            rejectBtn=(Button) view.findViewById(R.id.rejectBtn);

        }

        public TextView getUserNameTV() {
            return userNameTV;
        }

        public Button getRejectBtn() {
            return rejectBtn;
        }



    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserListingAdaptor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_listing_card, viewGroup, false);

        return new UserListingAdaptor.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(UserListingAdaptor.ViewHolder viewHolder, final int Adaptorposition) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Log.d("Adaptor","data"+localDataSet.get(viewHolder.getAdapterPosition()));

        int position=Adaptorposition;//to get data from list based on index

        //set data in viewholder
        viewHolder.getUserNameTV().setText(localDataSet.get(position).getPhone());
        viewHolder.getRejectBtn().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {


                CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
                dbRef.document(tourId)
                        .update("rsvp", FieldValue.arrayRemove(idList.get(position)))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");

                                //disable button after pressing reject so will not reject twice
                                viewHolder.getRejectBtn().setEnabled(false);
                                viewHolder.getRejectBtn().setBackgroundColor(5);
                                viewHolder.getRejectBtn().setText("rejected");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });



            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
