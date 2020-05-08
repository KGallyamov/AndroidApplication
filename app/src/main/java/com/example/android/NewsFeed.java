package com.example.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NewsFeed extends Fragment {
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private TextView title, menu;
    private ArrayList<RecyclerItem> listItems = new ArrayList<>();
    private Button btn_search;
    private EditText watch;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data");
    private String role, login;
    private LinearLayoutManager manager;
    public static String tag = "";
    private String wh = "Moderate";
    public NewsFeed(){}


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.news_feed, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        SharedPreferences pref = getActivity().getSharedPreferences("User_data", Context.MODE_PRIVATE);
        role = pref.getString("Role", "user");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems = new ArrayList<RecyclerItem>();
                ArrayList<String> pt = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                    listItems.add(p);
                    pt.add(dataSnapshot1.getKey());
                }
                Collections.reverse(listItems);
                Collections.reverse(pt);
                adapter = new MyAdapter(listItems, getContext(), "Data", "user", pt, login);
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
                    int pos = preferences.getInt("position", 0);
                    manager.scrollToPosition(pos);
                    SharedPreferences.Editor ed = preferences.edit();
                    ed.remove("position");
                    ed.apply();
                }catch(NullPointerException e){
                    Log.d("Error", e.toString());
                }
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
        manager = new LinearLayoutManager(getContext());
        SharedPreferences preferences = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
        int pos = preferences.getInt("position", 0);
        manager.scrollToPosition(pos);
        SharedPreferences.Editor ed = preferences.edit();
        ed.remove("position");
        ed.apply();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        watch = (EditText) getActivity().findViewById(R.id.watch);
        title = (TextView) getActivity().findViewById(R.id.title);
        menu = (TextView) getActivity().findViewById(R.id.options);
        btn_search = (Button) getActivity().findViewById(R.id.search);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getContext(), menu);
                if(role.equals("user")){
                    popupMenu.inflate(R.menu.option_user);
                }else {
                    popupMenu.inflate(R.menu.option_menu);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.mnu_main:
                                title.setVisibility(View.VISIBLE);
                                watch.setVisibility(View.GONE);
                                btn_search.setVisibility(View.GONE);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        listItems = new ArrayList<RecyclerItem>();
                                        ArrayList<String> pt = new ArrayList<>();
                                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                                        {
                                            RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                                            listItems.add(p);
                                            pt.add(dataSnapshot1.getKey());
                                        }
                                        Collections.reverse(listItems);
                                        Collections.reverse(pt);
                                        adapter = new MyAdapter(listItems, getContext(), "Data", "user", pt, login);
                                        recyclerView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(getContext(), "Main", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.mnu_search:
                                search();
                                Toast.makeText(getContext(), "Search", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.mnu_moderate:
                                title.setVisibility(View.VISIBLE);
                                watch.setVisibility(View.GONE);
                                btn_search.setVisibility(View.GONE);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Moderate");
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
                                        listItems.remove(0);
                                        if(listItems.size() == 0){
                                            Toast.makeText(getContext(), "Nothing to moderate", Toast.LENGTH_LONG).show();
                                        }

                                        Collections.reverse(listItems);
                                        Collections.reverse(paths);

                                        adapter = new MyAdapter(listItems, getContext(), "Moderate", role, paths, login);
                                        recyclerView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(getContext(), "Moderator", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.favorite:
                                DatabaseReference fav = FirebaseDatabase.getInstance().getReference().child("Favorite"+login);
                                fav.addValueEventListener(new ValueEventListener() {
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

                                        adapter = new MyAdapter(listItems, getContext(), "Favorite", "user", paths, login);
                                        recyclerView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                manager = new LinearLayoutManager(getContext());
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        recyclerView.setLayoutManager(manager);
    }

    public void search(){
        title.setVisibility(View.GONE);
        watch.setVisibility(View.VISIBLE);
        btn_search.setVisibility(View.VISIBLE);
        watch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tag = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listItems = new ArrayList<RecyclerItem>();
                        ArrayList<String> pt = new ArrayList<>();
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                        {
                            RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                            if(p.getHeading().equals(tag) || p.getTags().contains(tag)) {
                                listItems.add(p);
                                pt.add(dataSnapshot1.getKey());
                            }
                        }
                        Collections.reverse(listItems);
                        Collections.reverse(pt);
                        adapter = new MyAdapter(listItems, getContext(), "Data", "user", pt, login);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

}
