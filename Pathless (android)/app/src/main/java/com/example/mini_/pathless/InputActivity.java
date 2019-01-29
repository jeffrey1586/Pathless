package com.example.mini_.pathless;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This CirclePageIndicator is created by a viewPagerIndicator project
 * from JakeWharton's github.
 */

public class InputActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    // Widgets.
    ArrayList<String> allPlaceNames = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> urls = new ArrayList();
    ArrayList<String> oldPlaceNames;
    AutoCompleteTextView searchLocation;
    Boolean duplicate = false;
    Boolean upload = false;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;
    ImageSliderAdapter imageSliderAdapter;
    LatLng coordinates;
    StorageReference ref;
    StorageReference storageReference;
    String location;
    String user;
    String uploadUri;
    TextView descriptionInput;
    Uri newUri;
    Uri selectedUri;
    ViewPager viewPager;
    int grey = Color.parseColor("#9f9f9f");
    int black = Color.parseColor("#000000");
    Uri noImageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pathless-" +
            "android.appspot.com/o/images%2Fno_image.jpg?alt=media&token=50b7562c-5c11-" +
            "42bc-b785-5e51b0360265");

    // Vars.
    public int currentPage = 0;
    public int done = 0;
    public boolean appended = false;
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

        // Setup Firebase userId, storage and database.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(user);

        // Show no image picture.
        showImage(noImageUri);

        // Button to add pictures from gallery.
        Button buttonLoadImage = findViewById(R.id.gallery_button);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            // The onClick that brings user to the gallery.
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        // Setting up autocomplete for places in the edit text.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // Set the adapter to the edit text.
        mPlaceAutocompleteadapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        searchLocation = findViewById(R.id.location_text);
        searchLocation.setAdapter(mPlaceAutocompleteadapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Check which item is clicked on in the actionbar.
        if (item.toString().equals("Refresh")) {
            InputSend();
        }
        else {
            Intent intent = new Intent(InputActivity.this, MapActivity.class);
            startActivity(intent);
        }
        return true;
    }

    // The method for google api connection failed listener.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    // Function that saves and shows the pictures chosen from gallery.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedUri = data.getData();
            buttonOff();

            // Put the selected picture to the storage and add the url to array.
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

    // Method that posts all the location information to the database and storage
    public void InputSend() {
        // Check for a location input and added images.
        if (searchLocation.getText().toString().isEmpty()){
            Toast.makeText(InputActivity.this, "Please enter a location",
                    Toast.LENGTH_SHORT).show();
        }
        else if (urls.isEmpty()) {
            Toast.makeText(InputActivity.this, "Please add at least one image",
                    Toast.LENGTH_SHORT).show();
        }

        // Post all information and go to the main screen.
        else if (upload) {
            postAll();
            Intent intent = new Intent(InputActivity.this, MapActivity.class);
            startActivity(intent);
        }
    }

    // Method that shows the selected images in the InputActivity.
    public void showImage(Uri selectedUri){

        // Add no image picture to adapter.
        if (selectedUri == noImageUri){
            images.add(selectedUri.toString());
            imageSliderAdapter = new ImageSliderAdapter(this, images);
            images = new ArrayList<>();
        }

        // Add selected image to image slider.
        else {
            images.add(selectedUri.toString());
            imageSliderAdapter = new ImageSliderAdapter(this, images);
        }

        // Set the adapter for the image slider.
        viewPager = findViewById(R.id.image_selected);
        viewPager.setAdapter(imageSliderAdapter);
        buttonOn();

        // Setting up the indicator for the image slider,
        // ViewPagerIndicator project from Jake Wharton (github).
        if (selectedUri != noImageUri){
            CirclePageIndicator indicator = findViewById(R.id.indicator);
            indicator.setViewPager(viewPager);
            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }
                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }
                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });
        }
    }

    // Method that disables the buttons in the Input screen.
    public void buttonOff(){
        upload = false;
        Button galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setEnabled(false);
        galleryButton.setText("loading..");
        galleryButton.setTextColor(grey);
    }

    // Method that enables the buttons in the Input screen.
    public void buttonOn(){
        upload = true;
        Button galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setEnabled(true);
        galleryButton.setText("gallery");
        galleryButton.setTextColor(black);
    }

    // Method that pushes all the location information to Firebase database.
    private void postAll() {

        // Get new location.
        location = searchLocation.getText().toString();

        // Insert new location name to array with all added location names.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference(user);

                // Check if any location is added
                // and using done variable to prevent double appends to array.
                if (dataSnapshot.exists() && done == 0) {
                    oldPlaceNames = (ArrayList) dataSnapshot.child("places").getValue();
                    done++;

                    // Add already added places to new array.
                    for (int i = 0; i < oldPlaceNames.size(); i++) {
                        allPlaceNames.add(oldPlaceNames.get(i));

                        // Add new location to the new array if it does not exist in the array.
                        if (oldPlaceNames.get(i).equals(location)) {
                            duplicate = true;
                        }
                        Log.v(TAG, allPlaceNames.toString());
                    }

                    // If statement so array wil not be set multiple times to Firebase.
                    if (done == oldPlaceNames.size() && !appended) {
                        appended = true;

                        // Adding new location to the existing array.
                        if (!duplicate) {
                            allPlaceNames.add(location);
                        }
                        databaseReference = databaseReference.child("places");
                        databaseReference.setValue(allPlaceNames);
                    }
                }

                // If user has not yet added any places.
                else if (!dataSnapshot.exists() && done == 0){
                    allPlaceNames.add(location);
                    databaseReference = databaseReference.child("places");
                    databaseReference.setValue(allPlaceNames);
                    done = 1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something went wrong");
            }
        });

        // Get description.
        descriptionInput = findViewById(R.id.description_text);
        String description = descriptionInput.getText().toString();
        if (description.isEmpty()) {
            description = "empty";
        }

        // Getting LatLng of location.
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

        // Push location, description, coordinate(LatLng) and pictures(in an array) to Firebase.
        databaseReference = databaseReference.child(location);
        Post post = new Post(location, urls, description, coordinates);
        databaseReference.setValue(post);
    }
}
