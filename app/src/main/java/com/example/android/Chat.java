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

public class Chat extends Fragment {
    ListView chats;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.chat_fragment, container, false);
        chats = (ListView) myView.findViewById(R.id.chats);
        chats.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Chat1", "Chat2"}));
        return myView;
    }
}
