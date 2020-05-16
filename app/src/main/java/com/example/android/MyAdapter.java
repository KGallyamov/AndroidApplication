package com.example.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    public List<RecyclerItem> listItems;
    private Context mContext;
    public String where = "";
    private String role;
    public String login = "";
    ArrayList<String> paths = new ArrayList<>();
    boolean remove = false;
    float current_user_rating;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s, String role, ArrayList<String> paths, String login, float current_user_rating) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
        this.role = role;
        this.paths = paths;
        this.login = login;
        this.current_user_rating = current_user_rating;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerItem itemList = listItems.get(position);
        float middle = 0;
        holder.click(position);
        holder.txtTitle.setText(itemList.getTitle());
        String s;
        try{
            s = itemList.getDescription().substring(0, 57) + "...";
        }catch (Exception e){
            s = itemList.getDescription();
        }
        DatabaseReference author_rating = FirebaseDatabase.getInstance().getReference();
        author_rating.child("Users").child(itemList.getAuthor()).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float rating = dataSnapshot.getValue(Float.TYPE);
                SharedPreferences preferences = mContext.getSharedPreferences("Author_rating", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putFloat("rating", rating);
                ed.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.txtDescription.setText(s);
        holder.heading.setText(itemList.getHeading());
        holder.author.setText(itemList.getAuthor());
        holder.tvTime.setText(itemList.getTime());

        if(role.equals("admin") || itemList.getAuthor().equals(login)){
            holder.delete_post.setVisibility(View.VISIBLE);
            holder.delete_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder ask = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                    ask.setMessage("Are you sure you want to delete this post?").setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference del = FirebaseDatabase.getInstance().getReference();
                                    reference.child(where).child(paths.get(position)).removeValue();
                                    del.child("Users").child(login).child("posts").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot i:dataSnapshot.getChildren()){
                                                if(i.getValue().toString().equals(paths.get(position))){
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                            .child("Users").child(login).child("posts").child(i.getKey());
                                                    ref.removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = ask.create();
                    alertDialog.setTitle("Delete post");
                    alertDialog.show();
                }
            });
        }



        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnotherUserPage.class);
                intent.putExtra("author", holder.author.getText().toString());
                mContext.startActivity(intent);
            }
        });
        holder.author_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnotherUserPage.class);
                intent.putExtra("author", holder.author.getText().toString());
                mContext.startActivity(intent);
            }
        });
        DatabaseReference likes = FirebaseDatabase.getInstance().getReference();
        likes.child(where).child(paths.get(position)).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int result = 0;
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(!i.getKey().equals("zero")){
                        result = i.getValue().toString().equals("up") ? result + 1 : result - 1;
                    }
                    if(i.getKey().equals(login)){
                        final float author_rating = mContext.getSharedPreferences("Author_rating", MODE_PRIVATE).getFloat("rating", 0);
                        if(i.getValue().toString().equals("up")){
                            holder.upvote.setBackground(mContext.getResources().getDrawable(R.drawable.ic_thumb_up_activated_24dp));
                            holder.downvote.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                    vote.child(where).child(paths.get(position)).child("rating").child(login).removeValue();
                                    DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                                    update_author_rating.child("Users").child(itemList.getAuthor()).child("rating").
                                            setValue(author_rating - 1);
                                }
                            });

                        }else{
                            holder.downvote.setBackground(mContext.getResources().getDrawable(R.drawable.ic_thumb_down_activated_24dp));
                            holder.upvote.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                    vote.child(where).child(paths.get(position)).child("rating").child(login).removeValue();
                                    DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                                    update_author_rating.child("Users").child(itemList.getAuthor()).child("rating").
                                            setValue(author_rating + 1);
                                    DatabaseReference update_user_rating = FirebaseDatabase.getInstance().getReference();
                                    update_user_rating.child("Users").child(login).child("rating").
                                            setValue(current_user_rating + 1);

                                }
                            });
                        }
                    }
                }
                holder.result_rating.setText(Integer.toString(result));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                vote.child(where).child(paths.get(position)).child("rating").child(login).setValue("up");
                final float author_rating = mContext.getSharedPreferences("Author_rating", MODE_PRIVATE).getFloat("rating", 0);
                DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                update_author_rating.child("Users").child(itemList.getAuthor()).child("rating").
                        setValue(author_rating + 1);
            }
        });
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                vote.child(where).child(paths.get(position)).child("rating").child(login).setValue("down");
                final float author_rating = mContext.getSharedPreferences("Author_rating", MODE_PRIVATE).getFloat("rating", 0);
                DatabaseReference update_author_rating = FirebaseDatabase.getInstance().getReference();
                update_author_rating.child("Users").child(itemList.getAuthor()).child("rating").
                        setValue(author_rating - 1);
                DatabaseReference update_user_rating = FirebaseDatabase.getInstance().getReference();
                update_user_rating.child("Users").child(login).child("rating").
                        setValue(current_user_rating - 1);
            }
        });




        Glide.with(mContext).load(itemList.getImage()).into(holder.picture);
        DatabaseReference avatar_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login);
        avatar_ref.child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(mContext).load(dataSnapshot.getValue().toString()).into(holder.author_avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(where.equals("Favorite")){
            holder.txtSave.setBackground(mContext.getDrawable(R.drawable.ic_star_black_24dp));
            holder.txtSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Favorite" + login).child(paths.get(position)).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(mContext, "Post removed.", Toast.LENGTH_SHORT).show();
                            Log.d("HERE", "");
                        }
                    });
                }
            });
        }else{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Favorite" + login).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot i:dataSnapshot.getChildren()){
                        if(i.getKey().equals(paths.get(position))){
                            remove = true;
                            holder.txtSave.setBackground(mContext.getDrawable(R.drawable.ic_star_black_24dp));
                        }
                    }
                    holder.txtSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!remove) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Favorite" + login).child(paths.get(position)).setValue(listItems.get(position), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Toast.makeText(mContext, "Post added.", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                holder.txtSave.setBackground(mContext.getDrawable(R.drawable.ic_star_black_24dp));
                            }else{
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Favorite" + login).child(paths.get(position)).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Toast.makeText(mContext, "Post removed.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                holder.txtSave.setBackground(mContext.getDrawable(R.drawable.ic_star_border_black_24dp));
                            }

                            remove = !remove;
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTitle;
        public TextView tvTime;
        public TextView txtDescription;
        public TextView txtSave;
        public ImageView picture, author_avatar;
        public TextView heading;
        public TextView author;
        public TextView delete_post;
        public TextView upvote, downvote, result_rating;
        View v;
        Intent intent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            txtTitle = itemView.findViewById(R.id.txtTitle);
            result_rating = itemView.findViewById(R.id.result_likes);
            upvote = itemView.findViewById(R.id.up);
            downvote = itemView.findViewById(R.id.down);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtSave = itemView.findViewById(R.id.save_to_favorite);
            tvTime = itemView.findViewById(R.id.time);
            delete_post = itemView.findViewById(R.id.delete_post);
            picture = itemView.findViewById(R.id.picture);
            heading = itemView.findViewById(R.id.heading);
            author = itemView.findViewById(R.id.author);
            author_avatar = itemView.findViewById(R.id.author_avatar);


        }

        public void click(final int position) {
            final int pos = position;

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent = new Intent(mContext, PostPage.class);
                    intent.putExtra("title", listItems.get(pos).getTitle());
                    intent.putExtra("description", listItems.get(pos).getDescription());
                    intent.putExtra("image link", listItems.get(pos).getImage());
                    intent.putExtra("heading", listItems.get(pos).getHeading());
                    intent.putExtra("role", role);
                    intent.putExtra("Where", where);
                    intent.putExtra("login", login);
                    intent.putExtra("position", pos);
                    intent.putExtra("time", listItems.get(pos).getTime());
                    intent.putExtra("author", listItems.get(pos).getAuthor());
                    intent.putExtra("tags", listItems.get(pos).getTags());
                    if(paths.size() > 0) {
                        intent.putExtra("post path", paths.get(pos));
                    }
                    DatabaseReference author_posts = FirebaseDatabase.getInstance().getReference().child("Users").child(author.getText().toString());
                    author_posts.child("posts").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> posts = new ArrayList<>();
                            for(DataSnapshot i:dataSnapshot.getChildren()){
                                if(!i.getKey().equals("zero")){
                                    posts.add(i.getValue().toString());
                                }
                            }
                            intent.putExtra("author_posts", posts);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mContext.startActivity(intent);
                }
            });
        }
    }
}