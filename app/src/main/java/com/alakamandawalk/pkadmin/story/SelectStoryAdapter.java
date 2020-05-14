package com.alakamandawalk.pkadmin.story;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alakamandawalk.pkadmin.NewMsgActivity;
import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.model.StoryData;

import java.util.List;

public class SelectStoryAdapter extends RecyclerView.Adapter<SelectStoryAdapter.SelectStoryViewHolder> {

    Context context;
    List<StoryData> storyDataList;

    public SelectStoryAdapter(Context context, List<StoryData> storyDataList) {
        this.context = context;
        this.storyDataList = storyDataList;
    }

    @NonNull
    @Override
    public SelectStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.select_story_row, parent, false);

        return new SelectStoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectStoryViewHolder holder, int position) {

        final String storyId = storyDataList.get(position).getStoryId().toString();
        final String storyName = storyDataList.get(position).getStoryName().toString();

        holder.selectStoryNameTv.setText(storyName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMsgActivity.storyId = storyId;
                NewMsgActivity.attachStoryLayout.setVisibility(View.VISIBLE);
                NewMsgActivity.storyTitleTv.setText(storyName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyDataList.size();
    }

    class SelectStoryViewHolder extends RecyclerView.ViewHolder{

        TextView selectStoryNameTv;

        public SelectStoryViewHolder(@NonNull View itemView) {
            super(itemView);

            selectStoryNameTv = itemView.findViewById(R.id.selectStoryNameTv);

        }
    }
}
