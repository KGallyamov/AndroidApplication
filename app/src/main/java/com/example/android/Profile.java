package com.example.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile extends Fragment {
    String role, login, password, fake_password;
    TextView exit, change;
    Button look_password;
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
        exit = (TextView) getActivity().findViewById(R.id.exit123456);

        tv_role.setText(role);
        tv_login.setText(login);
        fake_password = "Password: ";
        for(int i=0;i<password.length() - "Password: ".length();i++){
            fake_password = fake_password + "*";
        }
        tv_password.setText(fake_password);

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
