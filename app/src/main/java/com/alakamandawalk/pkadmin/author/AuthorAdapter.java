package com.alakamandawalk.pkadmin.author;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.model.AuthorData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder>{

    Context context;
    List<AuthorData> authorDataList;

    public AuthorAdapter(Context context, List<AuthorData> authorDataList) {
        this.context = context;
        this.authorDataList = authorDataList;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.author_row, parent, false);

        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {

        String authorName = authorDataList.get(position).getAuthorName();
        final String authorId = authorDataList.get(position).getAuthorId();
        String authorProPic = authorDataList.get(position).getAuthorProfileImage();
        String authorCoverPic = authorDataList.get(position).getAuthorCoverImage();
        String authorPost = authorDataList.get(position).getAuthorPost();

        try {
            Picasso.get()
                    .load(authorCoverPic)
                    .placeholder(R.drawable.img_place_holder)
                    .fit()
                    .centerCrop()
                    .into(holder.authorCoverImg);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_place_holder).into(holder.authorCoverImg);
        }

        try {
            Picasso.get()
                    .load(authorProPic)
                    .placeholder(R.drawable.img_place_holder)
                    .fit()
                    .centerCrop()
                    .into(holder.authorProfileImg);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_place_holder).into(holder.authorCoverImg);
        }

        holder.authorNameTv.setText(authorName);
        holder.authorPostTv.setText(authorPost);
        setStoryCount(authorId, holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AuthorProfileActivity.class);
                intent.putExtra("authorId", authorId);
                context.startActivity(intent);
            }
        });
    }

    private void setStoryCount(String authorId, final AuthorViewHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("story");
        Query query = reference.orderByChild("authorId").equalTo(authorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String storyCount = dataSnapshot.getChildrenCount() + " STORIES";

                holder.storyCountTv.setText(storyCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return authorDataList.size();
    }

    static class AuthorViewHolder extends RecyclerView.ViewHolder{

        CardView authorCv;
        ImageView authorCoverImg, authorProfileImg;
        TextView authorNameTv, authorPostTv;
        TextView storyCountTv;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);

            authorCv = itemView.findViewById(R.id.authorCv);
            authorCoverImg = itemView.findViewById(R.id.authorCoverImg);
            authorProfileImg = itemView.findViewById(R.id.authorProfileImg);
            storyCountTv = itemView.findViewById(R.id.storyCountTv);
            authorNameTv = itemView.findViewById(R.id.authorNameTv);
            authorPostTv = itemView.findViewById(R.id.authorPostTv);

        }
    }
}
