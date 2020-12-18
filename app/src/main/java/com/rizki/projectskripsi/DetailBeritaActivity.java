package com.rizki.projectskripsi;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.rizki.projectskripsi.api.News;

import java.util.Objects;

public class DetailBeritaActivity extends AppCompatActivity {

    public static String KEY_DATA = "KEY_DATA_BERITA";

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_berita);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.berita));

        if (getIntent().hasExtra(KEY_DATA)) {
            news = getIntent().getParcelableExtra(KEY_DATA);
            initData();
        } else {
            finish();
        }
    }

    private void initData() {
        ImageView ivNews = findViewById(R.id.iv_news);
        TextView tvNewsTitle = findViewById(R.id.tv_news_title);
        TextView tvNewsWriter = findViewById(R.id.tv_news_writer);
        TextView tvNewsPostDate = findViewById(R.id.tv_news_post_date);
        TextView tvNewsContent = findViewById(R.id.tv_news_content);

        Glide.with(this).load(news.getImage()).into(ivNews);
        tvNewsTitle.setText(news.getTitle());
        tvNewsWriter.setText(news.getWriter());
        tvNewsPostDate.setText(news.getPost_date());
        tvNewsContent.setText(news.getContent());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}