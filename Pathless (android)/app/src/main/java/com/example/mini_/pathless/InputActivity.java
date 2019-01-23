package com.example.mini_.pathless;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
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
    ViewPager viewPager;

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
    ImageView noImageView;
    Uri selectedUri;
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> urls = new ArrayList();

    // function that saves and shows the pictures chosen from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        buttonOff();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedUri = data.getData();

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
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            showImage(selectedUri);
                        }
                    });
                }
            });
        }
    }

    // method that shows the selected images in the InputActivity
    public void showImage(Uri selectedUri){
        noImageView = findViewById(R.id.no_image);
        noImageView.setVisibility(View.INVISIBLE);
        images.add(selectedUri.toString());
        viewPager = findViewById(R.id.image_selected);
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(imageSliderAdapter);
        if (images.size() > 1){
            ImageView galleryIcon = findViewById(R.id.gallery_icon);
            galleryIcon.setVisibility(View.VISIBLE);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            galleryIcon.setColorFilter(filter);
        }
        buttonOn();
    }

    // method that disables the buttons in the Input screen
    public void buttonOff(){
        Button addButton = findViewById(R.id.add_button);
        Button galleryButton = findViewById(R.id.gallery_button);
        addButton.setEnabled(false);
        galleryButton.setEnabled(false);
        galleryButton.setText("loading..");
//        addButton.setBackgroundColor(Color.parseColor("#cacaca"));
//        galleryButton.setBackgroundColor(Color.parseColor("#cacaca"));
//        addButton.setTextColor(Color.parseColor("#363636"));
//        galleryButton.setTextColor(Color.parseColor("#363636"));
    }

    // method that enables the buttons in the Input screen
    public void buttonOn(){
        Button addButton = findViewById(R.id.add_button);
        Button galleryButton = findViewById(R.id.gallery_button);
        addButton.setEnabled(true);
        galleryButton.setEnabled(true);
        galleryButton.setText("gallery");
//        addButton.setBackgroundColor(Color.parseColor("#ffff"));
//        galleryButton.setBackgroundColor(Color.parseColor("#ffff"));
//        addButton.setTextColor(Color.parseColor("#0000"));
//        galleryButton.setTextColor(Color.parseColor("#0000"));
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

    // widgets
    Uri newUri;
    String uploadUri;
    StorageReference ref;
    String location;
    Boolean duplicate = false;
    TextView descriptionInput;
    LatLng coordinates;
    ArrayList<String> allPlaceNames = new ArrayList<>();
    ArrayList<String> oldPlaceNames;

    //vars
    public int done = 0;
    public boolean appended = false;

    // method that pushes all the location information to firebase database
    private void postAll() {

        // get location and description
        location = searchLocation.getText().toString();
        descriptionInput = findViewById(R.id.description_text);
        String description = descriptionInput.getText().toString();
        if (description.isEmpty()) {
            description = "empty";
        }

        // getting LatLng of location
        Geocoder geocoder = new Geocoder(InputActivity.this);
        List<Address> list;
        try {
            list = geocoder.getFromLocationName(location, 1);
            Address address = list.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            coordinates = new LatLng(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // push location, description, coordinate(LatLng) and pictures(in an array) to Firebase
        databaseReference = databaseReference.child(location);
        Post post = new Post(location, urls, description, coordinates);
        databaseReference.setValue(post);

        // insert new location name to array with all added location names
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get existing array with added location names
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    oldPlaceNames = (ArrayList) ds.child("places").getValue();
                }

                // using if loop as a callback
                if (done == 0) {
                    for (int i = 0; i < oldPlaceNames.size(); i++) {
                        allPlaceNames.add(oldPlaceNames.get(i));
                        done++;
                        if (oldPlaceNames.get(i).equals(location)){
                            duplicate = true;
                        }
                    }

                    // adding new location to the existing array
                    if (done == oldPlaceNames.size() && !appended) {
                        appended = true;
                        if (!duplicate){
                            allPlaceNames.add(location);
                        }
                        databaseReference = databaseReference.child(user).child("places");
                        databaseReference.setValue(allPlaceNames);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something went wrong");
            }
        });
    }
}
