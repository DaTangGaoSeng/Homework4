package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

//这是适配器
public class Items extends RecyclerView.Adapter<Items.ViewHolder> {
    private List<Category> list;
    private Context context;

    public Items(List<Category> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Category category = list.get(position);
        holder.type.setText(category.getList().get("type"));
        holder.publishedAt.setText(category.getList().get("publishedAt"));
        holder.who.setText(category.getList().get("who"));
        holder.desc.setText(category.getList().get("desc"));
        RequestOptions options = new RequestOptions().placeholder(R.mipmap.ic_launcher).
                error(R.mipmap.ic_launcher_round).fallback(R.mipmap.ic_launcher_round).override(500, 500);
        try {
            //把图片网址的第一条作为封面图
            Glide.with(context).load(category.getImages().get(0)).apply(options).into(holder.imageView);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShouActivity.class);
                intent.putExtra("http", category.getList().get("url"));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

   static class ViewHolder extends RecyclerView.ViewHolder {
        TextView desc;
        TextView who;
        TextView publishedAt;
        TextView type;
        ImageView imageView;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.desc);
            who = itemView.findViewById(R.id.who);
            publishedAt = itemView.findViewById(R.id.publishedAt);
            type = itemView.findViewById(R.id.type);
            imageView = itemView.findViewById(R.id.image_1);
            view = itemView.findViewById(R.id.fen);
        }
    }
}
















