package com.example.ricardo.expoingenieria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
    }

    public void login (View v){
        Intent login_intent = new Intent (this,LoginActivity.class);
        startActivity(login_intent);
    }
    public void signup (View v){
        Intent signup_intent = new Intent (this,SignUpActivity.class);
        startActivity(signup_intent);
    }
}
