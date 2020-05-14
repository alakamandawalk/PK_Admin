package com.alakamandawalk.pkadmin.explore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.NewMsgActivity;
import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.message.MessageAdapter;
import com.alakamandawalk.pkadmin.model.MessageData;
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
public class ExploreFragment extends Fragment {

    RecyclerView authorMsgRv;

    List<MessageData> msgList;
    MessageAdapter messageAdapter;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        authorMsgRv = view.findViewById(R.id.authorMsgRv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        authorMsgRv.setLayoutManager(layoutManager);
        authorMsgRv.setHasFixedSize(true);

        msgList = new ArrayList<>();

        loadMessages();

        return view;
    }

    private void loadMessages() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    MessageData messageData = ds.getValue(MessageData.class);

                    msgList.add(messageData);
                    messageAdapter = new MessageAdapter(msgList, getActivity());
                    authorMsgRv.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
