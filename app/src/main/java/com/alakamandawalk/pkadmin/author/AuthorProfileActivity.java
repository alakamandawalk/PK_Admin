package com.alakamandawalk.pkadmin.author;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
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
import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.alakamandawalk.pkadmin.story.StoryAdapter;
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
import java.util.Locale;

public class AuthorProfileActivity extends AppCompatActivity {

    ImageButton backIb, optionIb;
    ImageView authorCoverImg, authorProfileImg;
    TextView storyCountTv, authorNameTv, authorPostTv, authorDescriptionTv;
    RecyclerView authorStoryRv;

    String authorCoverImage, authorProfileImage;

    StoryAdapter storyAdapter;
    List<StoryData> storyDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_profile);

        Configuration configuration = new Configuration();
        setLocale(configuration);

        SharedPreferences themePref = getSharedPreferences(SettingsActivity.THEME_PREFERENCE, MODE_PRIVATE);
        boolean isDarkMode = themePref.getBoolean(SettingsActivity.KEY_IS_NIGHT_MODE, false);

        if (isDarkMode){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Intent intent = getIntent();
        final String authorId = intent.getStringExtra("authorId");

        optionIb = findViewById(R.id.optionIb);
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

        optionIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu(authorId);
            }
        });
    }

    private void showOptionMenu(final String authorId) {

        PopupMenu popupMenu = new PopupMenu(AuthorProfileActivity.this, optionIb, Gravity.END);
        popupMenu.getMenu().add(Menu.NONE, 0,0,"Edit");
        popupMenu.getMenu().add(Menu.NONE, 1,1,"Delete");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    Intent intent = new Intent(AuthorProfileActivity.this, NewAuthorActivity.class);
                    intent.putExtra("key", "edit");
                    intent.putExtra("authorId", authorId);
                    startActivity(intent);
                }
                if (id == 1){

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AuthorProfileActivity.this, R.style.AlertDialogTheme);
                    builder.setTitle("Delete");
                    builder.setMessage("are you sure..?");
                    builder.setPositiveButton("delete",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteAuthor(authorId);
                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
                return false;
            }
        });
        popupMenu.show();

    }

    private void deleteAuthor(final String authorId) {

        final ProgressDialog pd;
        pd = new ProgressDialog(AuthorProfileActivity.this);
        pd.setMessage("deleting cover image...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        StorageReference delCoverRef = FirebaseStorage.getInstance().getReferenceFromUrl(authorCoverImage);
        delCoverRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                pd.setMessage("deleting profile image...");

                StorageReference delProfileRef = FirebaseStorage.getInstance().getReferenceFromUrl(authorProfileImage);
                delProfileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        pd.setMessage("deleting author data...");

                        DatabaseReference delAuthorDataRef = FirebaseDatabase.getInstance().getReference("author");
                        Query query = delAuthorDataRef.orderByChild("authorId").equalTo(authorId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AuthorProfileActivity.this, "author deleted successfully!", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AuthorProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(AuthorProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AuthorProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AuthorProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
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
                    authorCoverImage = ds.child("authorCoverImage").getValue().toString();
                    authorProfileImage = ds.child("authorProfileImage").getValue().toString();

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

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
            setLocale(overrideConfiguration);
            applyOverrideConfiguration(overrideConfiguration);
        }
    }

    public void setLocale(Configuration config) {

        SharedPreferences languagePreference = getSharedPreferences(SettingsActivity.LANGUAGE_PREF, Context.MODE_PRIVATE);
        String lang =  languagePreference.getString(SettingsActivity.LANGUAGE_KEY, SettingsActivity.ENGLISH);
        String language;
        if (lang.equals(SettingsActivity.SINHALA)){

            language = SettingsActivity.SINHALA;
        }else {
            language = SettingsActivity.ENGLISH;
        }

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT>=17){
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
