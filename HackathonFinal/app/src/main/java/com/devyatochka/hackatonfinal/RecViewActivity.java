package com.devyatochka.hackatonfinal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexbelogurow on 01.04.17.
 */

public class RecViewActivity extends AppCompatActivity {
    private List<Profile> profiles;
    private RecyclerView recyclerView;
    private FloatingActionButton floatButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_activity);

        recyclerView = (RecyclerView)findViewById(R.id.rv_profile);
        floatButton = (FloatingActionButton) findViewById(R.id.fab);

        //mToolBar = (Toolbar) findViewById(R.id.toolbarRecView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        initializeData();
        initializeAdapter();

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecViewActivity.this);
                LayoutInflater inflater = getLayoutInflater();

                builder.setView(inflater.inflate(R.layout.fragment_login, null))
                        .setPositiveButton("Add data", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText login = (EditText)((AlertDialog)dialog).findViewById(R.id.username);
                                EditText password = (EditText)((AlertDialog)dialog).findViewById(R.id.password);

                                SharedPreferences mIDpreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
                                SharedPreferences mpreferences = getSharedPreferences("pin_code", Context.MODE_PRIVATE);

                                String key = mIDpreferences.getString("uuid","-1");
                                key += mpreferences.getString("pin", "-1");
                                DESedeEncryption encryption = null;
                                try {
                                    encryption = new DESedeEncryption(key);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                profiles.add(new Profile(login.getText().toString(),encryption.encrypt(password.getText().toString())));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    private void initializeAdapter() {
        ProfileAdapter adapter = new ProfileAdapter(profiles);
        recyclerView.setAdapter(adapter);
        //adapter.setHasStableIds(true);
    }

    private void initializeData() {
        profiles = new ArrayList<>();

    }

}
