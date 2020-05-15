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
    public float midValue;
    private String role;
    HashMap<String, Float> rate;
    public String login = "";
    ArrayList<String> paths = new ArrayList<>();
    boolean remove = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s, String role, ArrayList<String> paths, String login) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
        this.role = role;
        this.paths = paths;
        this.login = login;
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
        for(float i:itemList.getRating().values()){
            middle += i;
        }
        holder.txtDescription.setText(s);
        holder.heading.setText(itemList.getHeading());
        holder.author.setText(itemList.getAuthor());
        holder.tvTime.setText(itemList.getTime());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(where).child(paths.get(position)).child("rating");
        rate = new HashMap<>();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals(login)){
                        float f =  ds.getValue(Float.TYPE);
                        holder.ratingBar.setRating(f);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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



        if(itemList.getRating().size() == 1){
            midValue = 0;
            holder.middle_rating.setText("0.0");
        }else{
            midValue = middle/(itemList.getRating().size() - 1);
            DecimalFormat df = new DecimalFormat("#.##");
            holder.middle_rating.setText(df.format(midValue));
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

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                SharedPreferences preferences = mContext.getSharedPreferences("position", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putInt("position", position);
                ed.apply();
                databaseReference.child(where).child(paths.get(position)).child("rating").child(login).setValue(rating);
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
        public TextView middle_rating;
        public ImageView picture, author_avatar;
        public TextView heading;
        public TextView author;
        public TextView delete_post;
        public RatingBar ratingBar;
        View v;
        Intent intent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtSave = itemView.findViewById(R.id.save_to_favorite);
            ratingBar  =itemView.findViewById(R.id.rating);
            tvTime = itemView.findViewById(R.id.time);
            delete_post = itemView.findViewById(R.id.delete_post);
            middle_rating = itemView.findViewById(R.id.middle_rating);
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
                    intent.putExtra("rating", listItems.get(pos).getRating().get(login));
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