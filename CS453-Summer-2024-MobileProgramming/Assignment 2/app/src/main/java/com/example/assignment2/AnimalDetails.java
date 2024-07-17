package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class AnimalDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        TextView nameTextView = findViewById(R.id.animalName);
        TextView descriptionTextView = findViewById(R.id.animalDescription);
        ImageView imageView = findViewById(R.id.animalImage);

        // Get data from the intent
        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        String description = getIntent().getStringExtra("description");

        // Set the name and description
        nameTextView.setText(name);
        descriptionTextView.setText(description);

        // Load the image from assets
        try {
            InputStream inputStream = getAssets().open(image);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
