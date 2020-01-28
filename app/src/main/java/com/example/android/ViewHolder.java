package com.example.android;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
    }

    public void setDetails(Context context, String title, String description, String image){
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        ImageView imageView = view.findViewById(R.id.picture);

        txtTitle.setText(title);
        txtDescription.setText(description);
        Picasso.get().load(image).into(imageView);


    }
}
