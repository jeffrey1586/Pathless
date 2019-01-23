package com.example.mini_.pathless;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    // widgets
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String user;
    ArrayList<String> PlaceNames;
    String location;

    // vars
    private static final String TAG = "debugCheck";
    private GoogleMap mMap;
    public int done = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // user sign in to Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();

        // show the logo on top left
        ImageView logoImage = findViewById(R.id.logo_image);
        logoImage.setImageResource(R.drawable.pathless_mountain);

        // setting click listener on the floating action button
        FloatingActionButton newContent = findViewById(R.id.newEntry_button);
        newContent.setOnClickListener(new newEntryButtonClick());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);

        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }

        // setting up the Firedatabase authentication, storage and database
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // the event listener in order to read values from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collectCoordinates(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set click listener on the markers
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                LatLng position = m.getPosition();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                m.showInfoWindow();
                return true;
            }
        });

        // set click listener on the info window and put the location in the intent
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker m) {
                if (m.isInfoWindowShown()) {
                    String location = m.getTitle();
                    Intent intent = new Intent(MapActivity.this, DetailActivity.class);
                    intent.putExtra("location", location);
                    startActivity(intent);
                }
            }
        });
    }

    private void collectCoordinates(DataSnapshot dataSnapshot) {

        // get array with all location names
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            PlaceNames = (ArrayList) ds.child("places").getValue();

            // using the if loop to prevent
            if (done == 0) {
                done++;

                // get location name and LatLng
                for (int i = 0; i < PlaceNames.size(); i++) {
                    location = PlaceNames.get(i);
                    MarkerInformation markInfo = new MarkerInformation();
                    markInfo.setLocation(ds.child(location).getValue(
                            MarkerInformation.class).getLocation());
                    markInfo.setCoordinates(ds.child(location).getValue(
                            MarkerInformation.class).getCoordinates());

                    // set marker at location
                    double lat = markInfo.getCoordinates().get("latitude");
                    double lng = markInfo.getCoordinates().get("longitude");
                    LatLng marker = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(marker).title(
                            markInfo.getLocation()));
                }
            }
        }
    }

    // the click listener to make new location
    private class newEntryButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MapActivity.this, InputActivity.class);
            startActivity(intent);
        }
    }
}
