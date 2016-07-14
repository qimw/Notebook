package com.example.lenovo.notebook.Db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.parser.deserializer.DateDeserializer;
import com.example.lenovo.notebook.global.NotebookApp;

/**
 * Created by lenovo on 2016/5/7.
 */
public class Database {

    public static final int TITLE_EXIST = 0;
    public static final int FAILED_TO_SAVE = 1;
    public static final int SAVE_SUCCEED = 2;

    private static DatabaseHelper dh = new DatabaseHelper(NotebookApp.getInstance().getApplicationContext(),
          "data",1);
    private static SQLiteDatabase db = dh.getWritableDatabase();

    public static int add(Intent article){
        String title = article.getStringExtra("title");
        if(queryId(title)!=-1){
            return TITLE_EXIST;
        }
        String content = article.getStringExtra("content");
        int status = article.getIntExtra("status",-1);
        if(status == -1){
            return FAILED_TO_SAVE;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("content",content);
        contentValues.put("status",status);//已经完成云同步为1，未完成为0
        db.insert("article_one",null,contentValues);
        return SAVE_SUCCEED;
    }

    public static void update(Intent article){
        String title = article.getStringExtra("title");
        String content = article.getStringExtra("content");
        Integer id = article.getIntExtra("id",-1);
        int status = article.getIntExtra("status",0);
        ContentValues values = new ContentValues();
        values.put("content",content);
        values.put("title", title);
        values.put("status",status);
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

    public static Cursor queryStatus(int status){
        return db.query("article_one",new String[]{"status"},"status == ?",new String[] {""+status},null,null,null);
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

    public static void removeAll(){
        Cursor cursor = Database.query();
        if(cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                Intent intent = new Intent();
                intent.putExtra("title",title);
                Database.remove(intent);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
    public static void ifExisted(String title){

    }


}
