package campuscrowds.campuscrowds;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Matthew on 10/15/2017.
 */
public class UpdateLocationFragment extends Fragment {
    //Variables for the specific dining location
    private UUID mLocationId;
    private DiningLocation mLocation;
    public static final String LOCATION_ID  = "LOCATION_ID";
    public String updateTime;
    public String updateComment;

    //Layout variables
    ImageView mDiningImage;
    EditText mWaitTime;
    EditText mComments;
    Button mUpdate;

    //Database variables
    private static final int CAMERA_IMAGE = 111;
    String imageEncoded;
    private DatabaseReference mPhotoReference;
    private DatabaseReference mDateReference;
    private DatabaseReference mWaitReference;
    private DatabaseReference mCommentReference;

    //This is called to do initial creation of the fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //Creates and returns this fragment's view hierarchy
    @Override
    public View onCreateView(LayoutInflater LI, ViewGroup VG, Bundle savedInstanceState) {
        View v = LI.inflate(R.layout.fragment_update_location, VG, false);
        //Connects the layout's elements to local variables
        mDiningImage = (ImageView) v.findViewById(R.id.location_picture);
        mWaitTime = (EditText) v.findViewById(R.id.wait_update);
        mWaitTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.equals("")){
                    updateTime = s.toString();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mComments = (EditText) v.findViewById(R.id.comment_update);
        mComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.equals("")){
                    updateComment = s.toString();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mUpdate = (Button) v.findViewById(R.id.update_location);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Return to DiningLocationFragment and update the database
                mWaitReference.setValue(updateTime);
                mCommentReference.setValue(updateComment);
                mDateReference.setValue((new Date()).toString());
                mPhotoReference.setValue(imageEncoded);
                getActivity().onBackPressed();
            }
        });
        return v;
    }

    //This method is called when the activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mLocationId = (UUID) bundle.getSerializable(LOCATION_ID);
            mLocation = LocationBucket.get(getActivity()).getLocation(mLocationId);
        }
        //Initialize the database references
        mPhotoReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/1");
        mDateReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/2");
        mWaitReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/3");
        mCommentReference = FirebaseDatabase.getInstance().getReference("location/"+mLocation.getLocationName()+"/4");
    }

    //This is called when the fragment is resumed, this is to put the name of the dining location in the activity bar
    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(mLocation.getLocationName());
    }

    public void cameraPressed(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_IMAGE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 1000, 1000, false);
            mDiningImage.setImageBitmap(resizedBitmap);
            encodeBitmapAndSaveToFirebase(resizedBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        imageEncoded = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    //This method is for the camera in the action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.take_picture, menu);
    }

    //This method responds to when the user clicks on a camera
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.take_picture:
                cameraPressed();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }
}
