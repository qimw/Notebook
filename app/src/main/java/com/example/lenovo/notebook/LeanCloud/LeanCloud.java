package com.example.lenovo.notebook.LeanCloud;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.lenovo.notebook.BitmapHelper.BitmapHelper;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.RichEditor.Decoder;
import com.example.lenovo.notebook.WriteActivity;
import com.example.lenovo.notebook.global.NotebookApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2016/7/10.
 */
public class LeanCloud  {
    private static AVFile avFile;

    public static boolean updateLocal(){

        BitmapHelper.deleteAll();
        Database.removeAll();
        //下载笔记
        AVQuery<AVObject> query = new AVQuery<>("Notes");
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
        //下载图片
        query = new AVQuery<>("_File");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                List<AVObject> priorityEqualsZeroTodos = list;// 符合 priority = 0 的 Todo 数组
                for(final AVObject object :  priorityEqualsZeroTodos){
                    Log.d("holo",object.get("name").toString());
                    AVFile download = new AVFile(object.get("name").toString(),object.get("url").toString(),new HashMap<String, Object>());
                    download.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, AVException e) {
                        // bytes 就是文件的数据流
                        if(e == null && bytes.length != 0){
                            Log.d("RichEditor",bytes.toString());
                            Bitmap picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            BitmapHelper.output(picture,object.get("name").toString());
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {

                        // 下载进度数据，integer 介于 0 和 100。
                    }
                });
                }
            }
        });
        return true;
    }

    public static boolean updateCloud(){

        AVObject avObject;
        Cursor cursor = Database.query();

        ArrayList<Intent> articles = new ArrayList<>();
        Intent article;
        //删除所有笔记
        AVQuery<AVObject> query = new AVQuery<>("Notes");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                List<AVObject> priorityEqualsZeroTodos = list;// 符合 priority = 0 的 Todo 数组
                for(AVObject object :  priorityEqualsZeroTodos){
                    object.deleteInBackground();
                }
            }
        });

        query = new AVQuery<>("_File");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                List<AVObject> priorityEqualsZeroTodos = list;// 符合 priority = 0 的 Todo 数组
                for(AVObject object :  priorityEqualsZeroTodos){
                    object.deleteInBackground();
                    Log.d("holo",object.get("url").toString());
                }
            }
        });


        //重新上传笔记
        if(cursor.moveToFirst()){
            do{
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                if(status == 0){
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    article = new Intent();
                    article.putExtra("title",title);
                    article.putExtra("content",content);
                    article.putExtra("status",1);
                    article.putExtra("id",Database.queryId(title));
                    Database.update(article);
                    articles.add(article);
                    avObject = new AVObject("Notes");
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

        //上传图片
        final StringBuilder ids = new StringBuilder();
        for(Intent intent : articles){
            Intent result = Decoder.Decode(intent);
            ArrayList<String> pics = result.getStringArrayListExtra("pictureNames");
            for(String pictureName : pics){
                try{
                    String pictureAddress = Environment. getExternalStorageDirectory() + "/notebookdata/" + pictureName;
                    avFile= AVFile.withAbsoluteLocalPath(pictureName,pictureAddress);
                }catch (Exception e){
                    e.printStackTrace();
                }

                avFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            ids.append(avFile.getObjectId());
                        }
                    }
                });
            }
        }
        Log.d("holo",ids.toString());

        return true;
    }
}
