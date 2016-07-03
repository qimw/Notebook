package com.example.lenovo.notebook.Base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lenovo.notebook.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/5/6.
 */
public class BaseActivity extends AppCompatActivity {

    private static ArrayList<AppCompatActivity> activityManager = new ArrayList<AppCompatActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        activityManager.add(this);
    }

    @Override
    protected void  onDestroy(){
        super.onDestroy();
        activityManager.remove(this);
    }

    public static void finishAll(){
        for(AppCompatActivity a:activityManager){
            if(!a.isFinishing()){
                a.finish();
            }
        }
    }


}
