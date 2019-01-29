package com.example.mini_.pathless;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    // Widgets
    AlertDialog.Builder builder;
    ArrayList<String> images;
    ArrayList newPlaces = new ArrayList();
    Boolean delete = true;
    String placeName;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    ArrayList placesArray;
    String user;
    StorageReference storageReference;
    String location;
    TextView locDescription;
    ViewPager viewPager;

    // Vars
    public int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Getting the clicked location
        Intent intent = getIntent();
        location = intent.getStringExtra("location");

        // Setting up the Firebase authentication, storage and database
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(user);

        // The event listener in order to read values from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(delete){
                    showData(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "database connection failed",
                        Toast.LENGTH_SHORT);
            }
        });

        // setting up the pop up screen for deleting a location
        builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Delete location");
        builder.setMessage("Are you sure you want to delete this location?");

        // the cancel button of the dialog pop pup
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // the delete button of the dialog pop up
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteLocation();
            }
        });
    }

    // Puts the delete item on the top right of the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        return true;
    }

    // Controls the action on an item in the actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Delete location if bin icon is clicked
        if (item.toString().equals("Refresh")) {
            builder.show();
        }

        // Go back to map screen when back arrow is clicked
        else {
            Intent intent = new Intent(DetailActivity.this, MapActivity.class);
            startActivity(intent);
        }
        return true;
    }

    // Getting the specific location's data from the database.
    private void showData(DataSnapshot dataSnapshot) {
        placesArray = (ArrayList) dataSnapshot.child("places").getValue();
        LocationInformation locInfo = new LocationInformation();
        locInfo.setLocation(dataSnapshot.child(location).getValue(
                LocationInformation.class).getLocation());
        locInfo.setUrls(dataSnapshot.child(location).getValue(LocationInformation.class).getUrls());
        locInfo.setDescription(dataSnapshot.child(location).getValue(
                LocationInformation.class).getDescription());

        // Getting the urls and use them in the image adapter.
        images = locInfo.getUrls();
        viewPager = findViewById(R.id.imageSlider);
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(imageSliderAdapter);

        // Setting up the indicator for the image slider.
        // ViewPagerIndicator project from Jake Wharton (github).
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

        // Show description of the location.
        String description = locInfo.getDescription();
        if (description == "empty"){
            description = "";
        }
        locDescription = findViewById(R.id.location_text);
        locDescription.setText(description);
    }

    // this method deletes a location input
    public void DeleteLocation() {

        // Iterating through all the added places
        delete = false;
        for(int i = 0; i < placesArray.size(); i++) {
            placeName = String.valueOf(placesArray.get(i));

            // adding all the places except the that needs to be deleted to a new array
            if(!placeName.equals(location)){
                newPlaces.add(placeName);
            }
        }

        // adding the new array (without the deleted location) to the database
        databaseReference = databaseReference.child("places");
        databaseReference.setValue(newPlaces).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(DetailActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // delete the location data from the database
        FirebaseDatabase.getInstance().getReference(user).child(location).removeValue();
    }
}
