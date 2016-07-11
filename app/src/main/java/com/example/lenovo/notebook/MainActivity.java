package com.example.lenovo.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.global.NotebookApp;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        AVOSCloud.initialize(this,"iK548FuYvSfdIgyif1HN5ul7-gzGzoHsz","LCAxo0S3wXaUcmtvqlzifw9k");
        //SharedPreferences.Editor editor = getSharedPreferences("password",MODE_PRIVATE).edit();
        SharedPreferences sp = getSharedPreferences("password",MODE_PRIVATE);





        if(!sp.getBoolean("isCreated",false)){
            Intent intent = new Intent(MainActivity.this,IntroduceActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in,R.anim.anim_out);

        }else{
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
        }

        Button write = (Button) findViewById(R.id.write);
        Button read = (Button) findViewById(R.id.read);
        Button reset = (Button)findViewById(R.id.reset);
        Button cloud = (Button)findViewById(R.id.cloud);


        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,WriteActivity.class);
                intent.putExtra("isNew",true);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,ResetActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
        cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Cloud.class);
                startActivity(intent);
            }
        });
    }

}
