package com.example.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Chat extends Fragment {
    ListView chats;
    String login;
    ImageButton start_conv;
    RecyclerView group_chats;
    public Chat(){}
    Chat(String login){
        this.login = login;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.chat_fragment, container, false);
        chats = (ListView) myView.findViewById(R.id.chats);
        group_chats = (RecyclerView) myView.findViewById(R.id.group_chats);
        DatabaseReference user_chats = FirebaseDatabase.getInstance().getReference();
        user_chats.child("Users").child(login).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> chats = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(!i.getKey().equals("zero")){
                        chats.add(i.getValue().toString());
                    }
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                group_chats.setAdapter(new GroupChatAdapter(chats));
                group_chats.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        start_conv = (ImageButton) myView.findViewById(R.id.start_converstaion);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ch = new ArrayList<>();
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
        start_conv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                View dialogView = inflater.inflate(R.layout.start_dialog, null);

                final EditText message = (EditText) dialogView.findViewById(R.id.edt_comment);
                final EditText another_user = (EditText) dialogView.findViewById(R.id.address);
                TextView send = (TextView) dialogView.findViewById(R.id.Submit);
                TextView cancel = (TextView) dialogView.findViewById(R.id.Cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!message.getText().toString().equals("") && !another_user.getText().toString().equals("")){
                            final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                            final String recevier = another_user.getText().toString();
                            DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
                            users.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean user_exists = false;
                                    for(DataSnapshot i:dataSnapshot.getChildren()){
                                        if(i.getKey().equals(recevier)){
                                            user_exists = true;
                                        }
                                    }
                                    if(!user_exists){
                                        Toast.makeText(getContext(), "Specified user doesn't exist", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String[] arr = new String[]{login, recevier};
                                        Arrays.sort(arr);
                                        final String name = arr[0] + "_" + arr[1];
                                        DatabaseReference chat = FirebaseDatabase.getInstance().getReference().child("Messages");
                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                                        String now = dateformat.format(c.getTime());
                                        Message mes = new Message(message.getText().toString(), login, now);
                                        chat.child(name).push().setValue(mes);
                                        dialogBuilder.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        return myView;
    }
    public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder>{
        ArrayList<String> chats;
        GroupChatAdapter(ArrayList<String> chats){
            this.chats = chats;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item, parent, false);
            return new ViewHolder(v);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View v;
            public ImageView avatar;
            public TextView title;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                v = itemView;
                title = v.findViewById(R.id.title);
                avatar = v.findViewById(R.id.chat_avatar);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), GroupChatActivity.class);
                        intent.putExtra("path", chats.get(getAdapterPosition()));
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            DatabaseReference group_chat = FirebaseDatabase.getInstance().getReference();
            group_chat.child("GroupChats").child(chats.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GroupChat chat = dataSnapshot.getValue(GroupChat.class);
                    holder.title.setText(chat.getTitle());
                    Glide.with(getContext()).load(chat.getChat_avatar()).into(holder.avatar);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }
    }
}
