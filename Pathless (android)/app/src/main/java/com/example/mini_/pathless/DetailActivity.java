package com.example.mini_.pathless;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String location;
    ImageView pictureFrame;
    TextView locDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        location = intent.getStringExtra("location");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

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

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            LocationInformation locInfo = new LocationInformation();
            locInfo.setLocation(ds.child(location).getValue(LocationInformation.class).getLocation());
            locInfo.setDescription(ds.child(location).getValue(LocationInformation.class).getDescription());
            locInfo.setBitmap(ds.child(location).getValue(LocationInformation.class).getBitmap());

            pictureFrame = findViewById(R.id.imageSlider);
            byte[] encodeByte = Base64.decode(locInfo.getBitmap(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            pictureFrame.setImageBitmap(bitmap);

            locDescription = findViewById(R.id.location_text);
            locDescription.setText(locInfo.getDescription());
        }
    }
}
