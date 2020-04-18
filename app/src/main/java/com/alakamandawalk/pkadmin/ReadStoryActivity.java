package com.alakamandawalk.pkadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
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

    String storyId;
    String storyName;
    String story;
    String storyImage;
    String storyDate;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);

        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");

        localDb = new DBHelper(this);

        pd = new ProgressDialog(this);

        backIb = findViewById(R.id.backIb);
        favIb = findViewById(R.id.favIb);
        titleTv = findViewById(R.id.titleTv);
        storyTv = findViewById(R.id.storyTv);
        storyImg = findViewById(R.id.storyImg);
        dateTv = findViewById(R.id.dateTv);

        checkUserStatus();

        loadStory();
        isOnFav();

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        favIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrRemoveFav();
            }
        });

    }

    private void isOnFav() {

        DatabaseReference userDBRef = FirebaseDatabase.getInstance().getReference("user").child(uid);
        Query sIdQuery = userDBRef.orderByChild("storyId").equalTo(storyId);
        sIdQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    isFav = true;
                    favIb.setImageResource(R.drawable.ic_fav_light);
                }else {
                    isFav=false;
                    favIb.setImageResource(R.drawable.ic_fav_boder_light);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReadStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addOrRemoveFav() {

        if (isFav){

            DatabaseReference userDBRef = FirebaseDatabase.getInstance().getReference("user").child(uid);
            Query query = userDBRef.orderByChild("storyId").equalTo(storyId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                        localDb.deleteStory(storyId);
                        Toast.makeText(ReadStoryActivity.this, "Removed from favorites :)", Toast.LENGTH_SHORT).show();
                        isOnFav();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ReadStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    isOnFav();
                }
            });
        }else {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("storyId", storyId);

            DatabaseReference userDBRef = FirebaseDatabase.getInstance().getReference("user");
            userDBRef.child(uid).child(storyId).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Bitmap bitmap = ((BitmapDrawable)storyImg.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            final byte[] data = baos.toByteArray();

                            localDb.insertStory(storyId,storyName,story,storyDate,data);

                            Toast.makeText(ReadStoryActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                            isOnFav();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ReadStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    isOnFav();
                }
            });
        }

    }



    private void loadStory() {

        pd.setMessage("Loading...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story");
        Query query = ref.orderByChild("storyId").equalTo(storyId);
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

                    if (storyName.length()>30){

                        titleTv.setText(storyName.substring(0,28)+"...");

                    }else {

                        titleTv.setText(storyName);
                    }

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
