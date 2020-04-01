package com.example.android;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    public List<RecyclerItem> listItems;
    private Context mContext;
    public String where = "";
    public float midValue;
    private String role;
    HashMap<String, Float> rate;
    public static String login = "";
    ArrayList<String> paths = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s, String role) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
        this.role = role;
    }
    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s, String role, ArrayList<String> paths) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
        this.role = role;
        this.paths = paths;
    }
    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s, String role, ArrayList<String> paths, String login) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
        this.role = role;
        this.paths = paths;
        this.login = login;
    }
    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s, String role, String login) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
        this.role = role;
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
            s = itemList.getDescription().substring(0, 37) + "...";
        }catch (Exception e){
            s = itemList.getDescription();
        }
        for(float i:itemList.getRating().values()){
            middle += i;
        }
        holder.txtDescription.setText(s);
        holder.heading.setText(itemList.getHeading());
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



        if(itemList.getRating().size() == 1){
            midValue = 0;
            holder.middle_rating.setText("0.0");
        }else{
            midValue = middle/(itemList.getRating().size() - 1);
            holder.middle_rating.setText(Float.toString(middle/(itemList.getRating().size() - 1)));
        }

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                databaseReference.child(where).child(paths.get(position)).child("rating").child(login).setValue(rating);
            }
        });

        Glide.with(mContext).load(itemList.getImage()).into(holder.picture);

        if(where.equals("Favorite")){
            holder.txtSave.setBackground(mContext.getDrawable(R.drawable.ic_star_black_24dp));
        }else{
            holder.txtSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    databaseReference.child("Favorite"+login).push().setValue(listItems.get(position), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(mContext, "Post added.", Toast.LENGTH_SHORT).show();

                        }
                    });
                    holder.txtSave.setBackground(mContext.getDrawable(R.drawable.ic_star_black_24dp));
                }
            });
        }

    }
    @IgnoreExtraProperties
    public class Data{
        public String description;
        public String image;
        public String title;
        public String heading;
        public ArrayList<String> tags;


        public Data(){
        }

        public Data(String description, String image, String title, String heading, ArrayList<String> tags){
            this.description = description;
            this.image = image;
            this.title = title;
            this.heading = heading;
            this.tags = tags;
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtSave;
        public TextView middle_rating;
        public ImageView picture;
        public Button heading;
        public RatingBar ratingBar;
        View v;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtSave = itemView.findViewById(R.id.save_to_favorite);
            ratingBar  =itemView.findViewById(R.id.rating);
            middle_rating = itemView.findViewById(R.id.middle_rating);
            picture = itemView.findViewById(R.id.picture);
            heading = itemView.findViewById(R.id.heading);


        }

        public void click(final int position) {
            final int pos = position;


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostPage.class);
                    intent.putExtra("title", listItems.get(pos).getTitle());
                    intent.putExtra("description", listItems.get(pos).getDescription());
                    intent.putExtra("image link", listItems.get(pos).getImage());
                    intent.putExtra("heading", listItems.get(pos).getHeading());
                    intent.putExtra("role", role);
                    intent.putExtra("Where", where);
                    intent.putExtra("login", login);
                    intent.putExtra("tags", listItems.get(pos).getTags());
                    intent.putExtra("rating", listItems.get(pos).getRating().get(login));
                    if(paths.size() > 0) {
                        intent.putExtra("post path", paths.get(pos));
                    }

                    mContext.startActivity(intent);
                }
            });
        }
    }
}