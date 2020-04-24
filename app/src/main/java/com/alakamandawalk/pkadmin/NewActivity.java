package com.alakamandawalk.pkadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alakamandawalk.pkadmin.playlist.NewPlaylistActivity;

public class NewActivity extends AppCompatActivity {

    Button newCategoryBtn, newPlaylistBtn, newStoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        newCategoryBtn = findViewById(R.id.newCategoryBtn);
        newPlaylistBtn = findViewById(R.id.newPlaylistBtn);
        newStoryBtn = findViewById(R.id.newStoryBtn);

        newCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewActivity.this, NewCategoryActivity.class));
            }
        });

        newPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewActivity.this, NewPlaylistActivity.class));
            }
        });

        newStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NewActivity.this, AddOrEditStoryActivity.class);
                intent.putExtra("addOrEditKey", "edit");
                startActivity(intent);
            }
        });
    }
}
