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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsFeed extends Fragment {
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private List<RecyclerItem> listItems;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.news_feed, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        //СКАЧАТЬ С FIREBASE
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Bitmap bitmap = Bitmap.createBitmap(100, 100,
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.CYAN);
            RecyclerItem item = new RecyclerItem("Item " + (i + 1), "Description of item " + (i + 1), bitmap);
            listItems.add(item);
        }
        adapter = new MyAdapter(listItems, getContext(), "main");
        recyclerView.setAdapter(adapter);



    }

}
