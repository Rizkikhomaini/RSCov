package com.rizki.projectskripsi.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizki.projectskripsi.R;
import com.rizki.projectskripsi.api.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> news;
    private ListClickedListener listClickedListener;

    public NewsAdapter(ArrayList<News> news, ListClickedListener listClickedListener) {
        this.news = news;
        this.listClickedListener = listClickedListener;
    }

    public void setData(List<News> newData) {
        news.clear();
        news.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_berita, parent, false);
        return new ViewHolder(view, listClickedListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(news.get(position).getImage()).into(holder.mNewsImage);
        holder.mNewsTitle.setText(news.get(position).getTitle());

        holder.itemView.setOnClickListener(v -> holder.mListClickedListener.onListClicked(news.get(position)));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mNewsImage;
        TextView mNewsTitle;
        ListClickedListener mListClickedListener;

        public ViewHolder(@NonNull View itemView, ListClickedListener listClickedListener) {
            super(itemView);

            mNewsImage = itemView.findViewById(R.id.iv_news);

            mNewsTitle = itemView.findViewById(R.id.tv_news_title);

            mListClickedListener = listClickedListener;
        }
    }

    public interface ListClickedListener {
        void onListClicked(News news);
    }
}
