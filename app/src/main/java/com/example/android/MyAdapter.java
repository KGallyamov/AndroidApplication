package com.example.android;

import android.content.Context;
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
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    public List<RecyclerItem> listItems;
    private Context mContext;
    public String where = "";

    public MyAdapter(List<RecyclerItem> listItems, Context mContext, String s) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.where = s;
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


        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDescription.setText(itemList.getDescription());

        holder.picture.setImageBitmap(itemList.getImage());
        //Picasso.get().load(itemList.getImage()).into(holder.picture);

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
                                txtImage = "https://firebasestorage.googleapis.com/v0/b/android-824bc.appspot.com/o/images%2F397f6d9b-31d7-4aec-9fd4-426e5fb3c104?alt=media&token=f7d2a505-9831-4d34-8f15-02069bd4a580";
                                txtTitle = recyclerItem.getTitle();
                                Data data = new Data(txtDescription, txtImage, txtTitle);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                databaseReference.child("Favorite").push().setValue(data, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    }
                                });
//                                Information inf = new Information();
//                                RecyclerItem ri = listItems.get(position);
//                                inf.writeFile(ri.getTitle());

                                Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.mnu_item_delete:

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtOption = itemView.findViewById(R.id.txtOptionDigit);
            picture = itemView.findViewById(R.id.picture);


        }
    }


}