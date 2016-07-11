package com.example.lenovo.notebook.global;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by lenovo on 2016/5/6.
 */
public class NotebookApp extends Application {
    private static NotebookApp notebookApp;

    public static NotebookApp getInstance(){
        return notebookApp;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        AVOSCloud.initialize(this,"iK548FuYvSfdIgyif1HN5ul7-gzGzoHsz","LCAxo0S3wXaUcmtvqlzifw9k");
        notebookApp = this;
        Log.d("solo","from app");
    }
}
