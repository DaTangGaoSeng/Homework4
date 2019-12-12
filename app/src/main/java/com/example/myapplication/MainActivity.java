package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Category> categories = new ArrayList<>();
    private Items items;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Random random;
    private int length = 0;//用来计数更新的信息数目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
      /*  recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));*/
        upside();
        swipeRefreshLayout = findViewById(R.id.refresh);
        //监听下拉操作
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                upside();
                items.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //监听上滑操作是否到底端
        recyclerView.addOnScrollListener(new OnLastitemListener() {
            @Override
            void loadmore() {
                down();
            }
        });
    }

    private void request() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //随机访问网页
                    random = new Random();
                    int i = random.nextInt(20);
                    int j = random.nextInt(20);
                    URL url = new URL("http://gank.io/api/data/Android/" + i + "/" + j);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String s;
                    while ((s = bufferedReader.readLine()) != null)
                        stringBuilder.append(s);
                    translate(stringBuilder.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    //解析JSON
    private void translate(final String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            final JSONArray jsonArray = jsonObject.getJSONArray("results");
            length = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Map<String, String> hashMap = new HashMap<>();
                String id = jsonObject1.getString("_id");
                String createdat = jsonObject1.getString("createdAt");
                String des = jsonObject1.optString("desc");
                String desc;
                if(des.length() > 35){
                    des.substring(0,35);
                    desc = des+"....";
                }
                else desc = des;
                String publishedAt = jsonObject1.getString("publishedAt").substring(0,10);
                String source = jsonObject1.getString("source");
                String type = jsonObject1.getString("type");
                String url = jsonObject1.getString("url");
                String who = "by: "+jsonObject1.getString("who");
                hashMap.put("id", id);
                hashMap.put("createdAt", createdat);
                hashMap.put("desc", desc);
                hashMap.put("publishedAt", publishedAt);
                hashMap.put("source", source);
                hashMap.put("type", type);
                hashMap.put("url", url);
                hashMap.put("who", who);
                List<String> tem = new ArrayList<>();//存放图片网址的
                if (jsonObject1.has("images")) {
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        tem.add(jsonArray1.get(j).toString());
                    }
                    categories.add(new Category(hashMap, tem));
                } else
                    categories.add(new Category(hashMap));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//下拉调用方法
    private void upside() {
        request();
        Collections.reverse(categories);//用于颠倒Arraylist数据 否则会在下部刷新
        items = new Items(categories, MainActivity.this);
        recyclerView.setAdapter(items);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        });
        Toast.makeText(MainActivity.this, "更新了" + length + "条信息", Toast.LENGTH_SHORT).show();
    }
//上拉到顶调用方法
    private void down() {
        request();
        items = new Items(categories, MainActivity.this);
        recyclerView.setAdapter(items);
        Toast.makeText(MainActivity.this, "更新了" + length + "条信息", Toast.LENGTH_SHORT).show();
        length += categories.size();
    }

}


