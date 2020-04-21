package com.alakamandawalk.pkadmin.download;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.ReadStoryActivity;

public class DownloadedStoryAdapter extends RecyclerView.Adapter<DownloadedStoryAdapter.FavStoryViewHolder> {

    Context context;
    Cursor cursor;

    public DownloadedStoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public FavStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.download_story_row, parent, false);
        return new FavStoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavStoryViewHolder holder, int position) {

        if (!cursor.moveToPosition(position)){
            return;
        }

        final String storyId = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_ID));
        final String storyName = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_NAME));
        String storyDate = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_DATE));
        byte[] storyImage = cursor.getBlob(cursor.getColumnIndex(LocalDBContract.LocalDBEntry.KEY_IMAGE));

        Bitmap bmp = BitmapFactory.decodeByteArray(storyImage, 0, storyImage.length);

        try {
            holder.favStoryImg.setImageBitmap(bmp);
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            holder.favStoryImg.setImageResource(R.drawable.img_place_holder);
        }


        if (storyName.length()>27){

            holder.storyNameTv.setText(storyName.substring(0,25)+"...");

        }else {

            holder.storyNameTv.setText(storyName);
        }

        holder.storyDateTv.setText(storyDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ReadStoryActivity.class);
                intent.putExtra("isOnlineOffline", "offline");
                intent.putExtra("storyId",storyId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if (cursor!=null){
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor!=null){
            notifyDataSetChanged();
        }
    }

    class FavStoryViewHolder extends RecyclerView.ViewHolder{

        CardView favStoryCv;
        ImageView favStoryImg;
        TextView storyNameTv, storyDateTv;
        ImageButton optionIb;

        public FavStoryViewHolder(@NonNull View itemView) {
            super(itemView);

            favStoryCv = itemView.findViewById(R.id.favStoryCv);
            favStoryImg = itemView.findViewById(R.id.favStoryImg);
            storyNameTv = itemView.findViewById(R.id.storyNameTv);
            storyDateTv = itemView.findViewById(R.id.storyDateTv);
            optionIb = itemView.findViewById(R.id.optionIb);

        }
    }
}
