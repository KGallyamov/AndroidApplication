package com.example.android;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsFeed extends Fragment {
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private List<RecyclerItem> listItems;
    public static final String URL = "https://demonuts.com/Demonuts/JsonTest/Tennis/json_parsing.php";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.news_feed, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        String data = null;
        try {
            data = new URL(URL).toString();
            System.out.println(data);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(data);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            String url = "https://firebasestorage.googleapis.com/v0/b/image-259c9.appspot.com/o/knight.jpg?alt=media&token=178026e5-1d49-4716-8953-7ad1af3088df";
            RecyclerItem item = new RecyclerItem("Item " + (i + 1), "Description of item " + (i + 1), url);
            listItems.add(item);
        }

        adapter = new MyAdapter(listItems, getContext(), "main");
        recyclerView.setAdapter(adapter);
    }

}
