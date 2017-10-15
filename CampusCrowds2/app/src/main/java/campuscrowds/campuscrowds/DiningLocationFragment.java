package campuscrowds.campuscrowds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Matthew on 9/29/2017.
 * This class is for when someone selects a Dining Location from the initial list
 * It will display the most recent photo as well as the time for when it was updated
 * It inflates the XML file that defines this layout (onCreateView)
 */

public class DiningLocationFragment extends Fragment {

    //Variables for the specific dining location
    private UUID mLocationId;
    private DiningLocation mLocation;
    public static final String LOCATION_ID  = "LOCATION_ID";
    private ImageView mLocationPhoto;
    private TextView mLastUpdatedDate;
    private TextView mWaitTime;
    private TextView mComment;

    //Database variables
    private DatabaseReference mDiningLocationReference;
    private DatabaseReference mDateReference;
    private DatabaseReference mWaitReference;
    private DatabaseReference mCommentReference;
    private ValueEventListener mDateListener;
    private ValueEventListener mLocationListener;
    private ValueEventListener mWaitListener;
    private ValueEventListener mCommentListener;

    //This is called to do initial creation of the fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        //Add a value event listener to the location
        ValueEventListener locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                if(!s.equals("empty")){
                    //Put a different picture in
                    try {
                        Bitmap Image = decodeFromFirebase(s);
                        mLocationPhoto.setImageBitmap(Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO make an error message
            }
        };
        mDiningLocationReference.addValueEventListener(locationListener);
        //Keep a copy of the location listener so we can remove it when the app stops
        mLocationListener = locationListener;

        //Add a value event listener to the date
        ValueEventListener dateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                mLastUpdatedDate.setText(s);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO make an error message
            }
        };
        mDateReference.addValueEventListener(dateListener);
        //Keep a copy of the location listener so we can remove it when the app stops
        mDateListener = dateListener;

        //Add a value event listener to the wait time
        ValueEventListener waitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                mWaitTime.setText(s);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO make an error message
            }
        };
        mWaitReference.addValueEventListener(waitListener);
        mWaitListener = waitListener;

        //Add a value event listener to the comments
        ValueEventListener commentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                mComment.setText(s);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO make an error message
            }
        };
        mCommentReference.addValueEventListener(commentListener);
        mCommentListener = commentListener;
    }

    //Creates and returns this fragment's view hierarchy
    @Override
    public View onCreateView(LayoutInflater LI, ViewGroup VG, Bundle savedInstanceState){
        View v = LI.inflate(R.layout.fragment_dining_location, VG, false);
        //Connect the last posted picture with the ImageView in the layout
        mLocationPhoto = (ImageView) v.findViewById(R.id.most_recent);
        //Connecting the time of the last post with the TextView in the layout
        mLastUpdatedDate = (TextView) v.findViewById(R.id.post_date);
        //Connecting the wait time of the last update with the TextView
        mWaitTime = (TextView) v.findViewById(R.id.wait_time);
        //Connecting the comment of the last update with the TextView
        mComment = (TextView) v.findViewById(R.id.comment);
        return v;
    }

    //This is called when the fragment is resumed, this is to put the name of the dining location in the activity bar
    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(mLocation.getLocationName());
    }

    //This method is called when the activity is created
    //It unpacks the bundle passed to this fragment so that it knows which location to display
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            mLocationId = (UUID) bundle.getSerializable(LOCATION_ID);
            mLocation = LocationBucket.get(getActivity()).getLocation(mLocationId);
        }
        //Initialize the database references
        mDiningLocationReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/1");
        mDateReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/2");
        mWaitReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/3");
        mCommentReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/4");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Remove listeners
        if(mLocationListener != null){
            mDiningLocationReference.removeEventListener(mLocationListener);
        }
        if(mDateListener != null){
            mDateReference.removeEventListener(mDateListener);
        }
        if(mWaitListener != null){
            mWaitReference.removeEventListener(mWaitListener);
        }
        if(mCommentListener != null){
            mCommentReference.removeEventListener(mCommentListener);
        }
    }

    //This method is for the camera in the action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.update_location, menu);
    }

    //This method responds to when the user clicks on a camera
    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        switch(mi.getItemId()){
            case R.id.update_location:
                Bundle bundle = new Bundle();
                bundle.putSerializable(LOCATION_ID, mLocation.getId());
                UpdateLocationFragment nextFrag= new UpdateLocationFragment();
                nextFrag.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, nextFrag, "tag");
                ft.addToBackStack(null);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }

    public static Bitmap decodeFromFirebase(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}