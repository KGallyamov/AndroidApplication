package com.example.android;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class GroupChatAdapter extends ArrayAdapter<Message> {
    Message[] messages;
    String path;
    ArrayList<String> message_path;
    LayoutInflater inflater;
    String pinned_message_link;
    ArrayList<String> forwards_chats, group_chat_paths;
    RelativeLayout reply_layout;
    TextView reply_text, reply_author;
    ListView items;
    public GroupChatAdapter(@NonNull Context context, int resource, Message[] messages, String path,
                            ArrayList<String> message_path, LayoutInflater inflater,
                            String pinned_message_link, ArrayList<String> forwards_chats,
                            ArrayList<String> paths, RelativeLayout reply_layout, TextView reply_text,
                            TextView reply_author, ListView items) {
        super(context, resource, messages);
        this.messages = messages;
        this.path = path;
        this.message_path = message_path;
        this.inflater = inflater;
        this.pinned_message_link = pinned_message_link;
        this.forwards_chats = forwards_chats;
        this.group_chat_paths = paths;
        this.reply_layout = reply_layout;
        this.reply_author = reply_author;
        this.reply_text = reply_text;
        this.items = items;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Message message = getItem(position);
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        if(message.getAuthor().equals(login)){
            if(message.getForwarded().equals("not_forwarded")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_out_item, null);
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.forwarded_message_out, null);
                ((TextView) convertView.findViewById(R.id.real_author)).setText(message.getForwarded());
            }
        }else{
            if(message.getForwarded().equals("not_forwarded")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_in_group, null);
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.forwarded_message_in_group, null);
                ((TextView) convertView.findViewById(R.id.real_author)).setText(message.getForwarded());
            }
            DatabaseReference user_avatar = FirebaseDatabase.getInstance().getReference();
            final View finalConvertView = convertView;
            user_avatar.child("Users").child(message.getAuthor()).child("avatar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Glide.with(getContext()).load(dataSnapshot.getValue().toString()).into((ImageView) finalConvertView.findViewById(R.id.avatar));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            ((ImageView) finalConvertView.findViewById(R.id.avatar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AnotherUserPage.class);
                    intent.putExtra("author", message.getAuthor());
                    getContext().startActivity(intent);
                }
            });
        }
        if(!message.getReply().equals("no_reply")){
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.reply_in_message);
            layout.setVisibility(View.VISIBLE);
            TextView reply_message_tv = (TextView) convertView.findViewById(R.id.reply_message);
            ArrayList<Message> mes = new ArrayList<>(Arrays.asList(messages));
            String txt = mes.get(message_path.indexOf(message.getReply())).getText();
            reply_message_tv.setText(txt);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.smoothScrollToPosition(message_path.indexOf(message.getReply()));
                }
            });
        }
        if (!(message.getImage().equals("no_image"))){
            ImageView message_image = convertView.findViewById(R.id.image);
            message_image.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(message.getImage()).into(message_image);
            message_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photo_intent = new Intent(getContext(), PhotoPage.class);
                    photo_intent.putExtra("link", message.getImage());
                    getContext().startActivity(photo_intent);
                }
            });

        }
        AutoLinkTextView textView = convertView.findViewById(R.id.text);

        textView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_URL);
        textView.setHashtagModeColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        textView.setUrlModeColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        textView.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                Log.d("Look", matchedText);
                if(matchedText.charAt(0) == '#') {
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", matchedText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Tag copied", Toast.LENGTH_SHORT).show();
                }else{
                    Uri address = Uri.parse(matchedText);
                    Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                    getContext().startActivity(openlink);
                }
            }
        });
        textView.setAutoLinkText(message.getText());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MMMM.yyyy");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        String now = dateformat.format(c.getTime());
        String message_time = message.getTime();
        TextView hour_minute = (TextView) convertView.findViewById(R.id.hour_min);
        TextView day_month = (TextView) convertView.findViewById(R.id.day_month);
        String[] h = message_time.split(" ")[0].split(":");
        hour_minute.setText(h[0] + ":" + h[1]);
        // отправили не сегодня
        if(!now.equals(message_time.split(" ")[1])){
            String dm = message_time.split(" ")[1];
            if(year.format(c.getTime()).equals(dm.substring(dm.length() - 4))) {
                dm = dm.substring(0, dm.length() - 5);
            }
            day_month.setText(dm);
        }else{
            day_month.setText("today");
        }

        convertView.setLongClickable(true);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                View dialogView = inflater.inflate(R.layout.group_chat_message_options, null);
                TextView copy_text = dialogView.findViewById(R.id.copy);
                TextView edit_message = dialogView.findViewById(R.id.edit);
                TextView forward_message = (TextView) dialogView.findViewById(R.id.forward);
                TextView delete = dialogView.findViewById(R.id.delete);
                TextView reply = (TextView) dialogView.findViewById(R.id.reply);
                TextView pin = (TextView) dialogView.findViewById(R.id.pin_message);
                TextView exit = dialogView.findViewById(R.id.Cancel);
                if(login.equals(message.getAuthor())){
                    edit_message.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                }
                if(message_path.get(position).equals(pinned_message_link)){
                    pin.setText("Unpin message");
                }


                final AlertDialog alertDialog = ask.create();
                alertDialog.setView(dialogView);
                alertDialog.show();
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                forward_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        forwardMessage(message);
                    }
                });
                copy_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", message.getText());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getContext(), "Text copied", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = getContext().getSharedPreferences("Reply_message", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = preferences.edit();
                        ed.putString("link", message_path.get(position));
                        ed.putString("author", message.getAuthor());
                        ed.putString("text", message.getText());
                        ed.apply();
                        reply_layout.setVisibility(View.VISIBLE);
                        reply_text.setText(message.getText());
                        reply_author.setText(message.getAuthor());
                        alertDialog.dismiss();
                    }
                });
                pin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(message_path.get(position).equals(pinned_message_link)){
                            DatabaseReference pin_message = FirebaseDatabase.getInstance().getReference();
                            pin_message.child("GroupChats").child(path).child("pinned_message").setValue("no_message");
                            alertDialog.dismiss();
                        }else {
                            DatabaseReference pin_message = FirebaseDatabase.getInstance().getReference();
                            pin_message.child("GroupChats").child(path).child("pinned_message").setValue(message_path.get(position));
                            alertDialog.dismiss();
                        }
                    }
                });
                edit_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                        View dialogView = inflater.inflate(R.layout.edit_message, null);
                        final EditText ed_message = dialogView.findViewById(R.id.edit_message);
                        TextView submit = dialogView.findViewById(R.id.Submit);
                        TextView exit = dialogView.findViewById(R.id.Cancel);
                        ed_message.setText(message.getText());


                        final AlertDialog alertDialog_edit = ask.create();
                        alertDialog_edit.setView(dialogView);
                        exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog_edit.dismiss();
                            }
                        });
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                if(ed_message.getText().toString().equals("")){
                                    reference.child("GroupChats").child(path).child("messages").
                                            child(message_path.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            alertDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    reference.child("GroupChats").child(path).child("messages").
                                            child(message_path.get(position)).child("text").setValue(ed_message.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    alertDialog_edit.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        alertDialog_edit.show();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if(message.getAuthor().equals(login)) {
                            final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(),
                                    R.style.MyAlertDialogStyle);
                            ask.setMessage("Are you sure you want to delete this message?").setCancelable(false)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            // пользователь удаляет закрепленное сообщение
                                            if(message_path.get(position).equals(pinned_message_link)){
                                                Toast.makeText(getContext(), "unpin the message first",
                                                        Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }else {
                                                DatabaseReference reference = FirebaseDatabase.getInstance().
                                                        getReference();
                                                reference.child("GroupChats").child(path).child("messages").
                                                        child(message_path.get(position)).removeValue().
                                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog.dismiss();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }


                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = ask.create();
                            alertDialog.setTitle("Delete message");
                            alertDialog.show();
                        }else{
                            Toast.makeText(getContext(), "You can delete only your messages", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }
        });
        return convertView;
    }
    private void forwardMessage(final Message message) {
        final AlertDialog.Builder where = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        View dialogView = inflater.inflate(R.layout.forward_alert_dialog, null);
        final ListView variants = (ListView) dialogView.findViewById(R.id.variants);
        TextView cancel = (TextView) dialogView.findViewById(R.id.Cancel);




        final AlertDialog alertDialog = where.create();
        alertDialog.setView(dialogView);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        DatabaseReference chats_and_messages = FirebaseDatabase.getInstance().getReference().child("Messages");
        chats_and_messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                final ArrayList<String> messages = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    String[] people = i.getKey().split("_");
                    if(people[0].equals(login)){
                        messages.add(people[1]);
                    }else{
                        messages.add(people[0]);
                    }
                }
                final ArrayList<String> arrayList = messages;
                arrayList.addAll(forwards_chats);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        R.layout.forward_list_item,
                        arrayList.toArray(new String[0]));
                variants.setAdapter(adapter);
                variants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Message forwarded_message = message;
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                        String now = dateformat.format(c.getTime());
                        forwarded_message.setTime(now);
                        forwarded_message.setReply("no_reply");
                        if(forwarded_message.getForwarded().equals("not_forwarded")) {
                            forwarded_message.setForwarded(forwarded_message.getAuthor());
                        }
                        forwarded_message.setAuthor(login);
                        if(forwards_chats.contains(arrayList.get(position))){
                            DatabaseReference forward_to_chat = FirebaseDatabase.getInstance().getReference();
                            forward_to_chat.child("GroupChats").
                                    child(group_chat_paths.get(forwards_chats.indexOf(arrayList.get(position)))).
                                    child("messages").push().setValue(forwarded_message);
                            alertDialog.dismiss();
                        }else{
                            DatabaseReference forward_to_chat = FirebaseDatabase.getInstance().getReference();
                            String[] names = new String[]{login, messages.get(messages.indexOf(arrayList.get(position)))};
                            Arrays.sort(names);
                            forward_to_chat.child("Messages").
                                    child(names[0] + "_" + names[1]).push().setValue(forwarded_message);
                            alertDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        alertDialog.show();
    }
}
