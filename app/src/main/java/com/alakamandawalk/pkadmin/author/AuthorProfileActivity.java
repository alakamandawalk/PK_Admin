package com.alakamandawalk.pkadmin.author;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsFragment;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.alakamandawalk.pkadmin.story.StoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AuthorProfileActivity extends AppCompatActivity {

    ImageButton backIb;
    ImageView authorCoverImg, authorProfileImg;
    TextView storyCountTv, authorNameTv, authorPostTv, authorDescriptionTv;
    RecyclerView authorStoryRv;

    StoryAdapter storyAdapter;
    List<StoryData> storyDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_profile);

        Intent intent = getIntent();
        String authorId = intent.getStringExtra("authorId");

        backIb = findViewById(R.id.backIb);
        authorCoverImg = findViewById(R.id.authorCoverImg);
        authorProfileImg = findViewById(R.id.authorProfileImg);
        storyCountTv = findViewById(R.id.storyCountTv);
        authorNameTv = findViewById(R.id.authorNameTv);
        authorPostTv = findViewById(R.id.authorPostTv);
        authorDescriptionTv = findViewById(R.id.authorDescriptionTv);
        authorStoryRv = findViewById(R.id.authorStoryRv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        authorStoryRv.setLayoutManager(layoutManager);

        storyDataList = new ArrayList<>();

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadAuthorDetails(authorId);
        loadAuthorStories(authorId);
    }

    private void loadAuthorStories(String authorId) {

        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference("story");
        Query query = storyRef.orderByChild("authorId").equalTo(authorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyDataList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    StoryData storyData = ds.getValue(StoryData.class);

                    storyDataList.add(storyData);
                    storyAdapter = new StoryAdapter(AuthorProfileActivity.this, storyDataList);
                    authorStoryRv.setAdapter(storyAdapter);
                    storyCountTv.setText(storyDataList.size() + " STORIES");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AuthorProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAuthorDetails(String authorId) {

        DatabaseReference authorRef = FirebaseDatabase.getInstance().getReference("author");
        Query query = authorRef.orderByChild("authorId").equalTo(authorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String authorName = ds.child("authorName").getValue().toString();
                    String authorPost = ds.child("authorPost").getValue().toString();
                    String authorDescription = ds.child("authorDescription").getValue().toString();
                    String authorCoverImage = ds.child("authorCoverImage").getValue().toString();
                    String authorProfileImage = ds.child("authorProfileImage").getValue().toString();

                    authorNameTv.setText(authorName);
                    authorPostTv.setText(authorPost);
                    authorDescriptionTv.setText(authorDescription);

                    try {
                        Picasso.get()
                                .load(authorCoverImage)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.img_place_holder)
                                .into(authorCoverImg);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(authorCoverImg);
                    }

                    try {
                        Picasso.get()
                                .load(authorProfileImage)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.img_place_holder)
                                .into(authorProfileImg);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(authorProfileImg);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AuthorProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
