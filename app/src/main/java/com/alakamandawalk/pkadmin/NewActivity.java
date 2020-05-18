package com.alakamandawalk.pkadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alakamandawalk.pkadmin.author.NewAuthorActivity;
import com.alakamandawalk.pkadmin.category.NewCategoryActivity;
import com.alakamandawalk.pkadmin.message.NewMsgActivity;
import com.alakamandawalk.pkadmin.playlist.NewPlaylistActivity;
import com.alakamandawalk.pkadmin.story.AddOrEditStoryActivity;

public class NewActivity extends AppCompatActivity {

    Button newCategoryBtn, newPlaylistBtn, newStoryBtn, newMsgBtn, newAuthorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        newCategoryBtn = findViewById(R.id.newCategoryBtn);
        newPlaylistBtn = findViewById(R.id.newPlaylistBtn);
        newStoryBtn = findViewById(R.id.newStoryBtn);
        newMsgBtn = findViewById(R.id.newMsgBtn);
        newAuthorBtn = findViewById(R.id.newAuthorBtn);

        newCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivity.this, NewCategoryActivity.class);
                intent.putExtra("key", "add");
                startActivity(intent);
            }
        });

        newPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivity.this, NewPlaylistActivity.class);
                intent.putExtra("key", "add");
                startActivity(intent);
            }
        });

        newStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NewActivity.this, AddOrEditStoryActivity.class);
                intent.putExtra("key", "add");
                startActivity(intent);
            }
        });

        newMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewActivity.this, NewMsgActivity.class));
            }
        });

        newAuthorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewActivity.this, NewAuthorActivity.class));
            }
        });

    }
}
