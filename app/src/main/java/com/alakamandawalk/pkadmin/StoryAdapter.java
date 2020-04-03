package com.alakamandawalk.pkadmin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder>{

    Context context;
    List<StoryData> storyDataList;

    public StoryAdapter(Context context, List<StoryData> storyDataList) {
        this.context = context;
        this.storyDataList = storyDataList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.story_row, parent, false);

        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryViewHolder holder, int position) {

        final String storyId = storyDataList.get(position).getStoryId();
        final String storyImage = storyDataList.get(position).getStoryImage();
        String storyName = storyDataList.get(position).getStoryName();
        String timeStamp = storyDataList.get(position).getStoryDate();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String storyDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        try {
            Picasso.get()
                    .load(storyImage)
                    .placeholder(R.drawable.img_place_holder)
                    .fit()
                    .centerCrop()
                    .into(holder.storyImageIv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_place_holder).into(holder.storyImageIv);
        }

        holder.storyNameTv.setText(storyName);
        holder.storyDateTv.setText(storyDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ReadStoryActivity.class);
                intent.putExtra("storyId",storyId);
                context.startActivity(intent);
            }
        });

        holder.optionIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.optionIb, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 0,0,"Delete");
                popupMenu.getMenu().add(Menu.NONE, 1,1,"Edit");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id==0){

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete");
                            builder.setMessage("are you sure..?");
                            builder.setPositiveButton("delete",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteStory(storyId, storyImage);
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

                        if (id==1){
                            Intent intent = new Intent(context, UpdateStoryActivity.class);
                            intent.putExtra("storyId",storyId);
                            context.startActivity(intent);
                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });

    }

    private void deleteStory(final String storyId, String storyImage) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(storyImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Query query = FirebaseDatabase.getInstance().getReference("story").orderByChild("storyId").equalTo(storyId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();

                                    pd.dismiss();
                                    Toast.makeText(context, "Story deleted :)", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                pd.dismiss();
                                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return storyDataList.size();
    }

    class StoryViewHolder extends RecyclerView.ViewHolder{

        CardView storyCv;
        ImageView storyImageIv;
        TextView storyNameTv, storyDateTv;
        ImageButton optionIb;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            storyCv = itemView.findViewById(R.id.storyCv);
            storyImageIv = itemView.findViewById(R.id.storyImageIv);
            storyNameTv = itemView.findViewById(R.id.storyNameTv);
            storyDateTv = itemView.findViewById(R.id.storyDateTv);
            optionIb = itemView.findViewById(R.id.optionIb);

        }
    }
}


