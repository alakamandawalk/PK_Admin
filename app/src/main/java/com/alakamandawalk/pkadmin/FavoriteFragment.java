package com.alakamandawalk.pkadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    ListView obj;
    DBHelper mydb;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        obj = view.findViewById(R.id.listView1);

        mydb = new DBHelper(getActivity());
        ArrayList array_list = mydb.getAllStories();
        ArrayAdapter arrayAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, array_list);

        obj.setAdapter(arrayAdapter);
//        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                int id_To_Search = position + 1;
//
//                Bundle dataBundle = new Bundle();
//                dataBundle.putInt("id", id_To_Search);
//
//                Intent intent = new Intent(getActivity(),ReadStoryActivity.class);
//
//                intent.putExtras(dataBundle);
//                startActivity(intent);
//
//            }
//        });

        return view;
    }
}
