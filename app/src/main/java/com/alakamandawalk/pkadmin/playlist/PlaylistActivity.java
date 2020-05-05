package com.alakamandawalk.pkadmin.playlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.category.NewCategoryActivity;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    ImageView playlistImageIv;
    ImageButton backIb, optionIb;
    RecyclerView playlistRv;
    TextView playlistNameTv;

    PlaylistAdapter playlistAdapter;
    List<StoryData> storyList;

    String playlistImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent intent = getIntent();
        final String playlistId = intent.getStringExtra("playlistId");

        playlistImageIv = findViewById(R.id.playlistImageIv);
        playlistNameTv = findViewById(R.id.playlistNameTv);
        playlistRv = findViewById(R.id.playlistRv);
        backIb = findViewById(R.id.backIb);
        optionIb = findViewById(R.id.optionIb);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        playlistRv.setLayoutManager(layoutManager);

        storyList = new ArrayList<>();

        loadStories(playlistId);
        loadPlaylist(playlistId);

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        optionIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(PlaylistActivity.this, optionIb, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 0,0,"Delete");
                popupMenu.getMenu().add(Menu.NONE, 1,1,"Edit");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id==0){

                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PlaylistActivity.this, R.style.AlertDialogTheme);
                            builder.setTitle("Delete");
                            builder.setMessage("are you sure..?");
                            builder.setPositiveButton("delete",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deletePlaylist(playlistId, playlistImage);
                                        }
                                    });
                            builder.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            builder.create().show();

                        }

                        if (id==1){
                            Intent intent = new Intent(PlaylistActivity.this, NewPlaylistActivity.class);
                            intent.putExtra("key", "edit");
                            intent.putExtra("playlistId", playlistId);
                            startActivity(intent);
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void deletePlaylist(final String playlistId, String playlistImage) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("deleting playlist!");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(playlistImage);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                DatabaseReference playlistRef = FirebaseDatabase.getInstance().getReference("playlist");
                Query query = playlistRef.orderByChild("playlistId").equalTo(playlistId);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ds.getRef().removeValue();

                            pd.dismiss();
                            Toast.makeText(PlaylistActivity.this, "playlist deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        pd.dismiss();
                        Toast.makeText(PlaylistActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PlaylistActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPlaylist(String playlistId) {

        DatabaseReference playlistRef = FirebaseDatabase.getInstance().getReference("playlist");
        Query query = playlistRef.orderByChild("playlistId").equalTo(playlistId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String playlistName = ds.child("playlistName").getValue().toString();
                    playlistImage = ds.child("playlistImage").getValue().toString();

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
                    playlistAdapter = new PlaylistAdapter(PlaylistActivity.this, storyList);
                    playlistRv.setAdapter(playlistAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlaylistActivity.this, ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
