package com.example.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class Add_post extends Fragment implements View.OnClickListener {

    private Button btnChoose, btnUpload, btnRetrieve;
    private ImageView imageView;
    EditText title;
    EditText description;
    EditText tags;
    Button photo, post;
    Spinner spinner;
    private Uri filePath;
    public String txtTitle = "Title", txtDescription = "Description", txtImage = "0-0", role;
    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayAdapter<String> adapter;
    String heading = "", login;
    String image_link = "";
    String[] text_tags;
    ArrayList<String> tags_db = new ArrayList<>();
    static List<String> headings = new ArrayList<>();

    private final int PICK_IMAGE_REQUEST = 71;

    Add_post(String role, String login) {
        this.role = role;
        this.login = login;
    }

    public Add_post() {
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
        if (role.equals("admin")) {
            headings.add("System message");
        }
        head.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    headings.add(dataSnapshot1.getValue().toString());

                    adapter = new ArrayAdapter<>(myView.getContext(), R.layout.spinner_item, R.id.choose, headings.toArray(new String[headings.size()]));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo:
                chooseImage();
                break;
            case R.id.button_send:
                if (description.getText().toString().equals("") ||
                        tags.getText().toString().equals("") ||
                        title.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Заполните все поля перед отправкой", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            default:
                break;
        }
    }

    private void uploadPost(String image) {
        image_link = image;
        new AsyncRequest().execute();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionOnDeviceImageLabelerOptions options =
                        new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                                .setConfidenceThreshold(0.8f)
                                .build();
                FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                        .getOnDeviceImageLabeler(options);
                labeler.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                for (FirebaseVisionImageLabel label : labels) {
                                    String text = label.getText();
                                    Log.d("Look: ", text);
                                    SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
                                    SharedPreferences.Editor ed = preferences.edit();
                                    ed.putString("TAG", text);
                                    ed.apply();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("LOOK_HERE", e.getMessage());
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
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

    class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            String time = "failed";
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(30000);
                    client.connect("time-a-b.nist.gov");
                    time = client.getDate().toString();
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
            // успешное соединение с сервером
            String time_for_database;
            if (!s.equals("failed")) {
                String[] time_data = s.split(" ");
                for (String i : time_data) {
                    Log.d("Look", i);
                }

                time_for_database = time_data[3] + " " + time_data[2] +
                        "." + months.get(time_data[1]) + "." + time_data[5];

            }
            // если не получилось взять время с сервера
            else {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                time_for_database = dateformat.format(c.getTime());
            }
            for (String i : text_tags) {
                tags_db.add(i);
            }

            tags_db.add("#" + role);
            tags_db.add("#" + login);
            SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
            String tag = preferences.getString("TAG", "");

            tags_db.add("#" + tag);
            HashMap<String, String> rating = new HashMap<>();
            HashMap<String, Comment> comments = new HashMap<>();
            rating.put("zero", "nothing");
            HashMap<String, String> likes = new HashMap<>();
            likes.put("zero", "nothing");
            comments.put("zero", new Comment("nothing", "interesting", "in here", likes));
            RecyclerItem data = new RecyclerItem(txtTitle, txtDescription, image_link, heading, tags_db, rating, comments, login, time_for_database);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            String wh = "Moderate";

            if (role.equals("admin")) {
                wh = "Data";

            }

            databaseReference.child(wh).push().setValue(data, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (role.equals("admin")) {
                        DatabaseReference posts_num_update = FirebaseDatabase.getInstance().getReference();
                        posts_num_update.child("Users").child(login).child("posts").push().setValue(databaseReference.getKey());
                    }
                    title.setText("");
                    description.setText("");
                    imageView.setImageResource(android.R.color.transparent);
                    tags.setText("");
                }
            });


        }
    }


}
