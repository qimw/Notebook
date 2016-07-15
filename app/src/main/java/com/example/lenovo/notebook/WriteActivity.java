package com.example.lenovo.notebook;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.BitmapHelper.BitmapHelper;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.Db.DatabaseHelper;
import com.example.lenovo.notebook.RichEditor.Decoder;
import com.example.lenovo.notebook.RichEditor.RichTextEditor;
import com.example.lenovo.notebook.global.NotebookApp;


import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2016/5/7.
 */

public class WriteActivity extends BaseActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_PICK_IMAGE = 1023;
    private static final int REQUEST_CODE_CAPTURE_CAMERA = 1022;
    private RichTextEditor editor;
    private View btn1, btn2, btn3;
    private Intent articleIntent;
    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);
        getSupportActionBar().hide();

        editor = (RichTextEditor) findViewById(R.id.richEditor);

        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        articleIntent = getIntent();

        if (!articleIntent.getBooleanExtra("isNew", true)) {
            decodeContent(articleIntent);
            BitmapHelper.delete(articleIntent);
        } else {
                editor.createFirstEditTest();
        }
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    protected boolean dealEditData(List<RichTextEditor.EditData> editList) {
        StringBuilder builder = new StringBuilder();
        String title;
        String content;
        //取出标题
        EditText editText = (EditText)findViewById(R.id.title);
        title = editText.getText().toString();
        //取出其余项
        for (RichTextEditor.EditData itemData : editList) {

            if (itemData.inputStr != null) {
                builder.append(itemData.inputStr);
            }else if(itemData.bitmap != null){
                //content中添加标记
                long id = Calendar.getInstance().getTimeInMillis();
                builder.append("|" + id + "|");
                Log.d("holo","from write activity" + builder.toString() + " " + id);
                //将图片输出到本地
                BitmapHelper.output(itemData.bitmap,id + ".jpg");
            }
        }
        content = builder.toString();
        Intent intent = new Intent();
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        intent.putExtra("status",0);


        //保存时的逻辑处理
        if(title.equals("") || content.equals("")){
            Toast.makeText(WriteActivity.this,"标题或内容不能为空",Toast.LENGTH_SHORT).show();
        }else if(articleIntent.getBooleanExtra("isNew",false) == true) {
            intent.putExtra("id",Calendar.getInstance().getTimeInMillis());
            Database.add(intent);
            Toast.makeText(WriteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            intent.putExtra("id",articleIntent.getLongExtra("id",0));
            Log.d("holo","from write activity id=" + articleIntent.getLongExtra("id",0));
            Database.update(intent);
            finish();
        }
        return false;
    }

    protected void openCamera() {
        try {
            // Launch camera to take photo for selected contact
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
        } catch (ActivityNotFoundException e) {
        }
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    /**
     * 用当前时间给取得的图片命名
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri uri = data.getData();
            insertBitmap(getRealFilePath(uri));
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
            insertBitmap(mCurrentPhotoFile.getAbsolutePath());
        }
    }

    /**
     * 添加图片到富文本剪辑器
     *
     * @param imagePath
     */
    private void insertBitmap(String imagePath) {
        editor.insertImage(imagePath,WriteActivity.this);
    }

    /**
     * 根据Uri获取图片文件的绝对路径
     */
    public String getRealFilePath(final Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    @Override
    public void onClick(View v) {
        editor.hideKeyBoard();
        if (v.getId() == btn1.getId()) {
            // 打开系统相册
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");// 相片类型
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        } else if (v.getId() == btn2.getId()) {
            // 打开相机
            openCamera();
        } else if (v.getId() == btn3.getId()) {
            List<RichTextEditor.EditData> editList = editor.buildEditData();
            // 下面的代码可以上传、或者保存，请自行实现
            dealEditData(editList);
        }
    }

    private void decodeContent(Intent intent){
            //添加标题
            String title = intent.getStringExtra("title");
            ((EditText)findViewById(R.id.title)).setText(title);
            //添加内容
            String contentCode = intent.getStringExtra("content");
            int former = 0;
            //editor.createEditText("");
            for(int i = 0 ; i < contentCode.length() ; i++){
                if((i == contentCode.length()-1)
                        ||(contentCode.charAt(i) == '|' &&(i + 14)<contentCode.length()&&(contentCode.charAt(i+14)) == '|')){
                    if((i+14)>contentCode.length()){
                        i = contentCode.length();
                    }
                    //设置前面的文字
                    if(former < i){
                        String tempContent = contentCode.substring(former,i);
                        editor.createEditText(tempContent);
                        if(i == contentCode.length()){
                            break;
                        }
                    }
                    String number = contentCode.substring(i + 1,i + 14);
                    former = i;
                    former += 15;
                    i += 14;
                    Log.d("holo","former:" + former + "i:" + i);
                    String address = Environment. getExternalStorageDirectory()+"/notebookdata/" + number + ".jpg";
                    Log.d("holo",address);
                    insertBitmap(address);
                }
            }
    }
    @Override
    public void onBackPressed() {
        if(articleIntent.getBooleanExtra("isNew",false) == false){
            Toast.makeText(WriteActivity.this,"未保存的笔记将会丢失，请保存后退出",Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }

    }

}
