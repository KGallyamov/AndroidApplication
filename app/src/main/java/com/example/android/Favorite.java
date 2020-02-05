package com.example.android;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Favorite extends Fragment {

    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private List<RecyclerItem> listItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.favorite_activity, null);

    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO:СКАЧАТЬ С FIREBASE
        //Information inf = new Information();
        //String s = inf.readFile();

        int[] liked = new int[]{2,4,6,8,10};
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.liked);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();
        for(int i :liked){
            Bitmap bitmap = Bitmap.createBitmap(100, 100,
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.RED);
            RecyclerItem item = new RecyclerItem("Item " + i, "Description of item " + i, bitmap);
            listItems.add(item);
        }
        adapter = new MyAdapter(listItems, getContext(), "favorite");
        recyclerView.setAdapter(adapter);

    }
}
