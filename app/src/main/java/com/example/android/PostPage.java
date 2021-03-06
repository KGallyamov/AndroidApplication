package com.example.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class PostPage extends AppCompatActivity {
    TextView title, description, heading, author, upvote, downvote, result_rating;
    ImageView imageView, author_avatar;
    Button close, ok, refuse, send;
    Context getActivity = this;
    String role = "", path;
    TextView tvTime;
    String reply;
    Context context = this;
    ListView comments_list;
    EditText leave_a_comment;
    ArrayList<String> tags;
    AutoLinkTextView autoLinkTextView;
    float midValue = 0;
    int pos;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);

        title = (TextView) findViewById(R.id.txtTitle);
        description = (TextView) findViewById(R.id.txtDescription);
        imageView = (ImageView) findViewById(R.id.picture);
        author = (TextView) findViewById(R.id.author);
        author_avatar = (ImageView) findViewById(R.id.author_avatar);
        close = (Button) findViewById(R.id.close);
        upvote = (TextView) findViewById(R.id.up);
        downvote = (TextView) findViewById(R.id.down);
        result_rating = (TextView) findViewById(R.id.result_likes);
        ok = (Button) findViewById(R.id.ok);
        tvTime = (TextView) findViewById(R.id.time);
        send = (Button) findViewById(R.id.send_comment);
        leave_a_comment = (EditText) findViewById(R.id.leave_a_comment);
        comments_list = findViewById(R.id.comments);
        heading = (TextView) findViewById(R.id.heading);
        refuse = (Button) findViewById(R.id.reject);
        autoLinkTextView = (AutoLinkTextView) findViewById(R.id.tags);

        Intent intent = getIntent();
        final String txt_title = intent.getStringExtra("title");
        final String txt_description = intent.getStringExtra("description");
        final String image_link = intent.getStringExtra("image link");
        final String txt_heading = intent.getStringExtra("heading");
        final HashMap<String, String> rating = new HashMap<>();
        final String text_author = intent.getStringExtra("author");
        final HashMap<String, Comment> comment = new HashMap<>();
        final ArrayList<String> author_posts = intent.getStringArrayListExtra("author_posts");
        final String where = intent.getStringExtra("Where");
        final String login = intent.getStringExtra("login");
        final String text_time = intent.getStringExtra("time");
        final DecimalFormat df = new DecimalFormat("#.##");
        pos = intent.getIntExtra("position", 0);
        tags = intent.getStringArrayListExtra("tags");
        role = intent.getStringExtra("role");
        path = intent.getStringExtra("post path");
        tvTime.setText(text_time);
        author.setText(text_author);
        DatabaseReference user_rating = FirebaseDatabase.getInstance().getReference();
        user_rating.child("Users").child(login).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences preferences = getSharedPreferences("User_rating", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putFloat("current_user", dataSnapshot.getValue(Float.TYPE));
                ed.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference author_rating = FirebaseDatabase.getInstance().getReference();
        author_rating.child("Users").child(text_author).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float rating = dataSnapshot.getValue(Float.TYPE);
                SharedPreferences preferences = getSharedPreferences("Author_rating", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putFloat("rating", rating);
                ed.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(where).child(path).child("rating");
        //рейтинг поста
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int result = 0;
                boolean is_here = false;
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(!i.getKey().equals("zero")){
                        result = i.getValue().toString().equals("up") ? result + 1 : result - 1;
                    }
                    if(i.getKey().equals(login)){
                        final float author_rating = getSharedPreferences("Author_rating", MODE_PRIVATE).getFloat("rating", 0);

                        is_here = true;
                        if(i.getValue().toString().equals("up")){
                            upvote.setBackground(getResources().getDrawable(R.drawable.ic_thumb_up_activated_24dp));
                            downvote.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    upvote.setBackground(getResources().getDrawable(R.drawable.ic_thumb_up_24dp));
                                    DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                    vote.child(where).child(path).child("rating").child(login).removeValue();
                                    DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                                    update_author_rating.child("Users").child(text_author).child("rating").
                                            setValue(author_rating);
                                }
                            });

                        }else{
                            downvote.setBackground(getResources().getDrawable(R.drawable.ic_thumb_down_activated_24dp));
                            upvote.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downvote.setBackground(getResources().getDrawable(R.drawable.ic_thumb_up_24dp));
                                    DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                    vote.child(where).child(path).child("rating").child(login).removeValue();
                                    DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                                    update_author_rating.child("Users").child(text_author).child("rating").
                                            setValue(author_rating + 1);
                                    DatabaseReference update_user_rating = FirebaseDatabase.getInstance().getReference();
                                    update_user_rating.child("Users").child(login).child("rating").
                                            setValue(getSharedPreferences("User_rating", MODE_PRIVATE).
                                                    getFloat("current_user", 0) + 1);
                                }
                            });
                        }
                    }
                    if(!is_here){
                        upvote.setBackground(getResources().getDrawable(R.drawable.ic_thumb_up_24dp));
                        downvote.setBackground(getResources().getDrawable(R.drawable.ic_thumb_down_24dp));
                        upvote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                vote.child(where).child(path).child("rating").child(login).setValue("up");
                                final float author_rating = getSharedPreferences("Author_rating", MODE_PRIVATE).getFloat("rating", 0);
                                DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                                update_author_rating.child("Users").child(text_author).child("rating").
                                        setValue(author_rating + 1);
                            }
                        });
                        downvote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                vote.child(where).child(path).child("rating").child(login).setValue("down");
                                final float author_rating = getSharedPreferences("Author_rating", MODE_PRIVATE).getFloat("rating", 0);
                                DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                                update_author_rating.child("Users").child(text_author).child("rating").
                                        setValue(author_rating - 1);
                                DatabaseReference update_user_rating = FirebaseDatabase.getInstance().getReference();
                                update_user_rating.child("Users").child(login).child("rating").
                                        setValue(getSharedPreferences("User_rating", MODE_PRIVATE).
                                                getFloat("current_user", 0) - 1);
                            }
                        });
                    }
                }
                result_rating.setText(Integer.toString(result));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference avatar_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login);
        avatar_ref.child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getActivity).load(dataSnapshot.getValue().toString()).into(author_avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // подсветка ссылок и хэштегов
        autoLinkTextView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_URL);
        autoLinkTextView.setHashtagModeColor(ContextCompat.getColor(this, R.color.colorAccent));
        autoLinkTextView.setUrlModeColor(ContextCompat.getColor(this, R.color.blue_800));
        autoLinkTextView.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                String[] tag = matchedText.split(" ");
                String res = "";
                for(String i:tag){
                    res += i;
                }
                if(res.charAt(0) == '#') {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", res);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, "Tag copied", Toast.LENGTH_SHORT).show();
                }else{
                    Uri address = Uri.parse(res);
                    Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                    startActivity(openlink);
                }


            }
        });

        String to_tag = "";
        for(String s:tags){
            to_tag += s + " ";
        }
        autoLinkTextView.setAutoLinkText(to_tag);

        // модерация
        if(!role.equals("user")){
            ok.setVisibility(View.VISIBLE);
            refuse.setVisibility(View.VISIBLE);
            comments_list.setVisibility(View.GONE);

            // пост допущен в общую ленту
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rating.put("zero", "nothing");
                    HashMap<String, String> likes = new HashMap<>();
                    likes.put("zero", "nothing");
                    comment.put("zero", new Comment("nothing", "interesting", "in here", likes));
                    RecyclerItem post = new RecyclerItem(txt_title, txt_description, image_link,
                            txt_heading, tags, rating, comment, text_author, text_time);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Data").push().setValue(post, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            DatabaseReference posts_num_update = FirebaseDatabase.getInstance().getReference();
                            posts_num_update.child("Users").child(text_author).child("posts").push().setValue(databaseReference.getKey());
                            Toast.makeText(context, "Post added", Toast.LENGTH_SHORT).show();
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            db.child("Moderate").child(path).removeValue();
                        }
                    });

                }
            });
            // пост не допущен в общую ленту
            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("Moderate").child(path).removeValue();
                    Toast.makeText(getActivity, "Success", Toast.LENGTH_SHORT).show();

                }
            });
        }

        //считывание комментариев с базы данных
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child(path);
        databaseReference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> opinion = new ArrayList<>();
                ArrayList<String> comment_paths = new ArrayList<>();
                 for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(!i.getKey().equals("zero")) {
                        opinion.add(i.getValue(Comment.class));
                        comment_paths.add(i.getKey());
                    }
                }
                CommentAdapter adapter = new CommentAdapter(context, R.layout.comment_item, opinion.toArray(new Comment[0]), where, path, comment_paths);
                comments_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });

        title.setText(txt_title);
        description.setText(txt_description);
        Glide.with(PostPage.this).load(image_link).into(imageView);
        heading.setText(txt_heading);

        //закроет текущее окно
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("position", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putInt("position", pos);
                ed.apply();
                finish();
            }
        });
        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity, AnotherUserPage.class);
                intent.putExtra("author", author.getText().toString());
                getActivity.startActivity(intent);
            }
        });
        author_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity, AnotherUserPage.class);
                intent.putExtra("author", author.getText().toString());
                getActivity.startActivity(intent);
            }
        });

        //откроет новое окно только с картинкой
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity, PhotoPage.class);
                intent.putExtra("link", image_link);
                startActivity(intent);
            }
        });

        // отправка комментария
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncRequest().execute();
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
                    client.setDefaultTimeout(10000);
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

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data");
            HashMap<String, String> months = new HashMap<>();
            months.put("May", "мая");
            months.put("Jun", "июня");
            months.put("Jul", "июля");
            months.put("Aug", "августа");

            months.put("Sep", "сентября");
            months.put("Oct", "октября");
            months.put("Nov", "ноября");
            months.put("Dec", "декабря");

            months.put("Jan", "января");
            months.put("Feb", "февраля");
            months.put("Mar", "марта");
            months.put("Apr", "апреля");

            String[] time_data = s.split(" ");
            String time_for_database = time_data[3] + " " + time_data[2] +
                    "." + months.get(time_data[1]) + "." + time_data[5];

            HashMap<String, String> likes = new HashMap<>();
            likes.put("zero", "nothing");
            reference.child(path).child("comments").push().setValue(
                    new Comment(FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0],
                    leave_a_comment.getText().toString(),
                            time_for_database, likes));
            leave_a_comment.setText("");

        }
    }



}
