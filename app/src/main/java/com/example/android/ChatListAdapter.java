package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.HashMap;

public class ChatListAdapter extends ArrayAdapter<String> {
    // адаптер списка диалогов
    private HashMap<String, Integer> unread;
    private String[] dialogs;

    ChatListAdapter(@NonNull Context context, int resource, String[] arr, HashMap<String, Integer> unread) {
        super(context, resource, arr);
        this.unread = unread;
        this.dialogs = arr;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String chat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, null);
        }
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        final String[] arr = new String[]{login, chat};
        TextView unchecked = (TextView) convertView.findViewById(R.id.unchecked);
        if (!(unread.get(chat) == 0)) {
            unchecked.setVisibility(View.VISIBLE);
            unchecked.setText(Integer.toString(unread.get(chat)));
        } else {
            unchecked.setVisibility(View.GONE);
        }
        Arrays.sort(arr);
        final TextView last_message = (TextView) convertView.findViewById(R.id.last_message);
        ((TextView) convertView.findViewById(R.id.name)).setText(chat);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final View finalConvertView = convertView;
        reference.child("Users").child(chat).child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getContext()).load(dataSnapshot.getValue().toString()).into((ImageView) finalConvertView.findViewById(R.id.avatar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // показывается последнее отправленное сообщение
        DatabaseReference get_last = FirebaseDatabase.getInstance().getReference();
        final View finalConvertView1 = convertView;
        get_last.child("Messages").child(arr[0] + "_" + arr[1]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> arrayList = new ArrayList<>();
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    arrayList.add(i.getValue(Message.class));
                }
                TextView time = (TextView) finalConvertView1.findViewById(R.id.time);
                try {
                    last_message.setText(arrayList.get(arrayList.size() - 1).getText().substring(0, 25));
                } catch (Exception e) {
                    last_message.setText(arrayList.get(arrayList.size() - 1).getText());
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd.MMMM.yyyy");
                String now = dateformat.format(c.getTime());
                if (now.equals(arrayList.get(arrayList.size() - 1).getTime().split(" ")[1])) {
                    time.setText(arrayList.get(arrayList.size() - 1).getTime().split(" ")[0]);
                } else {
                    String moment = (arrayList.get(arrayList.size() - 1).getTime().split(" ")[1]);
                    SimpleDateFormat year = new SimpleDateFormat("yyyy");
                    if (year.format(c.getTime()).equals(moment.substring(moment.length() - 4))) {
                        moment = moment.substring(0, moment.length() - 5);
                    }
                    String[] clock = arrayList.get(arrayList.size() - 1).getTime().split(" ")[0].split(":");
                    time.setText(clock[0] + ":" + clock[1] + "  " + moment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // открыть окно диалога
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OneChatActivity.class);
                intent.putExtra("Another_person", chat);
                intent.putExtra("dialogs", dialogs);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
