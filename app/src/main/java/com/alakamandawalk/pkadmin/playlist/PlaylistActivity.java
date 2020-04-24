package com.alakamandawalk.pkadmin.playlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.home.StoryAdapter;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    ImageView playlistImageIv;
    RecyclerView playlistRv;
    TextView playlistNameTv;

    StoryAdapter storyAdapter;
    List<StoryData> storyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent intent = getIntent();
        String playlistId = intent.getStringExtra("playlistId");

        playlistImageIv = findViewById(R.id.playlistImageIv);
        playlistNameTv = findViewById(R.id.playlistNameTv);
        playlistRv = findViewById(R.id.playlistRv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        playlistRv.setLayoutManager(layoutManager);

        storyList = new ArrayList<>();

        loadStories(playlistId);
        loadPlaylist(playlistId);
    }

    private void loadPlaylist(String playlistId) {

        DatabaseReference playlistRef = FirebaseDatabase.getInstance().getReference("playlist");
        Query query = playlistRef.orderByChild("playlistId").equalTo(playlistId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String playlistName = ds.child("playlistName").getValue().toString();
                    String playlistImage = ds.child("playlistImage").getValue().toString();

                    try {
                        Picasso.get()
                                .load(playlistImage)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.img_place_holder)
                                .into(playlistImageIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(playlistImageIv);
                    }

                    playlistNameTv.setText(playlistName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadStories(String playlistId) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
        Query query = dbRef.orderByChild("storyPlaylistId").equalTo(playlistId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    StoryData storyData = ds.getValue(StoryData.class);

                    storyList.add(storyData);
                    storyAdapter = new StoryAdapter(PlaylistActivity.this, storyList);
                    playlistRv.setAdapter(storyAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlaylistActivity.this, ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
