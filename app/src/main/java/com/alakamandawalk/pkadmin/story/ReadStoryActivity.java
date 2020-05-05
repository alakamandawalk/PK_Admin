package com.alakamandawalk.pkadmin.story;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.LoginActivity;
import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.localdb.LocalDBContract;
import com.alakamandawalk.pkadmin.localdb.DBHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Locale;

public class ReadStoryActivity extends AppCompatActivity {

    ImageButton backIb;
    TextView titleTv, storyTv, dateTv;
    ImageView storyImg;
    ImageButton favIb;

    private boolean isFav = false;

    DBHelper localDb;

    FirebaseUser user;
    String uid;

    String storyName, story, storyImage, storyDate, storyCategoryId, storyPlaylistId, storySearchTag;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);

        Intent intent = getIntent();
        final String storyId = intent.getStringExtra("storyId");

        localDb = new DBHelper(this);

        pd = new ProgressDialog(this);

        backIb = findViewById(R.id.backIb);
        favIb = findViewById(R.id.favIb);
        titleTv = findViewById(R.id.titleTv);
        storyTv = findViewById(R.id.storyTv);
        storyImg = findViewById(R.id.storyImg);
        dateTv = findViewById(R.id.dateTv);

        checkUserStatus();

        isOnDownloads(storyId);

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        favIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOrRemove(storyId);
            }
        });

    }

    private void loadStoryOffline(String id) {

        Cursor cursor = localDb.getStory(id);
        cursor.moveToFirst();

        String storyName = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_NAME));
        String story = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_STORY));
        String storyDate = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_DATE));
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

    }

    private void isOnDownloads(String id) {

        Cursor cursor = localDb.getStory(id);
        cursor.moveToFirst();

        if (cursor.getCount()>0){

            isFav = true;
            favIb.setImageResource(R.drawable.ic_delete_holo_dark);
            loadStoryOffline(id);

        }else {
            isFav=false;
            favIb.setImageResource(R.drawable.ic_download_holo_dark);
            loadStoryOnline(id);
        }

    }

    private void downloadOrRemove(final String id) {

        if (isFav){

            pd.setMessage("removing...");

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
            builder.setTitle("Are you sure?");
            builder.setMessage("you want to delete this story from downloads?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    pd.show();
                    pd.setCanceledOnTouchOutside(false);

                    localDb.deleteStory(id);
                    Toast.makeText(ReadStoryActivity.this, "Removed!", Toast.LENGTH_SHORT).show();
                    isOnDownloads(id);
                    pd.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }else {

            pd.setMessage("downloading...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);

            Bitmap bitmap = ((BitmapDrawable)storyImg.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            final byte[] data = baos.toByteArray();

            localDb.insertStory(id, storyName, story, storyDate, storyCategoryId, storyPlaylistId, storySearchTag, data);

            Toast.makeText(ReadStoryActivity.this, "Added to favorites!", Toast.LENGTH_SHORT).show();
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
                    String timeStamp  = ds.child("storyDate").getValue().toString();

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

    private void checkUserStatus() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null){

            uid = user.getUid();

        }else {
            startActivity(new Intent(ReadStoryActivity.this, LoginActivity.class));
            finish();
        }
    }
}
