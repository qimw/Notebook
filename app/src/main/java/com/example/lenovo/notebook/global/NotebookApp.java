package com.example.lenovo.notebook.global;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

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
        notebookApp = this;
        Log.d("solo","from app");
    }
}
