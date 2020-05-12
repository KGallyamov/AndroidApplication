package com.example.android;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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

public class OneChatAdapter extends ArrayAdapter<Message> {
    String chat;
    ArrayList<String> paths;
    OneChatAdapter(@NonNull Context context, int resource, Message[] arr, String chat, ArrayList<String> paths) {
        super(context, resource, arr);
        this.chat = chat;
        this.paths = paths;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Message message = getItem(position);
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        if(message.getAuthor().equals(login)){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_out_item, null);
        }else{
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_in_item, null);
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
        }

        convertView.setLongClickable(true);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                return false;
            }
        });
        return convertView;
    }
}
