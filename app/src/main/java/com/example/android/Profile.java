package com.example.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile extends Fragment {
    String role, login;
    TextView exit;
    private TextView tv_login, password, tv_role;

    Profile(String role, String login){
        this.role = role;
        this.login = login;
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
        exit = (TextView) getActivity().findViewById(R.id.exit123456);

        tv_role.setText(role);
        tv_login.setText(login);

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
