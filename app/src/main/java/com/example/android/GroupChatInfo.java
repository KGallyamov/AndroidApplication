package com.example.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class GroupChatInfo extends AppCompatActivity {
    // активити с информацией о чате
    String path;
    String creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_info);
        path = getIntent().getStringExtra("path");
        creator = getIntent().getStringExtra("creator");
        Button exit = (Button) findViewById(R.id.exit123456);
        final EditText new_member_name = (EditText) findViewById(R.id.member_name);
        final ListView members = (ListView) findViewById(R.id.members);

        // в окне с информацией о чате показывается список его участников
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("GroupChats").child(path).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> list_members = new ArrayList<>();
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    list_members.add(i.getValue().toString());
                }
                // получение аватарок и времени последнего входа пользователей
                DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference();
                user_ref.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<User> list = new ArrayList<>();
                        for (DataSnapshot i : dataSnapshot.getChildren()) {
                            if (list_members.contains(i.getKey())) {
                                list.add(i.getValue(User.class));
                            }
                        }

                        GroupChatMembersAdapter adapter = new GroupChatMembersAdapter(
                                GroupChatInfo.this, R.layout.group_chat_member,
                                list.toArray(new User[0]),
                                list_members.toArray(new String[0]), creator, path);
                        members.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference chat_title = FirebaseDatabase.getInstance().getReference();
        chat_title.child("GroupChats").child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupChat chat = dataSnapshot.getValue(GroupChat.class);
                try {
                    Glide.with(GroupChatInfo.this).load(chat.getChat_avatar()).into((ImageView) findViewById(R.id.chat_avatar));
                } catch (IllegalArgumentException e) {
                    Log.e("GroupChatInfo_106", e.toString());
                }
                ((TextView) findViewById(R.id.title)).setText(chat.getTitle());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ((ImageView) findViewById(R.id.chat_avatar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        ((TextView) findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((Button) findViewById(R.id.add_member)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new_member_name.getText().toString().equals("")) {
                    Toast.makeText(GroupChatInfo.this, "Enter the name", Toast.LENGTH_SHORT).show();
                } else {
                    final String name = new_member_name.getText().toString();
                    DatabaseReference check_exists = FirebaseDatabase.getInstance().getReference();
                    check_exists.child("Users").child(name).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                User user = dataSnapshot.getValue(User.class);
                                boolean in_chat = false;
                                boolean have_permission = false;
                                String permission = user.getPrivacy_settings().get("add_to_group_chats");
                                if (permission.equals("everyone")) {
                                    have_permission = true;
                                } else if (permission.equals("friends")) {
                                    String login = FirebaseAuth.getInstance().getCurrentUser().
                                            getEmail().split("@")[0];
                                    if (user.getFriends().containsKey(login)) {
                                        have_permission = user.getFriends().get(login).equals("friend");
                                    } else {
                                        have_permission = false;
                                    }
                                }
                                for (String s : user.getChats().values()) {
                                    if (s.equals(path)) {
                                        in_chat = true;
                                    }
                                }
                                if ((!in_chat) && have_permission) {
                                    DatabaseReference add_member = FirebaseDatabase.getInstance().getReference();
                                    add_member.child("GroupChats").child(path).child("members")
                                            .child(name).setValue(name);
                                    DatabaseReference add_chat = FirebaseDatabase.getInstance().getReference();
                                    add_chat.child("Users").child(name).child("chats").push().
                                            setValue(path).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(GroupChatInfo.this,
                                                    "User added", Toast.LENGTH_SHORT).show();
                                            new_member_name.setText("");
                                        }
                                    });
                                } else if (!have_permission) {
                                    Toast.makeText(GroupChatInfo.this,
                                            "You can't add this user", Toast.LENGTH_SHORT).show();
                                    new_member_name.setText("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(GroupChatInfo.this,
                                        "Please check user's name", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ask = new AlertDialog.Builder(GroupChatInfo.this,
                        R.style.MyAlertDialogStyle);
                ask.setMessage("Are you sure you want to leave this chat?").setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String login = FirebaseAuth.getInstance().getCurrentUser().
                                        getEmail().split("@")[0];
                                final DatabaseReference user_update = FirebaseDatabase.getInstance().
                                        getReference();
                                user_update.child("Users").child(login).child("chats").
                                        addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot i : dataSnapshot.getChildren()) {
                                                    if (i.getValue().toString().equals(path)) {
                                                        final DatabaseReference user_update_2 =
                                                                FirebaseDatabase.getInstance().getReference();
                                                        user_update_2.child("Users").child(login).
                                                                child("chats").child(i.getKey()).removeValue();
                                                        finish();
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                DatabaseReference update_chat = FirebaseDatabase.getInstance().getReference();
                                update_chat.child("GroupChats").child(path).child("members").child(login).removeValue();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = ask.create();
                alertDialog.setTitle("Leave chat");
                alertDialog.show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 71 && resultCode == -1
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.
                        getBitmap(getApplicationContext().getContentResolver(), filePath);
                ((ImageView) findViewById(R.id.chat_avatar)).setImageBitmap(bitmap);
                uploadImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage(Uri filePath) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(GroupChatInfo.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image_link = uri.toString();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("GroupChats").child(path).child("chat_avatar").
                                    setValue(image_link);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(GroupChatInfo.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
}
