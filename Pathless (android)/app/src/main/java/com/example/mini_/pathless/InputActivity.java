package com.example.mini_.pathless;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class InputActivity extends AppCompatActivity {

    TextView descriptionInput;
    TextView locationInput;
    ImageView imageview;
    Bitmap bitmap;
    Uri selectedUri;
    String user;
    String location;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference ref;
    String newUri;
    ArrayList<Uri> listUris = new ArrayList();
    ArrayList<String> urls = new ArrayList();
    private static int RESULT_LOAD_IMAGE = 1;

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
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    // function that shows the pictures chosen from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // show image that is selected from the gallery and add to array
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                imageview = findViewById(R.id.image_selected);
                imageview.setImageBitmap(bitmap);
                listUris.add(selectedUri);
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

        // add images to storage Firebase
        for (int i = 0; i < listUris.size(); i++) {
            Uri uri = listUris.get(i);
            Date currentTime = Calendar.getInstance().getTime();
            ref = storageReference.child("images/" + currentTime + uri.getLastPathSegment());
            ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            newUri = task.getResult().toString();
                            urls.add(newUri);
                            if (urls.size() == listUris.size()){
                                postAll();
                            }
                        }
                    });
                }
            });
        }
    }

    private void postAll() {
        // get location and description
        locationInput = findViewById(R.id.location_text);
        location = locationInput.getText().toString();
        descriptionInput = findViewById(R.id.description_text);
        String description = descriptionInput.getText().toString();
        if (description.isEmpty()) {
            description = "empty";
        }

        // push location, description and pictures (in array) to Firebase
        databaseReference = databaseReference.child(location);
        Post post = new Post(location, urls, description);
        databaseReference.setValue(post);
    }

}
