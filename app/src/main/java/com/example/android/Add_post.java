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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Add_post extends Fragment implements View.OnClickListener {

    private Button btnChoose, btnUpload, btnRetrieve;
    private ImageView imageView;
    EditText title;
    EditText description;
    EditText tags;
    Button photo, post;
    Spinner spinner;
    private Uri filePath;
    public String txtTitle="Title", txtDescription="Description", txtImage = "0-0", role;
    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayAdapter<String> adapter;
    String heading = "", login;
    String [] text_tags;
    ArrayList<String> tags_db = new ArrayList<>();
    static List<String> headings = new ArrayList<>();

    private final int PICK_IMAGE_REQUEST = 71;

    Add_post(String role, String login){
        this.role = role;
        this.login = login;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.add_post_activity, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnChoose = (Button) myView.findViewById(R.id.add_photo);
        btnUpload = (Button) myView.findViewById(R.id.button_send);
        imageView = (ImageView) myView.findViewById(R.id.imgView);
        description = (EditText) myView.findViewById(R.id.Description);
        title = (EditText) myView.findViewById(R.id.title);
        tags = (EditText) myView.findViewById(R.id.tags);
        spinner = (Spinner) myView.findViewById(R.id.spinner);

        DatabaseReference head = FirebaseDatabase.getInstance().getReference().child("Headings");
        if(role.equals("admin")){
            headings.add("System message");
        }
        head.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    headings.add(dataSnapshot1.getValue().toString());
                    adapter = new ArrayAdapter<String>(myView.getContext(), android.R.layout.simple_spinner_item, headings.toArray(new String[headings.size()]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        spinner.setPrompt("Choose heading");
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                heading = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        tags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_tags = s.toString().split(" ");


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        for(String i:text_tags){
            tags_db.add(i);
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");
        String[] datetime = dateformat.format(c.getTime()).split(" ")[0].split("-");
        tags_db.add("#" + datetime[0] + datetime[1]);
        tags_db.add("#" + datetime[2]);
        tags_db.add("#" + role);
        tags_db.add("#" + login);
            Data data = new Data(txtDescription, image, txtTitle, heading, tags_db);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            String wh = "Moderate";

            if (role.equals("admin")) {
                wh = "Data";
            }

            databaseReference.child(wh).push().setValue(data, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(getActivity(), "Post added.", Toast.LENGTH_SHORT).show();
                    title.setText("");
                    description.setText("");
                    imageView.setImageResource(android.R.color.transparent);
                    tags.setText("");
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
