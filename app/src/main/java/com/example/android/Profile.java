package com.example.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends Fragment {
    String role, login, password, fake_password;
    TextView change;
    Button look_password, confirm, exit;
    EditText new_password;
    String updated = "";
    boolean is = true;
    private TextView tv_login, tv_password, tv_role;

    Profile(String role, String login, String passowrd){
        this.role = role;
        this.login = login;
        this.password = "Password: " + passowrd;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_profile, null);

    }

    @Override
    public void onStart() {
        super.onStart();
        tv_login = (TextView) getActivity().findViewById(R.id.login);
        tv_role  =(TextView) getActivity().findViewById(R.id.role);
        tv_password = (TextView) getActivity().findViewById(R.id.password);
        look_password = (Button) getActivity().findViewById(R.id.look_password);
        change = (TextView) getActivity().findViewById(R.id.change);
        confirm  =(Button) getActivity().findViewById(R.id.confirm);
        exit = (Button) getActivity().findViewById(R.id.exit123456);
        new_password = (EditText) getActivity().findViewById(R.id.new_password);

        tv_role.setText(role);
        tv_login.setText(login);
        fake_password = "Password: ";
        for(int i=0;i<password.length() - "Password: ".length();i++){
            fake_password = fake_password + "*";
        }
        tv_password.setText(fake_password);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is){
                    change.setText("Cancel");
                    is = false;
                    change.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                    look_password.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    tv_password.setVisibility(View.GONE);
                    new_password.setVisibility(View.VISIBLE);
                    new_password.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            updated = s.toString();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(updated.equals("") || updated.length() < 4){
                                Toast.makeText(getContext(), "Too simple", Toast.LENGTH_SHORT).show();
                            }else{
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login);
                                ref.child("password").setValue(updated);
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                look_password.setVisibility(View.VISIBLE);
                                confirm.setVisibility(View.GONE);
                                tv_password.setVisibility(View.VISIBLE);
                                new_password.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    is = true;
                    change.setTextColor(getActivity().getResources().getColor(R.color.bzzzz));
                    change.setText("Change password");
                    look_password.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    tv_password.setVisibility(View.VISIBLE);
                    new_password.setVisibility(View.GONE);
                }

            }
        });

        look_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_password.getText().equals(password)) {
                    look_password.setBackground(getActivity().getDrawable(R.drawable.ic_visibility_unactive_24dp));
                    tv_password.setText(fake_password);

                }else{
                    look_password.setBackground(getActivity().getDrawable(R.drawable.ic_visibility_black_24dp));
                    tv_password.setText(password);
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                ask.setMessage("Are you sure you want to log out?").setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = ask.create();
                alertDialog.setTitle("Log out");
                alertDialog.show();
            }
        });

    }
}
