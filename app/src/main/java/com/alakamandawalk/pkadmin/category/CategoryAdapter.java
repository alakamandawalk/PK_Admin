package com.alakamandawalk.pkadmin.category;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.alakamandawalk.pkadmin.model.CategoryData;
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

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    Context context;
    List<CategoryData> categoryList;

    public CategoryAdapter(Context context, List<CategoryData> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {

        final String categoryName = categoryList.get(position).getCategoryName();
        final String categoryImage = categoryList.get(position).getCategoryImage();
        final String categoryId = categoryList.get(position).getCategoryId();

        try {
            Picasso.get()
                    .load(categoryImage)
                    .placeholder(R.drawable.img_place_holder)
                    .fit()
                    .centerCrop()
                    .into(holder.categoryIv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_place_holder).into(holder.categoryIv);
        }

        holder.categoryNameTv.setText(categoryName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("key", "showCategoryList");
                intent.putExtra("categoryId",categoryId);
                intent.putExtra("title",categoryName);
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
                                            deleteCategory(categoryId, categoryImage);
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
                            Intent intent = new Intent(context, NewCategoryActivity.class);
                            intent.putExtra("key", "edit");
                            intent.putExtra("categoryId",categoryId);
                            context.startActivity(intent);
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });


    }

    private void deleteCategory(final String categoryId, String categoryImage) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(categoryImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Query query = FirebaseDatabase.getInstance().getReference("category").orderByChild("categoryId").equalTo(categoryId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();

                                    pd.dismiss();
                                    Toast.makeText(context, "category deleted!", Toast.LENGTH_SHORT).show();
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
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        CardView categoryCv;
        ImageView categoryIv;
        ImageButton optionIb;
        TextView categoryNameTv;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryCv = itemView.findViewById(R.id.categoryCv);
            categoryIv = itemView.findViewById(R.id.categoryIv);
            optionIb = itemView.findViewById(R.id.optionIb);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);
        }
    }
}
