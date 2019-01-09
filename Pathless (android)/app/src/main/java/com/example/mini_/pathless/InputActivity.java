package com.example.mini_.pathless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InputActivity extends AppCompatActivity {

    TextView descriptionInput;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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
        descriptionInput = findViewById(R.id.description_text);
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

    // this function pushes the information to the database
    private void postComment(){
        String description = descriptionInput.getText().toString();

        Post post = new Post(description);
        databaseReference.push().setValue(post);
    }

}
