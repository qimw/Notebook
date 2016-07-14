package com.example.lenovo.notebook.BitmapHelper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;

import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.RichEditor.Decoder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/7/13.
 */
public class BitmapHelper {

    public static void output(ArrayList<Bitmap> list,String title){
        int i = 1;
        for(Bitmap bitmap:list){
            String name = title + i++ + ".jpg";
            BitmapHelper.output(bitmap,name);
        }
    }

    public static void output(Bitmap bitmap,String name){

            FileOutputStream out = null;
            BufferedOutputStream bufferedOutputStream;
            try{
                File outputImage = new File(Environment. getExternalStorageDirectory()+"/notebookdata",name);
                outputImage.createNewFile();
                out = new FileOutputStream(outputImage);
                bufferedOutputStream = new BufferedOutputStream(out);
                if(bitmap.getByteCount() < 1024 * 1024){
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG,100,bufferedOutputStream);
                }else{
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG,70,bufferedOutputStream);
                }
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    public static void delete(Intent intent){
        Intent result = Decoder.Decode(intent);
        ArrayList<String> pictureNames = result.getStringArrayListExtra("pictureNames");
        for(String name :pictureNames){
            File file = new File(Environment.getExternalStorageDirectory() + "/notebookdata/" + name);
            file.delete();
        }
    }

    public static void deleteAll(){
        Cursor cursor = Database.query();
        if(cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Intent intent = new Intent();
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                BitmapHelper.delete(intent);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
}
