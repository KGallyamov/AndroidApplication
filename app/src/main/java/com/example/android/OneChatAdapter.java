package com.example.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OneChatAdapter extends ArrayAdapter<Message> {
    String avatar;
    String another_user;
    OneChatAdapter(@NonNull Context context, int resource, Message[] arr) {
        super(context, resource, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);
        String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        if(message.getAuthor().equals(login)){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_out_item, null);
        }else{
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_in_item, null);
        }
        AutoLinkTextView textView = convertView.findViewById(R.id.text);

        textView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_URL);
        textView.setHashtagModeColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        textView.setUrlModeColor(ContextCompat.getColor(getContext(), R.color.blue_800));
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
        String now = dateformat.format(c.getTime());
        String message_time = message.getTime();
        TextView message_tv = (TextView) convertView.findViewById(R.id.time);
        // отправили сегодня
        if(now.equals(message_time.split(" ")[1])){
            message_tv.setText(message.getTime().split(" ")[0]);
        }else{
            String moment = (message.getTime().split(" ")[1]);
            moment = moment.substring(0, moment.length() - 5);
            String[] clock = message.getTime().split(" ")[0].split(":");
            message_tv.setText(clock[0] +":"+ clock[1] + "  " + moment);
        }
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO: выбор между удалением сообщения и его
//                // редактированием(если при редактировании стерли весь текст, то удалить сообщение)
//                // всё с AlertDialog'ами
//            }
//        });
        return convertView;
    }
}
