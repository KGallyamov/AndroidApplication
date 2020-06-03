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
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
import com.google.firebase.storage.FirebaseStorage;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class OneChatAdapter extends ArrayAdapter<Message> {
    String chat;
    ArrayList<String> paths;
    LayoutInflater inflater;
    ArrayList<String> dialogs;
    OneChatAdapter(@NonNull Context context, int resource, Message[] arr, String chat,
                   ArrayList<String> paths, LayoutInflater inflater, ArrayList<String> dialogs) {
        super(context, resource, arr);
        this.chat = chat;
        this.paths = paths;
        this.inflater = inflater;
        this.dialogs = dialogs;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Message message = getItem(position);
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        if(message.getAuthor().equals(login)){
            if(!message.isForwarded().equals("not_forwarded")){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.forwarded_message_out, null);
                ((TextView) convertView.findViewById(R.id.real_author)).setText(message.isForwarded());
            }else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_out_item, null);
            }
        }else{
            if(!message.isForwarded().equals("not_forwarded")){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.forwarded_message_in, null);
                ((TextView) convertView.findViewById(R.id.real_author)).setText(message.isForwarded());
            }else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_in_item, null);
            }
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
        final String message_time = message.getTime();
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
        LinearLayout message_body = (LinearLayout) convertView.findViewById(R.id.message_body);

        message_body.setLongClickable(true);
        message_body.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                View dialogView = inflater.inflate(R.layout.message_options, null);
                TextView copy_text = dialogView.findViewById(R.id.copy);
                TextView tv_forward = dialogView.findViewById(R.id.forward);
                TextView edit_message = dialogView.findViewById(R.id.edit);
                TextView delete = dialogView.findViewById(R.id.delete);
                TextView exit = dialogView.findViewById(R.id.Cancel);
                if(login.equals(message.getAuthor())){
                    edit_message.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
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
                                    reference.child("Messages").child(chat).child(paths.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    reference.child("Messages").child(chat).child(paths.get(position)).child("text").setValue(ed_message.getText().toString())
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
                tv_forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        DatabaseReference user_chats = FirebaseDatabase.getInstance().getReference();
                        user_chats.child("Users").child(login).child("chats").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<String> chats_paths = new ArrayList<>();
                                for(DataSnapshot i:dataSnapshot.getChildren()){
                                    if(!i.getKey().equals("zero")){
                                        chats_paths.add(i.getValue().toString());
                                    }
                                }
                                forwardMessage(message, chats_paths);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if(message.getAuthor().equals(login)) {
                            final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                            ask.setMessage("Are you sure you want to delete this message?").setCancelable(false)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                            reference.child("Messages").child(chat).child(paths.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
    private void forwardMessage(final Message message, final ArrayList<String> group_chat_paths) {
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
        DatabaseReference chats_and_messages = FirebaseDatabase.getInstance().getReference().child("GroupChats");
        chats_and_messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                final ArrayList<String> forwards_chats = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(group_chat_paths.contains(i.getKey())){
                        GroupChat groupChat = i.getValue(GroupChat.class);
                        forwards_chats.add(groupChat.getTitle());
                    }
                }
                final ArrayList<String> arrayList = dialogs;
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
                        if(forwarded_message.isForwarded().equals("not_forwarded")){
                            forwarded_message.setForwarded(login);
                        }else{
                            Log.d("OneChatAdapter_354", forwarded_message.isForwarded());
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
                            String[] names = new String[]{login, dialogs.get(dialogs.indexOf(arrayList.get(position)))};
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
