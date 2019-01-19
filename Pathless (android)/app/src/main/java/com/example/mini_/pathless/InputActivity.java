package com.example.mini_.pathless;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InputActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    //widgets
    AutoCompleteTextView searchLocation;
    String user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //vars
    private static final String TAG = "debugCheck";
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mPlaceAutocompleteadapter;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // setup Firebase userId, storage and database
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(user);

        // connecting the add button to a click listener
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new AddClickListener());

        // button to add pictures from gallery
        Button buttonLoadImage = findViewById(R.id.gallery_button);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            // the onClick that brings user to the gallery
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        // setting up autocomplete for places in the edit text
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // set the adapter to the edit text
        mPlaceAutocompleteadapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        searchLocation = findViewById(R.id.location_text);
        searchLocation.setAdapter(mPlaceAutocompleteadapter);
    }

    // the method for google api connection failed listener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    //widgets
    ImageView imageview;
    Bitmap bitmap;
    Uri selectedUri;
    ArrayList<String> urls = new ArrayList();

    // function that saves and shows the pictures chosen from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedUri = data.getData();

            // show image that is selected from the gallery
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                imageview = findViewById(R.id.image_selected);
                imageview.setImageBitmap(bitmap);

                // put the selected picture to the storage and add the url to array
                Date currentTime = Calendar.getInstance().getTime();
                ref = storageReference.child("images/" + currentTime +
                        selectedUri.getLastPathSegment());
                ref.putFile(selectedUri).addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                newUri = task.getResult();
                                uploadUri = newUri.toString();
                                urls.add(uploadUri);
                            }
                        });
                    }
                });
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    // the click listener for the add button
    private class AddClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            postAll();
            Intent intent = new Intent(InputActivity.this, MapActivity.class);
            startActivity(intent);
        }
    }

    //widgets
    Uri newUri;
    String uploadUri;
    StorageReference ref;
    String location;
    TextView descriptionInput;
    LatLng latLong;
    ArrayList<String> allPlaceNames = new ArrayList<>();
    ArrayList<String> oldPlaceNames;

    private void postAll() {
        // get location and description
        location = searchLocation.getText().toString();
        descriptionInput = findViewById(R.id.description_text);
        String description = descriptionInput.getText().toString();
        if (description.isEmpty()) {
            description = "empty";
        }

        // push location, description, coordinate(LatLng) and pictures (in array) to Firebase
        databaseReference = databaseReference.child(location);
        Post post = new Post(location, urls, description);
        databaseReference.setValue(post);


        // getting LatLng of location
        Geocoder geocoder = new Geocoder(InputActivity.this);
        List<Address> list;
        try {
            list = geocoder.getFromLocationName(location, 1);
            Address address = list.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            latLong = new LatLng(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set LatLng in child with all added coordinates
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("places");
                MarkerInformation markInfo = new MarkerInformation();
                markInfo.setPlaceName(ds.getValue(MarkerInformation.class).getPlaceName());
                oldPlaceNames = markInfo.getPlaceName();
                Log.d(TAG, oldPlaceNames.toString());
                for (int i = 0; i < oldPlaceNames.size(); i++){
                    allPlaceNames.add(oldPlaceNames.get(i));
                }
                allPlaceNames.add(location);
                Log.d(TAG, "new" + allPlaceNames.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something went wrong");
            }
        });

//        allPlaceNames.add(location);
//        allPlaceNames.add("New York, Verenigde Staten");
        databaseReference = databaseReference.child("places");
        databaseReference.setValue(allPlaceNames);
    }
}
