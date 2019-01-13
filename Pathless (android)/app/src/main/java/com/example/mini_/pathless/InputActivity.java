package com.example.mini_.pathless;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class InputActivity extends AppCompatActivity {

    TextView descriptionInput;
    TextView locationInput;
    ImageView imageview;
    Bitmap bitmap;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // setup the Firebase path
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("LOCATION_ENTRY");

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
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    // function that shows the pictures chosen from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // set image that is selected from the gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageview = findViewById(R.id.image_selected);
                imageview.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    // the click listener for the add button
    private class AddClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            postComment();
            Intent intent = new Intent(InputActivity.this, MapActivity.class);
            startActivity(intent);
        }
    }

    // this function pushes the pictures and text to the database
    private void postComment(){

        // get location and description
        descriptionInput = findViewById(R.id.description_text);
        locationInput = findViewById(R.id.location_text);
        String description = descriptionInput.getText().toString();
        String location = locationInput.getText().toString();

        // convert bitmap to string
        ByteArrayOutputStream biteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, biteStream);
        byte[] b = biteStream.toByteArray();
        String biteString = Base64.encodeToString(b, Base64.DEFAULT);

        // push location, description and pictures to Firebase
        databaseReference = databaseReference.child(location);
        Post post = new Post(location, description, biteString);
        databaseReference.setValue(post);
    }
}
