package com.example.lenovo.notebook.LeanCloud;

import android.content.Intent;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.WriteActivity;
import com.example.lenovo.notebook.global.NotebookApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/7/10.
 */
public class LeanCloud  {

    public static boolean updateLocal(){


        AVQuery<AVObject> query = new AVQuery<>("test");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for(int i = 0;i < list.size();i++){
                    AVObject avObject = list.get(i);
                    String title = avObject.getString("title");
                    String content = avObject.getString("content");
                    Intent intent = new Intent();
                    intent.putExtra("title",title);
                    intent.putExtra("content",content);
                    intent.putExtra("status",1);

                    int result = Database.add(intent);
                    if(result == Database.SAVE_SUCCEED){
                        Log.d("holo","保存成功");
                    }else if(result == Database.TITLE_EXIST){
                       Log.d("holo","标题已存在");
                    }else if(result == Database.FAILED_TO_SAVE){
                        Log.d("holo","保存失败");
                    }
                }

            }
        });
        return true;
    }
    public static boolean updateCloud(){

        Cursor cursor = Database.query();
         AVObject avObject;

        if(cursor.moveToFirst()){
            do{

                int status = cursor.getInt(cursor.getColumnIndex("status"));

                if(status == 0){

                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    avObject = new AVObject("test");
                    avObject.put("title",title);
                    avObject.put("content",content);
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {

                                // 存储成功
                            } else {
                                Toast.makeText(NotebookApp.getInstance().getApplicationContext(),"云同步失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        return true;
    }
}
