package com.alakamandawalk.pkadmin.story;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.author.AuthorProfileActivity;
import com.alakamandawalk.pkadmin.localdb.LocalDBContract;
import com.alakamandawalk.pkadmin.localdb.DBHelper;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.alakamandawalk.pkadmin.playlist.PlaylistActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ReadStoryActivity extends AppCompatActivity {

    ImageButton authorIb, downloadIb, playListIb, relatedStoriesIb;
    TextView titleTv, storyTv, dateTv, authorNameTv, downloadBtnTipTv;
    ImageView storyImg;
    RelativeLayout showRelRl;
    ProgressBar relStoryPb;
    RecyclerView relatedStoryRv;

    private boolean isDownloaded = false;
    private boolean showRel = false;

    DBHelper localDb;

    String storyId, storyName, story, storyImage, storyDate, storyCategoryId, storyPlaylistId, storySearchTag;
    String authorId, authorName;

    ProgressDialog pd;

    RelatedStoryAdapter relatedStoryAdapter;
    List<StoryData> relStoryList;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);

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
        storyId = intent.getStringExtra("storyId");

        localDb = new DBHelper(this);

        pd = new ProgressDialog(this);

        relatedStoryRv = findViewById(R.id.relatedStoryRv);
        relStoryPb = findViewById(R.id.relStoryPb);
        showRelRl = findViewById(R.id.showRelRl);
        authorIb = findViewById(R.id.authorIb);
        playListIb = findViewById(R.id.playListIb);
        relatedStoriesIb = findViewById(R.id.relatedStoriesIb);
        downloadIb = findViewById(R.id.downloadIb);
        titleTv = findViewById(R.id.titleTv);
        storyTv = findViewById(R.id.storyTv);
        storyImg = findViewById(R.id.storyImg);
        dateTv = findViewById(R.id.dateTv);
        authorNameTv = findViewById(R.id.authorNameTv);
        downloadBtnTipTv = findViewById(R.id.downloadBtnTipTv);

        LinearLayoutManager relStoriesLm =
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        true);
        relStoriesLm.setStackFromEnd(true);
        relatedStoryRv.setLayoutManager(relStoriesLm);

        relStoryList = new ArrayList<>();
        showRel = false;
        showRelStories(storyCategoryId);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-7611458447394787/2180536786");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        isOnDownloads(storyId);

        downloadIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOrRemove(storyId);
            }
        });

        playListIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storyPlaylistId.length()>9){

                    String checkedPlaylist = storyPlaylistId.substring(0,10);

                    if (checkedPlaylist.equals("noplaylist")){

                        Toast.makeText(ReadStoryActivity.this, "no playlist!", Toast.LENGTH_SHORT).show();
                    }else{

                        Intent intent = new Intent(ReadStoryActivity.this, PlaylistActivity.class);
                        intent.putExtra("playlistId",storyPlaylistId);
                        startActivity(intent);
                    }
                }else {

                    Intent intent = new Intent(ReadStoryActivity.this, PlaylistActivity.class);
                    intent.putExtra("playlistId",storyPlaylistId);
                    startActivity(intent);
                }
            }
        });

        authorIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadStoryActivity.this, AuthorProfileActivity.class);
                intent.putExtra("authorId", authorId);
                startActivity(intent);
            }
        });

        relatedStoriesIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showRel){
                    showRel=false;
                    showRelStories(storyCategoryId);
                }else {
                    showRel=true;
                    showRelStories(storyCategoryId);
                }
            }
        });

    }

    private void showRelStories(String categoryId) {

        if (showRel){
            relatedStoryRv.setVisibility(View.GONE);
            showRelRl.setVisibility(View.VISIBLE);
            relStoryPb.setVisibility(View.VISIBLE);

            DatabaseReference relRef = FirebaseDatabase.getInstance().getReference("story");
            Query query = relRef.orderByChild("storyCategoryId").equalTo(categoryId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    relStoryList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        StoryData storyData = ds.getValue(StoryData.class);

                        if (!storyData.getStoryId().equals(storyId)){

                            relStoryList.add(storyData);
                            Collections.shuffle(relStoryList);
                            relatedStoryAdapter = new RelatedStoryAdapter(ReadStoryActivity.this, relStoryList);
                            relatedStoryRv.setAdapter(relatedStoryAdapter);
                            relStoryPb.setVisibility(View.GONE);
                            relatedStoryRv.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ReadStoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    relStoryPb.setVisibility(View.GONE);
                }
            });
        } else {
            showRelRl.setVisibility(View.GONE);
        }
    }

    private void
    loadStoryOffline(String id) {

        Cursor cursor = localDb.getStory(id);
        cursor.moveToFirst();

        storyName = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_NAME));
        story = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_STORY));
        storyDate = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_DATE));
        storyPlaylistId = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_PLAYLIST_ID));
        storyCategoryId = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_CATEGORY_ID));
        authorId = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_AUTHOR_ID));
        authorName = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_AUTHOR_NAME));
        byte[] storyImage = cursor.getBlob(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_IMAGE));

        if (!cursor.isClosed()){
            cursor.close();
        }

        Bitmap bmp = BitmapFactory.decodeByteArray(storyImage, 0, storyImage.length);

        try {
            storyImg.setImageBitmap(bmp);
        }catch (Exception e){
            Toast.makeText(ReadStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            storyImg.setImageResource(R.drawable.img_place_holder);
        }

        titleTv.setText(storyName);
        storyTv.setText(story);
        dateTv.setText(storyDate);
        authorNameTv.setText(authorName);
    }

    private void isOnDownloads(String id) {

        Cursor cursor = localDb.getStory(id);
        cursor.moveToFirst();

        if (cursor.getCount()>0){

            isDownloaded = true;
            downloadIb.setImageResource(R.drawable.ic_delete_holo_dark);
            downloadBtnTipTv.setText(getString(R.string.remove));
            loadStoryOffline(id);

        }else {
            isDownloaded=false;
            downloadIb.setImageResource(R.drawable.ic_download_holo_dark);
            downloadBtnTipTv.setText(getString(R.string.download));
            loadStoryOnline(id);
        }

    }

    private void downloadOrRemove(final String id) {

        if (isDownloaded){

            pd.setMessage(getString(R.string.removing));

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
            builder.setTitle(getString(R.string.delete));
            builder.setMessage(getString(R.string.delete_message));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    pd.show();
                    pd.setCanceledOnTouchOutside(false);

                    localDb.deleteStory(id);
                    Toast.makeText(ReadStoryActivity.this, "Removed!", Toast.LENGTH_SHORT).show();
                    isOnDownloads(id);
                    pd.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }else {

            pd.setMessage(getString(R.string.downloading));
            pd.show();
            pd.setCanceledOnTouchOutside(false);

            Bitmap bitmap = ((BitmapDrawable)storyImg.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            final byte[] data = baos.toByteArray();

            localDb.insertStory(id, storyName, story, storyDate, storyCategoryId, storyPlaylistId, storySearchTag,authorId, authorName, data);

            Toast.makeText(ReadStoryActivity.this, "Downloaded!", Toast.LENGTH_SHORT).show();
            isOnDownloads(id);
            pd.dismiss();

        }
    }

    private void loadStoryOnline(String id) {

        pd.setMessage("Loading...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story");
        Query query = ref.orderByChild("storyId").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    storyName = ds.child("storyName").getValue().toString();
                    story = ds.child("story").getValue().toString();
                    storyImage = ds.child("storyImage").getValue().toString();
                    storyCategoryId = ds.child("storyCategoryId").getValue().toString();
                    storyPlaylistId = ds.child("storyPlaylistId").getValue().toString();
                    storySearchTag = ds.child("storySearchTag").getValue().toString();
                    String timeStamp  = ds.child("storyDate").getValue().toString();
                    authorId = ds.child("authorId").getValue().toString();

                    java.util.Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(timeStamp));
                    storyDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

                    try {
                        Picasso.get()
                                .load(storyImage)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.img_place_holder)
                                .into(storyImg);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(storyImg);
                    }

                    titleTv.setText(storyName);
                    storyTv.setText(story);
                    dateTv.setText(storyDate);
                    getAuthorDetails();
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(ReadStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAuthorDetails() {

        DatabaseReference authorRef = FirebaseDatabase.getInstance().getReference("author");
        Query query = authorRef.orderByChild("authorId").equalTo(authorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    authorName = ds.child("authorName").getValue().toString();
                    authorNameTv.setText(authorName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReadStoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        showRel=false;
        showRelStories(storyCategoryId);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
