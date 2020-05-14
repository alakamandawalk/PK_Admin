package com.alakamandawalk.pkadmin.message;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.model.MessageData;
import com.alakamandawalk.pkadmin.story.ReadStoryActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    List<MessageData> msgList;
    Context context;

    public MessageAdapter(List<MessageData> msgList, Context context) {
        this.msgList = msgList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        String msg = msgList.get(position).getMessage().toString();
        String msgId = msgList.get(position).getMessageId().toString();
        String timeStamp = msgList.get(position).getMessageTime().toString();
        final String storyId = msgList.get(position).getStoryId().toString();
        String storyName = msgList.get(position).getStoryName().toString();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String msgTime = DateFormat.format("dd/MM/yyyy  HH:mm", calendar).toString();

        holder.messageTv.setText(msg);
        holder.timeTv.setText(msgTime);

        if (storyId.equals("noAttachment")){
            holder.storyTitleTv.setVisibility(View.GONE);
        } else {
            holder.storyTitleTv.setText(storyName);
        }

        holder.storyTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadStoryActivity.class);
                intent.putExtra("storyId", storyId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView messageTv, storyTitleTv, timeTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTv);
            storyTitleTv = itemView.findViewById(R.id.storyTitleTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
