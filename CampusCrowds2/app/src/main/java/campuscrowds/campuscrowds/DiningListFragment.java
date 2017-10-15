package campuscrowds.campuscrowds;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Matthew on 9/29/2017.
 * This fragment is responsible for the recyclerview, it is the main list of dining locations
 * Responds to clicks on the location, if the location is clicked, starts DiningLocationFragment
 */

public class DiningListFragment extends Fragment {
    private RecyclerView mLocationRecyvlerView;
    private LocationAdapter mAdapter;

    //Creates and returns this fragment's view hierarchy
    @Override
    public View onCreateView(LayoutInflater Li, ViewGroup Vg, Bundle savedInstanceState){
        View v = Li.inflate(R.layout.fragment_location_list, Vg, false);
        mLocationRecyvlerView = (RecyclerView) v.findViewById(R.id.location_recycler_view);
        mLocationRecyvlerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        update();
        return v;
    }

    private class LocationHolder extends RecyclerView.ViewHolder{
        private DiningLocation mLocation;
        public static final String LOCATION_ID  = "LOCATION_ID";
        private TextView mLocationNameView;

        private ImageView mColorIndicator;
        private DatabaseReference mColorReference;

        //Inflate list_location, pass it into the super constructor
        //ViewHolder class holds onto the view in the heirarchy
        public LocationHolder(LayoutInflater Li, ViewGroup Vg){
            super(Li.inflate(R.layout.list_location, Vg, false));
            mLocationNameView = (TextView) itemView.findViewById(R.id.location_name);
            mColorIndicator = (ImageView) itemView.findViewById(R.id.color_indicator);
        }

        //This is called each time a new location is displayed in the LocationHolder
        //When given a location, it updates the TextView to the locations name
        public void bind(DiningLocation location){
            mLocation = location;
            mLocationNameView.setText(mLocation.getLocationName());

            //Responds to updates in the database, changes color of the ImageView based on the wait time
            mColorReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/3");
            //Add a value event listener to the wait time
            ValueEventListener timeListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.getValue(String.class);
                    int colorTime = Integer.parseInt(s);
                    if(colorTime >= 0 && colorTime <= 5){
                        mColorIndicator.setBackgroundColor(Color.rgb(0,255,0));
                    }
                    else if (colorTime > 5 && colorTime <= 10){
                        mColorIndicator.setBackgroundColor(Color.rgb(255,255,0));
                    }
                    else{
                        mColorIndicator.setBackgroundColor(Color.rgb(255,0,0));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO make an error message
                }
            };
            mColorReference.addValueEventListener(timeListener);

            //Responds to clicks
            mLocationNameView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LOCATION_ID, mLocation.getId());
                    DiningLocationFragment nextFrag= new DiningLocationFragment();
                    nextFrag.setArguments(bundle);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, nextFrag, "tag");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }

    //When RecyclerView needs to display a new Viewholder, this method is called
    //Adapter knows all of the Locations details
    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<DiningLocation> mLocations;

        public LocationAdapter(List<DiningLocation> locations){
            mLocations = locations;
        }

        //Called by the RecyclerView when it needs a new ViewHolder to display
        //Use the layout inflater to construct a new LocationHolder
        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater Li = LayoutInflater.from(getActivity());
            return new LocationHolder(Li, parent);
        }

        //Called each time the RecyclerView has the holder bound to the location
        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            DiningLocation location = mLocations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }
    }

    //This sets up this fragment's UI
    private void update(){
        LocationBucket locationBucket = LocationBucket.get(getActivity());
        List<DiningLocation> locations = locationBucket.getLocations();
        mAdapter = new LocationAdapter(locations);
        mLocationRecyvlerView.setAdapter(mAdapter);
    }

    //This is called when the fragment is resumed, this is to put the name of the dining location in the activity bar
    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("CampusCrowds");
    }
}