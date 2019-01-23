package com.example.mini_.pathless;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

    //widgets
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String user;
    ArrayList<String> images;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    String location;
    TextView locDescription;
    ViewPager viewPager;

    //vars
    public int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // getting the clicked location
        Intent intent = getIntent();
        location = intent.getStringExtra("location");

        // setting up the Firedatabase authentication, storage and database
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // the event listener in order to read values from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // getting the specific location's data from the database
    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            LocationInformation locInfo = new LocationInformation();
            locInfo.setLocation(ds.child(location).getValue(
                    LocationInformation.class).getLocation());
            locInfo.setUrls(ds.child(location).getValue(LocationInformation.class).getUrls());
            locInfo.setDescription(ds.child(location).getValue(
                    LocationInformation.class).getDescription());

            // getting the urls and use them in the image adapter
            images = locInfo.getUrls();
            viewPager = findViewById(R.id.imageSlider);
            ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(this, images);
            viewPager.setAdapter(imageSliderAdapter);

            // setting up the indicator for the image slider
            // ViewPagerIndicator project from Jake Wharton (github)
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

            // Show description of the location
            String description = locInfo.getDescription();
            if (description == "empty"){
                description = "";
            }
            locDescription = findViewById(R.id.location_text);
            locDescription.setText(description);
        }
    }
}
