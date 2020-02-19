package com.example.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.UUID;

public class Add_post extends Fragment implements View.OnClickListener {

    private Button btnChoose, btnUpload, btnRetrieve;
    private ImageView imageView;
    EditText title;
    EditText description;
    Button photo, post;
    private Uri filePath;
    public String txtTitle="Title", txtDescription="Description", txtImage = "0-0";
    FirebaseStorage storage;
    StorageReference storageReference;

    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.add_post_activity, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnChoose = (Button) myView.findViewById(R.id.add_photo);
        btnUpload = (Button) myView.findViewById(R.id.button_send);
        imageView = (ImageView) myView.findViewById(R.id.imgView);
        description = (EditText) myView.findViewById(R.id.Description);
        title = (EditText) myView.findViewById(R.id.title);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        return myView;

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
    public void onClick(View v){
        switch (v.getId()){
            case R.id.add_photo:
                chooseImage();
                break;
            case R.id.button_send:
                uploadImage();
            default:
                break;
        }
    }

    private void uploadPost(String image) {
        Data data = new Data(txtDescription, image, txtTitle);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Data").push().setValue(data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(getActivity(), "Post added.", Toast.LENGTH_SHORT).show();
                title.setText("");
                description.setText("");
                imageView.setImageResource(android.R.color.transparent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == -1
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image = uri.toString();
                                    uploadPost(image);
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

}
