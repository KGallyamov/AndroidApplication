package com.example.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat extends Fragment {
    ListView chats;
    String login;
    Chat(String login){
        this.login = login;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.chat_fragment, container, false);
        chats = (ListView) myView.findViewById(R.id.chats);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Messages").addValueEventListener(new ValueEventListener() {
            ArrayList<String> ch = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    String[] arr = i.getKey().split("_");
                    if(arr[1].equals(login)){
                        ch.add(arr[0]);
                    }else if(arr[0].equals(login)){
                        ch.add(arr[1]);
                    }
                }
                ChatListAdapter adapter = new ChatListAdapter(getContext(), R.layout.chat_item, ch.toArray(new String[0]));
                chats.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return myView;
    }
}
