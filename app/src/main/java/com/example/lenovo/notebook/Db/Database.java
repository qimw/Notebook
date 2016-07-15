package com.example.lenovo.notebook.Db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.parser.deserializer.DateDeserializer;
import com.example.lenovo.notebook.global.NotebookApp;

import java.util.Calendar;

/**
 * Created by lenovo on 2016/5/7.
 */
public class Database {


    private static DatabaseHelper dh = new DatabaseHelper(NotebookApp.getInstance().getApplicationContext(),
          "data",1);
    private static SQLiteDatabase db = dh.getWritableDatabase();

    public static void add(Intent article){
        String title = article.getStringExtra("title");
        String content = article.getStringExtra("content");
        int status = article.getIntExtra("status",-1);
        long id = article.getLongExtra("id",0);
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("content",content);
        contentValues.put("status",status);//已经完成云同步为1，未完成为0
        contentValues.put("id", id);
        db.insert("article_one",null,contentValues);
    }

    public static void update(Intent article){
        String title = article.getStringExtra("title");
        String content = article.getStringExtra("content");
        Long id = article.getLongExtra("id",-1);
        int status = article.getIntExtra("status",0);
        ContentValues values = new ContentValues();
        values.put("content",content);
        values.put("title", title);
        values.put("status",status);
        Log.d("holo","from database" + content);
        Log.d("holo","from database" + id.toString());
        db.update("article_one", values, "id = ?", new String[]{id.toString()});
    }

    public static void remove(Intent intent){
        long id = intent.getLongExtra("id",0);
        db.delete("article_one","id = ?",new String[]{id + ""});
    }

    public static Cursor query(){
        return db.query("article_one",null,null,null,null,null,null);
    }

    public static int queryStatus(long id){
        Cursor cursor = Database.query();
        if(cursor.moveToFirst()){
            long tempId = cursor.getLong(cursor.getColumnIndex("id"));
            if(id == tempId){
                cursor.close();
                return cursor.getInt(cursor.getColumnIndex("status"));
            }
        }
        cursor.close();
        return 0;
    }

//    public static int queryId(String title){
//        Cursor cursor = db.query("article_one",null,null,null,null,null,null);
//        if(cursor != null){
//            while(cursor.moveToNext()){
//                String t = cursor.getString(cursor.getColumnIndex("title"));
//                if(t.equals(title)){
//                    int id = cursor.getInt(cursor.getColumnIndex("id"));
//                    return id;
//                }
//            }
//        }
//        return -1;
//    }

//    public static void removeAll(){
//        Cursor cursor = Database.query();
//        if(cursor.moveToFirst()){
//            do {
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                Intent intent = new Intent();
//                intent.putExtra("title",title);
//                Database.remove(intent);
//            }while(cursor.moveToNext());
//        }
//        cursor.close();
//    }
    public static void ifExisted(String title){

    }


}
