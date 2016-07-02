package com.example.lenovo.notebook.Db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.lenovo.notebook.global.NotebookApp;

/**
 * Created by lenovo on 2016/5/7.
 */
public class Database {
    private static DatabaseHelper dh = new DatabaseHelper(NotebookApp.getInstance().getApplicationContext(),
          "data",1);
    private static SQLiteDatabase db = dh.getWritableDatabase();

    public static void add(Intent article){
        String title = article.getStringExtra("title");
        if(queryId(title)!=-1){
            Toast.makeText(NotebookApp.getInstance().getApplicationContext(),"抱歉，标题已经存在。",Toast.LENGTH_SHORT).show();
            return;
        }
        String content = article.getStringExtra("content");
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("content",content);
        db.insert("article_one",null,contentValues);
    }

    public static void update(Intent article){
        String title = article.getStringExtra("title");
        String content = article.getStringExtra("content");
        Integer id = article.getIntExtra("id",-1);
        ContentValues values = new ContentValues();
        values.put("content",content);
        values.put("title", title);
        db.update("article_one", values, "id = ?", new String[]{id.toString()});
        Log.d("holo","up sudcc");
    }

    public static void remove(Intent intent){
        Log.d("holo", "long ");
        String title = intent.getStringExtra("title");
        Log.d("holo", "long click2");
        db.delete("article_one","title = ?",new String[]{title});
        Log.d("holo", "long click2");
    }

    public static Cursor query(){
        return db.query("article_one",null,null,null,null,null,null);
    }

    public static int queryId(String title){
        Cursor cursor = db.query("article_one",null,null,null,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                String t = cursor.getString(cursor.getColumnIndex("title"));
                if(t.equals(title)){
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    return id;
                }
            }
        }
        return -1;
    }


}
