package com.alakamandawalk.pkadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.category.CategoryActivity;
import com.alakamandawalk.pkadmin.message.MessageAdapter;
import com.alakamandawalk.pkadmin.model.MessageData;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.alakamandawalk.pkadmin.story.SelectStoryAdapter;
import com.alakamandawalk.pkadmin.story.StoryAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewMsgActivity extends AppCompatActivity {

    ImageButton attachmentIb, sendMsgIb, removeStoryIb;
    EditText msgEt;
    LinearLayout searchLayout;
    RecyclerView selectStoryRv, msgRv;
    SearchView searchView;
    public static RelativeLayout attachStoryLayout;
    public static TextView storyTitleTv;

    List<StoryData> storyDataList;
    SelectStoryAdapter storyAdapter;

    public static String storyId = "";
    private boolean showHideStoryRv = false;

    List<MessageData> msgList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg);

        attachmentIb = findViewById(R.id.attachmentIb);
        sendMsgIb = findViewById(R.id.sendMsgIb);
        msgEt = findViewById(R.id.msgEt);
        searchLayout = findViewById(R.id.searchLayout);
        selectStoryRv = findViewById(R.id.selectStoryRv);
        msgRv = findViewById(R.id.msgRv);
        searchView = findViewById(R.id.searchView);
        storyTitleTv = findViewById(R.id.storyTitleTv);
        removeStoryIb = findViewById(R.id.removeStoryIb);
        attachStoryLayout = findViewById(R.id.attachStoryLayout);
        attachStoryLayout.setVisibility(View.GONE);

        storyDataList = new ArrayList<>();
        msgList = new ArrayList<>();

        LinearLayoutManager SelectStoryRvLM = new LinearLayoutManager(this);
        SelectStoryRvLM.setReverseLayout(true);
        SelectStoryRvLM.setStackFromEnd(true);
        selectStoryRv.setLayoutManager(SelectStoryRvLM);

        LinearLayoutManager msgRvLM = new LinearLayoutManager(this);
        msgRvLM.setStackFromEnd(true);
        msgRv.setLayoutManager(msgRvLM);
        msgRv.setHasFixedSize(true);
        loadMessages();

        attachmentIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showHideStoryRv){
                    showHideStoryRv = false;
                    searchLayout.setVisibility(View.GONE);

                }else {
                    showHideStoryRv = true;
                    searchLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    loadSearchList(query);
                }else {
                    storyDataList.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    loadSearchList(newText);
                }else {
                    storyDataList.clear();
                }
                return false;
            }
        });

        removeStoryIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyId = "";
                storyTitleTv.setText("");
                attachStoryLayout.setVisibility(View.GONE);
            }
        });

        sendMsgIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = msgEt.getText().toString().trim();
                String attachedStoryId;
                String attachedStoryName;

                if (TextUtils.isEmpty(message)){
                    return;
                }

                if (!storyId.equals("")){
                    attachedStoryId = storyId;
                    attachedStoryName = storyTitleTv.getText().toString().trim();

                    sendMessage(message, attachedStoryId, attachedStoryName);
                } else {
                    sendMessage(message, "noAttachment", "noStory");
                }
            }
        });
    }

    private void sendMessage(String message, String attachedStoryId, String attachedStoryName) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("messageId", timeStamp);
        hashMap.put("messageTime", timeStamp);
        hashMap.put("storyId", attachedStoryId);
        hashMap.put("storyName", attachedStoryName);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("message");
        reference.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storyId = "";
                        storyTitleTv.setText("");
                        attachStoryLayout.setVisibility(View.GONE);
                        msgEt.setText("");
                        loadMessages();
                    }
                });
    }

    private void loadMessages() {

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    MessageData messageData = ds.getValue(MessageData.class);

                    msgList.add(messageData);
                    messageAdapter = new MessageAdapter(msgList, NewMsgActivity.this);
                    msgRv.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewMsgActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadSearchList(final String searchText) {

        DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference("story");
        searchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyDataList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    StoryData storyData = ds.getValue(StoryData.class);

                    if (storyData.getStoryName().contains(searchText) || storyData.getStorySearchTag().contains(searchText)){
                        storyDataList.add(storyData);
                    }

                    storyAdapter = new SelectStoryAdapter(NewMsgActivity.this, storyDataList);
                    selectStoryRv.setAdapter(storyAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showHideStoryRv = false;
        searchLayout.setVisibility(View.GONE);
    }
}
