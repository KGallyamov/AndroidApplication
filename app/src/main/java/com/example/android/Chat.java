package com.example.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class Chat extends Fragment {
    ListView chats;
    String login;
    View dialogView;
    ImageButton start_conv;
    RecyclerView group_chats;
    Context context = getContext();
    AlertDialog dialogBuilder = null;
    Uri filePath = null;
    public Chat(){}
    String image_link;
    LayoutInflater inf;
    Chat(String login){
        this.login = login;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.chat_fragment, container, false);
        chats = (ListView) myView.findViewById(R.id.chats);
        inf = inflater;
        group_chats = (RecyclerView) myView.findViewById(R.id.group_chats);
        DatabaseReference user_chats = FirebaseDatabase.getInstance().getReference();
        login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
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
                View dialogView = inflater.inflate(R.layout.choose_conversation_type, null);


                TextView dialog = (TextView) dialogView.findViewById(R.id.dialog);
                TextView chat = (TextView) dialogView.findViewById(R.id.group_chat);
                TextView cancel = (TextView) dialogView.findViewById(R.id.Cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        startDialog();
                    }
                });
                chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        startChat();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        return myView;
    }
    public void startDialog(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        View dialogView = inf.inflate(R.layout.start_dialog, null);

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

    public void startChat(){
        dialogBuilder = new AlertDialog.Builder(getContext()).create();
        dialogView = inf.inflate(R.layout.start_group_chat, null);

        final EditText message = (EditText) dialogView.findViewById(R.id.edt_comment);
        final EditText members = (EditText) dialogView.findViewById(R.id.address);
        final EditText groupTitle = (EditText) dialogView.findViewById(R.id.title);
        final ImageView chat_avatar = (ImageView) dialogView.findViewById(R.id.chat_avatar);
        chat_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


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
                if(!message.getText().toString().equals("") && !members.getText().toString().equals("") && !groupTitle.getText().toString().equals("")){
                    final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                    final String[] receviers = members.getText().toString().split(" ");
                    uploadImage(filePath, receviers, message.getText().toString(), groupTitle.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == -1
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                ((ImageView) dialogView.findViewById(R.id.chat_avatar)).setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    private void uploadImage(Uri filePath, final String[] receviers, final String message, final String title) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image_link = uri.toString();
                            try{
                                final HashMap<String, String> members_map = new HashMap<>();
                                for(String i:receviers){
                                    members_map.put(i, i);
                                }
                                HashMap<String, Message> messages = new HashMap<>();
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                                String now = dateformat.format(c.getTime());
                                messages.put("-M0t3jq9g", new Message(message, login, now));

                                final DatabaseReference new_chat = FirebaseDatabase.getInstance().getReference();
                                new_chat.child("GroupChats").push().setValue(new GroupChat(image_link,
                                        members_map, messages, title), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        updateUsers(receviers, databaseReference.getKey());
                                        dialogBuilder.dismiss();
                                    }
                                });
                            }catch(NullPointerException | ArrayIndexOutOfBoundsException e){
                                Toast.makeText(getContext(), "Put spaces between names", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
    }

    private void updateUsers(String[] receviers, String key) {
        Log.d("Look", key);
        for (String recevier : receviers) {
            DatabaseReference update = FirebaseDatabase.getInstance().getReference();
            update.child("Users").child(recevier).child("chats").push().setValue(key);
        }
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
                    try {
                        Glide.with(Objects.requireNonNull(getContext())).load(chat.getChat_avatar()).into(holder.avatar);
                    }catch (NullPointerException e){
                        Log.d("Err", e.toString());
                    }

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
