package com.example.android;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NewsFeed extends Fragment {
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<RecyclerItem> listItems = new ArrayList<>();
    private Button moderate;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data");
    private String role;
    private String wh = "Moderate";
    NewsFeed(String role){
        this.role = role;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.news_feed, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems = new ArrayList<RecyclerItem>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                    listItems.add(p);
                }
                Collections.reverse(listItems);
                adapter = new MyAdapter(listItems, getContext(), "main", "user");
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        moderate = (Button) getActivity().findViewById(R.id.moderate);
        if(!role.equals("user")){
            moderate.setVisibility(View.VISIBLE);
            moderate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(wh);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listItems = new ArrayList<RecyclerItem>();
                            ArrayList<String> paths = new ArrayList<>();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                                listItems.add(p);
                                paths.add(dataSnapshot1.getKey());

                            }
                            Collections.reverse(listItems);
                            Collections.reverse(paths);
                            adapter = new MyAdapter(listItems, getContext(), "main", role, paths);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if(wh.equals("Moderate")) {
                        Toast.makeText(getContext(), "Hi moderator", Toast.LENGTH_SHORT).show();
                        wh = "Data";
                    }else{
                        wh = "Moderate";
                    }
                    moderate.setText(wh);
                }
            });
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
