package com.example.tourbud5.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbud5.R;
import com.example.tourbud5.TourGuidePages.CreatedTours;
import com.example.tourbud5.TourGuidePages.EditTour;
import com.example.tourbud5.TourGuidePages.RSVPTourGuide;
import com.example.tourbud5.model.Tour;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CreateTourListingAdaptor extends RecyclerView.Adapter<CreateTourListingAdaptor.ViewHolder> {

    private String TAG="adaptor";

    private ArrayList<Tour> localDataSet;  //list of fetched tour objects
    private AppCompatActivity currentActivity;
    private ArrayList<String> idList= new ArrayList<>();  //list of tour id
    Context context;



    public CreateTourListingAdaptor(ArrayList<Tour> localDataSet, ArrayList<String> idList,
                                    Context context, AppCompatActivity currentActivity)
    {
        this.localDataSet = localDataSet;
        this.idList=idList;
        this.context = context;
        this.currentActivity = currentActivity;

    }

    //a single card is passed into view holder of adaptor
    public static class ViewHolder extends RecyclerView.ViewHolder {



        //from tourListingCard.xml
        private TextView tourListingTitleTV;
        private  TextView tourListingDescriptionTV;
        private ImageView tourImageView;
        private Button editTourBtn;
        private Button bookingBtn;
        private Button deleteBtn;
        private View viewVH;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            viewVH=view;


            tourListingTitleTV = (TextView) view.findViewById(R.id.createdTourTitleTV);
            tourListingDescriptionTV=(TextView) view.findViewById(R.id.createdTourdescriptionTV);
            tourImageView=(ImageView) view.findViewById(R.id.createdTourImgView);
            editTourBtn=(Button) view.findViewById(R.id.editTourBtn);
            bookingBtn=(Button) view.findViewById(R.id.bookingBtn);
            deleteBtn=(Button) view.findViewById(R.id.deleteTourBtn);
        }

        public TextView getTourListingTitleTV() {
            return tourListingTitleTV;
        }
        public TextView getTourListingDescriptionTV() {
            return tourListingDescriptionTV;
        }
        public ImageView getTourImageView() {
            return tourImageView;
        }
        public Button getEditTourBtn(){return  editTourBtn;}
        public Button getBookingBtn(){return bookingBtn;}
        public Button getDeleteTourBtn(){return deleteBtn;}


    }

    // Create new views (invoked by the layout manager) : the tour listing card
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.created_tour_listing_card, viewGroup, false);

        return new CreateTourListingAdaptor.ViewHolder(view);
    }



    // Bind data to contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CreateTourListingAdaptor.ViewHolder viewHolder, final int Adaptorposition) {



        int position=Adaptorposition; //index to get the corresponding data from list

        //setting text
        viewHolder.getTourListingTitleTV().setText(localDataSet.get(position).getTitle());
        viewHolder.getTourListingDescriptionTV().setText(localDataSet.get(position).getDescription());

        //setting image
        Uri imgUri = Uri.parse(localDataSet.get(position).getUri());
        Picasso.get().load(imgUri).into(viewHolder.getTourImageView());


        //setting button listeners
        viewHolder.getEditTourBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(currentActivity, EditTour.class);
                myIntent.putExtra("tourId", idList.get(position)); //Optional parameters
                currentActivity.startActivity(myIntent);

            }
        });
        viewHolder.getDeleteTourBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             deleteTourFromFirestore(idList.get(position));

            }
        });

        viewHolder.getBookingBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(currentActivity, RSVPTourGuide.class);

                myIntent.putStringArrayListExtra("RSVP", localDataSet.get(position).getRSVP());
                myIntent.putExtra("tourId",idList.get(position));
                myIntent.putExtra("tourName",localDataSet.get(position).getTitle());
                currentActivity.startActivity(myIntent);

            }
        });



    }//end of onBindViewHolder




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    //delete tour callback
    private void deleteTourFromFirestore(String tourId){
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("Tours");
        dbRef.document(tourId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Intent myIntent = new Intent(currentActivity, CreatedTours.class);
                        currentActivity.startActivity(myIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }
}
