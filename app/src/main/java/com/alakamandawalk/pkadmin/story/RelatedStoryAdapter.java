package com.alakamandawalk.pkadmin.story;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RelatedStoryAdapter extends RecyclerView.Adapter<RelatedStoryAdapter.RelayedStoryViewHolder>{

    Context context;
    List<StoryData> dataList;

    public RelatedStoryAdapter(Context context, List<StoryData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RelayedStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.related_story_row, parent, false);

        return new RelayedStoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelayedStoryViewHolder holder, int position) {

        final String id = dataList.get(position).getStoryId().toString();
        String image = dataList.get(position).getStoryImage().toString();
        String name = dataList.get(position).getStoryName().toString();

        holder.storyNameTv.setText(name);

        try {
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.img_place_holder)
                    .fit()
                    .centerCrop()
                    .into(holder.storyImgIv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_place_holder).into(holder.storyImgIv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadStoryActivity.class);
                intent.putExtra("storyId",id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (dataList.size()<=5){
            return dataList.size();
        }else {
            return 5;
        }
    }

    class RelayedStoryViewHolder extends RecyclerView.ViewHolder{

        ImageView storyImgIv;
        TextView storyNameTv;

        public RelayedStoryViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImgIv = itemView.findViewById(R.id.storyImgIv);
            storyNameTv = itemView.findViewById(R.id.storyNameTv);
        }
    }
}
