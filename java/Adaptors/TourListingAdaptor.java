package com.example.tourbud5.Adaptors;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbud5.R;
import com.example.tourbud5.model.Tour;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TourListingAdaptor extends RecyclerView.Adapter<TourListingAdaptor.ViewHolder> {

    private ArrayList<Tour> localDataSet;
    private AppCompatActivity currentActivity;
    private Class nextActivity;
    private ArrayList<String> idList=new ArrayList<>();
    Context context;


    public TourListingAdaptor(ArrayList<Tour> localDataSet, ArrayList<String> idList,
                              Context context, AppCompatActivity currentActivity, Class nextActivity)
    {
        this.localDataSet = localDataSet;
        this.idList=idList;
        this.context = context;
        this.currentActivity = currentActivity;
        this.nextActivity=nextActivity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {



        //from tourListingCard.xml
        private  TextView tourListingTitleTV;
        private  TextView tourListingDescriptionTV;
        private  ImageView tourImageView;
        private  View viewVH;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            viewVH=view;


            tourListingTitleTV = (TextView) view.findViewById(R.id.tourListingTitle);
            tourListingDescriptionTV=(TextView) view.findViewById(R.id.tourListingDescription);
            tourImageView=(ImageView) view.findViewById(R.id.imageView);
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


    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tour_listing_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int Adaptorposition) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Log.d("Adaptor","data"+localDataSet.get(viewHolder.getAdapterPosition()));

        int position=Adaptorposition;//get data from list acc to index

        //set data in viewholder
        viewHolder.getTourListingTitleTV().setText(localDataSet.get(position).getTitle());
        viewHolder.getTourListingDescriptionTV().setText(localDataSet.get(position).getDescription());
        Uri imgUri = Uri.parse(localDataSet.get(position).getUri());
        Picasso.get().load(imgUri).into(viewHolder.getTourImageView());


        viewHolder.viewVH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(currentActivity, nextActivity);
                myIntent.putExtra("tourId", idList.get(position)); //Optional parameters
                currentActivity.startActivity(myIntent);

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

