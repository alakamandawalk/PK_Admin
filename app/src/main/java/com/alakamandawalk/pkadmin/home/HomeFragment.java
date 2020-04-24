package com.alakamandawalk.pkadmin.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.DashboardActivity;
import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.download.DownloadFragment;
import com.alakamandawalk.pkadmin.model.CategoryData;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    RecyclerView storyRv, categoryRv;
    StoryAdapter storyAdapter;
    List<StoryData> storyList;
    CategoryAdapter categoryAdapter;
    List<CategoryData> categoryList;

    ProgressDialog pd;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        storyRv = view.findViewById(R.id.storyRv);
        categoryRv = view.findViewById(R.id.categoryRv);
        pd = new ProgressDialog(getActivity());

        LinearLayoutManager categoryLayoutManager =
                new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        true);
        categoryLayoutManager.setStackFromEnd(true);
        categoryRv.setLayoutManager(categoryLayoutManager);


        LinearLayoutManager storyLayoutManager = new LinearLayoutManager(getActivity());
        storyLayoutManager.setStackFromEnd(true);
        storyLayoutManager.setReverseLayout(true);
        storyRv.setLayoutManager(storyLayoutManager);

        categoryList = new ArrayList<>();
        storyList = new ArrayList<>();

        checkNetworkStatus();

        return view;
    }

    private void loadStories() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    StoryData storyData = ds.getValue(StoryData.class);

                    storyList.add(storyData);
                    storyAdapter = new StoryAdapter(getActivity(), storyList);
                    storyRv.setAdapter(storyAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {

        pd.setMessage("Loading...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("category");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    CategoryData categoryData = ds.getValue(CategoryData.class);

                    categoryList.add(categoryData);
                    categoryAdapter = new CategoryAdapter(getActivity(), categoryList);
                    categoryRv.setAdapter(categoryAdapter);

                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    public void checkNetworkStatus(){

        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
            loadCategories();
            loadStories();
        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

        }

    }
}
