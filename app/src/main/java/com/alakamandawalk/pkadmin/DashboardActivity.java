package com.alakamandawalk.pkadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    StoryAdapter storyAdapter;
    List<StoryData> storyList;

    RecyclerView storyRv;
    ImageButton menuIb;
    Toolbar toolbar;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        storyRv = findViewById(R.id.storyRv);
        menuIb = findViewById(R.id.menuIb);
        storyRv = findViewById(R.id.storyRv);

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        storyRv.setLayoutManager(layoutManager);

        storyList = new ArrayList<>();
        
        loadStories();

        menuIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, menuIb, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 0,0,"New Story");
                popupMenu.getMenu().add(Menu.NONE, 1,1,"Sign Out");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id==0){
                            startActivity(new Intent(DashboardActivity.this, NewStoryActivity.class));
                        }
                        if (id == 1){

                            final AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                            builder.setTitle("Sign Out");
                            builder.setMessage("are you sure..?");
                            builder.setPositiveButton("Sign out",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            firebaseAuth.signOut();
                                            checkUserStatus();
                                        }
                                    });
                            builder.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void loadStories() {

        pd.setMessage("Loading...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    StoryData storyData = ds.getValue(StoryData.class);

                    storyList.add(storyData);
                    storyAdapter = new StoryAdapter(DashboardActivity.this, storyList);
                    storyRv.setAdapter(storyAdapter);
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

    }

    private void checkUserStatus() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){

        }else {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
}
