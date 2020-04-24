package com.alakamandawalk.pkadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.alakamandawalk.pkadmin.download.DownloadFragment;
import com.alakamandawalk.pkadmin.explore.ExploreFragment;
import com.alakamandawalk.pkadmin.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;


    ImageButton menuIb;
    TextView titleTv;

    FrameLayout frameLayout;
    BottomNavigationView bottomNav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        checkUserStatus();

        frameLayout = findViewById(R.id.frameLayout);
        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnNavigationItemSelectedListener(selectedListener);

        menuIb = findViewById(R.id.menuIb);
        titleTv = findViewById(R.id.titleTv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadFirstActivity();

        menuIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, menuIb, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 0,0,"New");
                popupMenu.getMenu().add(Menu.NONE, 1,1,"Sign Out");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id==0){
                            startActivity(new Intent(DashboardActivity.this, NewActivity.class));
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

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

            switch (item.getItemId()){
                case R.id.nav_explore:

                    titleTv.setText("Explore");
                    ExploreFragment exploreFragment = new ExploreFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.frameLayout, exploreFragment, "");
                    ft3.commit();
                    return true;

                case R.id.nav_home:

                    titleTv.setText("Home");
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.frameLayout, homeFragment, "");
                    ft1.commit();

                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                            || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {

                    }
                    else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                            || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                        builder.setTitle("Not Connected!");
                        builder.setMessage("you can still read downloaded stories!");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                titleTv.setText("Downloads");
                                DownloadFragment downloadFragment = new DownloadFragment();
                                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                                ft2.replace(R.id.frameLayout, downloadFragment, "");
                                ft2.commit();
                                item.setChecked(true);

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    return true;

                case R.id.nav_fav:

                    titleTv.setText("Downloads");
                    DownloadFragment downloadFragment = new DownloadFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.frameLayout, downloadFragment, "");
                    ft2.commit();
                    return true;
            }

            return false;
        }
    };

    private void checkUserStatus() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){

        }else {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void loadFirstActivity(){

        titleTv.setText("Home");
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.frameLayout, homeFragment, "");
        ft1.commit();

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {


        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Not Connected!");
            builder.setMessage("you can still read downloaded stories!");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    titleTv.setText("Downloads");
                    DownloadFragment downloadFragment = new DownloadFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.frameLayout, downloadFragment, "");
                    ft2.commit();

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
