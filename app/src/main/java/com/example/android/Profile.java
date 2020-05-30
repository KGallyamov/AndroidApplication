package com.example.android;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class Profile extends Fragment {
    String role, login, password, fake_password, text_avatar;
    TextView change, tv_posts;
    Button look_password, confirm, exit;
    EditText new_password;
    RecyclerView user_posts;
    String updated = "";
    ImageView avatar;
    boolean is = true;
    ArrayList<String> posts;
    private TextView tv_login, tv_password, tv_role;
    private final int PICK_IMAGE_REQUEST = 71;
    float rating;
    private HashMap<String, String> privacy_settings, friends;

    public Profile(){}

    Profile(String role, String login, String password, String text_avatar, ArrayList<String> posts,
            float rating, HashMap<String, String> privacy_settings, HashMap<String, String> friends){
        this.role = role;
        this.login = login;
        this.password = "Password: " + password;
        this.text_avatar = text_avatar;
        this.posts = posts;
        this.rating = rating;
        this.friends = friends;
        this.privacy_settings = privacy_settings;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_profile, null);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();
        //TDO: показать количество друзей, при нажатии на них открыть список с ними
        // какой-нибудь тип уведомлений о заявке в друзья
        Log.d("Profile", Integer.toString(friends.size()));

        tv_login = (TextView) getActivity().findViewById(R.id.login);
        tv_role  =(TextView) getActivity().findViewById(R.id.role);
        tv_password = (TextView) getActivity().findViewById(R.id.password);
        tv_posts = (TextView) getActivity().findViewById(R.id.posts);
        user_posts = (RecyclerView) getActivity().findViewById(R.id.users_posts);
        look_password = (Button) getActivity().findViewById(R.id.look_password);
        change = (TextView) getActivity().findViewById(R.id.change);
        confirm  =(Button) getActivity().findViewById(R.id.confirm);
        exit = (Button) getActivity().findViewById(R.id.exit123456);
        avatar = (ImageView) getActivity().findViewById(R.id.avatar);
        new_password = (EditText) getActivity().findViewById(R.id.new_password);

        tv_role.setText(role);
        tv_login.setText(login);

        tv_posts.setText(Integer.toString(posts.size() - 1));

        Glide.with(getActivity()).load(text_avatar).into(avatar);
        DatabaseReference update_role = FirebaseDatabase.getInstance().getReference();
        update_role.child("Users").child(login).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float user_rating = dataSnapshot.getValue(Float.TYPE);
                String new_role = "user";
                DatabaseReference update = FirebaseDatabase.getInstance().getReference().child("Users").child(login).child("role");
                if(!role.equals("admin")) {
                    if (user_rating >= 120) {
                        new_role = "moderator";
                        update.setValue(new_role);
                    } else if (rating < 120 && role.equals("moderator")) {
                        update.setValue(new_role);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(login).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> user_links = new ArrayList<>();
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    if (!i.getKey().equals("zero")) {
                        user_links.add(i.getValue().toString());
                    }
                }
                fill(user_links);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView tv_rating = (TextView) getActivity().findViewById(R.id.rating);
        tv_rating.setText(Float.toString(rating));
        if(rating > 0){
            tv_rating.setTextColor(getActivity().getColor(R.color.rating_green));
        }else if(rating < 0){
            tv_rating.setTextColor(getActivity().getColor(R.color.colorAccent));
        }


        fake_password = "Password: ";
        for(int i=0;i<password.length() - "Password: ".length();i++){
            fake_password = fake_password + "*";
        }
        tv_password.setText(fake_password);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is){
                    change.setText("Cancel");
                    is = false;
                    change.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                    look_password.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    tv_password.setVisibility(View.GONE);
                    new_password.setVisibility(View.VISIBLE);
                    new_password.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            updated = s.toString();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(updated.equals("") || updated.length() < 4){
                                Toast.makeText(getContext(), "Too simple", Toast.LENGTH_SHORT).show();
                            }else{
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login);
                                ref.child("password").setValue(updated);
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                look_password.setVisibility(View.VISIBLE);
                                confirm.setVisibility(View.GONE);
                                tv_password.setVisibility(View.VISIBLE);
                                new_password.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    is = true;
                    change.setTextColor(getActivity().getResources().getColor(R.color.active_blue));
                    change.setText("Change password");
                    look_password.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    tv_password.setVisibility(View.VISIBLE);
                    new_password.setVisibility(View.GONE);
                }

            }
        });

        look_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_password.getText().equals(password)) {
                    look_password.setBackground(getActivity().getDrawable(R.drawable.ic_visibility_unactive_24dp));
                    tv_password.setText(fake_password);

                }else{
                    look_password.setBackground(getActivity().getDrawable(R.drawable.ic_visibility_black_24dp));
                    tv_password.setText(password);
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                ask.setMessage("Are you sure you want to log out?").setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        new AsyncRequest().execute();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = ask.create();
                alertDialog.setTitle("Log out");
                alertDialog.show();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                ask.setMessage("Do you want to change your avatar?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chooseImage();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = ask.create();
                alertDialog.setTitle("Change avatar");
                alertDialog.show();

            }
        });

    }
    class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            String time = "failed";
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(30000);
                    client.connect("time.nist.gov");
                    time =  client.getDate().toString();
                } finally {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return time;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                HashMap<String, String> months = new HashMap<>();
                months.put("May", "мая");
                months.put("June", "июня");
                months.put("July", "июля");
                months.put("August", "августа");

                months.put("September", "сентября");
                months.put("October", "октября");
                months.put("November", "ноября");
                months.put("December", "декабря");

                months.put("January", "января");
                months.put("February", "февраля");
                months.put("March", "марта");
                months.put("April", "апреля");
                String time_for_database;
                if(!s.equals("failed")) {
                    String[] time_data = s.split(" ");
                    for (String i : time_data) {
                        Log.d("Look", i);
                    }

                    time_for_database = time_data[3] + " " + time_data[2] +
                            "." + months.get(time_data[1]) + "." + time_data[5];

                }
                // если не получилось взять время с сервера
                else{
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                    time_for_database = dateformat.format(c.getTime());
                }
                Log.d("Look", time_for_database);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Users").child(login).child("lastSeen").setValue(time_for_database);
            Intent mStartActivity = new Intent(getContext(), Authentication.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId, mStartActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filePath;
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null ) {
            filePath = data.getData();
            uploadImage(filePath);


        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void uploadImage(Uri filePath){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String image = uri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(login);
                        reference.child("avatar").setValue(image);
                        Glide.with(getActivity()).load(image).into(avatar);


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
    private void fill(final ArrayList<String> links){
        user_posts = (RecyclerView) getActivity().findViewById(R.id.users_posts);
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Data");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<RecyclerItem> listItems = new ArrayList<>();
                final ArrayList<String> pt = new ArrayList<>();
                final LinearLayoutManager manager = new LinearLayoutManager(getContext());
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    if(links.contains(dataSnapshot1.getKey())) {
                        RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                        listItems.add(p);
                        pt.add(dataSnapshot1.getKey());
                    }
                }
                Collections.reverse(listItems);
                Collections.reverse(pt);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Users").child(login).
                        child("rating").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float rating = dataSnapshot.getValue(Float.TYPE);
                        MyAdapter adapter = new MyAdapter(listItems, getContext(), "Data", "user", pt, login, rating);
                        user_posts.setAdapter(adapter);

                        user_posts.setLayoutManager(manager);
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
    }

}
