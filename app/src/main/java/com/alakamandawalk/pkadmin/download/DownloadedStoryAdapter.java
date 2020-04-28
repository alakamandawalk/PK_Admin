package com.alakamandawalk.pkadmin.download;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.category.NewCategoryActivity;
import com.alakamandawalk.pkadmin.localdb.DBHelper;
import com.alakamandawalk.pkadmin.story.ReadStoryActivity;
import com.alakamandawalk.pkadmin.localdb.LocalDBContract;

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
    public void onBindViewHolder(@NonNull final FavStoryViewHolder holder, int position) {

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
                intent.putExtra("storyId",storyId);
                context.startActivity(intent);
            }
        });

        holder.optionIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.optionIb, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 0,0,"Delete");
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
                                            deleteStory(storyId);
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

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void deleteStory(String storyId) {

        DBHelper localDb = new DBHelper(context);
        localDb.deleteStory(storyId);
        swapCursor(cursor);
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
