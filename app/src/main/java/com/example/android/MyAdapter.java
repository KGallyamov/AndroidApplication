package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    public List<RecyclerItem> listItems;
    private Context mContext;
    public String where = "";
    private String role;
    ArrayList<String> paths;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerItem itemList = listItems.get(position);

        holder.click(position);
        holder.txtTitle.setText(itemList.getTitle());
        String s = "";
        try{
            s = itemList.getDescription().substring(0, 37) + "...";
        }catch (Exception e){
            s = itemList.getDescription();
        }
        holder.txtDescription.setText(s);

        Glide.with(mContext).load(itemList.getImage()).into(holder.picture);

        holder.txtOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOption);
                if(where.equals("main")){
                    popupMenu.inflate(R.menu.option_menu);
                }else {
                    popupMenu.inflate(R.menu.favorite_option_menu);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.mnu_item_save:
                                String txtDescription, txtImage, txtTitle;
                                RecyclerItem recyclerItem = listItems.get(position);
                                txtDescription = recyclerItem.getDescription();
                                txtImage = recyclerItem.getImage();
                                txtTitle = recyclerItem.getTitle();
                                Data data = new Data(txtDescription, txtImage, txtTitle);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Favorite").push().setValue(data, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case R.id.mnu_item_delete:
                                //TODO: удаление из бд (ТОЛЬКО сохраненных для юзера) или (главным модером любых постов)
                                //DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                //db.child("Data").removeValue();
                                break;
                            case R.id.mnu_item_delete_1:
                                listItems.remove((position));
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    @IgnoreExtraProperties
    public class Data{
        public String description;
        public String image;
        public String title;

        public Data(){
        }

        public Data(String description, String image, String title){
            this.description = description;
            this.image = image;
            this.title = title;
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtOption;
        public ImageView picture;
        View v;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtOption = itemView.findViewById(R.id.txtOptionDigit);
            picture = itemView.findViewById(R.id.picture);

        }

        public void click(int position) {
            final int pos = position;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, PostPage.class);
                    intent.putExtra("title", listItems.get(pos).getTitle());
                    intent.putExtra("description", listItems.get(pos).getDescription());
                    intent.putExtra("image link", listItems.get(pos).getImage());
                    intent.putExtra("role", role);
                    intent.putExtra("post path", paths.get(pos));

                    mContext.startActivity(intent);
                }
            });
        }
    }
}