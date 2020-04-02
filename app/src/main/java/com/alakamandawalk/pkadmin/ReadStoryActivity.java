package com.alakamandawalk.pkadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class ReadStoryActivity extends AppCompatActivity {

    ImageButton backIb;
    TextView titleTv, storyTv, dateTv;
    ImageView storyImg;

    String storyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);

        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");

        backIb = findViewById(R.id.backIb);
        titleTv = findViewById(R.id.titleTv);
        storyTv = findViewById(R.id.storyTv);
        storyImg = findViewById(R.id.storyImg);
        dateTv = findViewById(R.id.dateTv);

        checkUserStatus();

        loadStory();

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadStory() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story");
        Query query = ref.orderByChild("storyId").equalTo(storyId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String storyName = ds.child("storyName").getValue().toString();
                    String story = ds.child("story").getValue().toString();
                    String storyImage = ds.child("storyImage").getValue().toString();
                    String timeStamp  = ds.child("storyDate").getValue().toString();

                    java.util.Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(timeStamp));
                    String storyDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

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

                    storyTv.setText(story);
                    titleTv.setText(storyName);
                    dateTv.setText(storyDate);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReadStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkUserStatus() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){

        }else {
            startActivity(new Intent(ReadStoryActivity.this, LoginActivity.class));
            finish();
        }
    }
}
