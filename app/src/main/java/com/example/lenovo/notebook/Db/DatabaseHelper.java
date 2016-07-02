package com.example.lenovo.notebook.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.lenovo.notebook.global.NotebookApp;

/**
 * Created by lenovo on 2016/5/6.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    Context mContext;

    public static final String CREATE_DATA = "create table article_one("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"content text)";


    public DatabaseHelper (Context context,String name, int version){
        super(context, name, null, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){

    }

}
