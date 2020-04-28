package com.alakamandawalk.pkadmin.category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.model.CategoryData;

import java.util.List;

public class SimpleCategoryAdapter extends RecyclerView.Adapter<SimpleCategoryAdapter.SimpleCategoryViewHolder>{

    Context context;
    List<CategoryData> categoryDataList;

    public SimpleCategoryAdapter(Context context, List<CategoryData> categoryDataList) {
        this.context = context;
        this.categoryDataList = categoryDataList;
    }

    @NonNull
    @Override
    public SimpleCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.simple_category_row,parent,false);

        return new SimpleCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleCategoryViewHolder holder, int position) {

        final String categoryName = categoryDataList.get(position).getCategoryName().toString();
        final String categoryId = categoryDataList.get(position).getCategoryId().toString();

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

    }

    @Override
    public int getItemCount() {
        return categoryDataList.size();
    }

    class SimpleCategoryViewHolder extends RecyclerView.ViewHolder{

        CardView simpleCategoryListCv;
        TextView categoryNameTv;

        public SimpleCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            simpleCategoryListCv = itemView.findViewById(R.id.simpleCategoryListCv);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);
        }
    }
}
