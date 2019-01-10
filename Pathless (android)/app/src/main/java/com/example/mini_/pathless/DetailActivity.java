package com.example.mini_.pathless;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    ImageView pictureFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

//        pictureFrame = findViewById(R.id.imageSlider);
//
//        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//                encodeByte.length);
//
//        pictureFrame.setImageBitmap(bitmap);
    }
}
