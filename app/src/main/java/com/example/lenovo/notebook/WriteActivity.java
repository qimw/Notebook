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
    private Intent start;
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

        start = getIntent();

        if (!start.getBooleanExtra("isNew", true)) {
            decodeContent(start);
            BitmapHelper.delete(start);
        } else {
                editor.createFirstEditTest();
        }
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    protected boolean dealEditData(List<RichTextEditor.EditData> editList) {

        StringBuilder builder = new StringBuilder();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        String title;
        String content;
        int count = 1;
        //取出标题
        EditText editText = (EditText)findViewById(R.id.title);
        title = editText.getText().toString();
        //取出其余项
        for (RichTextEditor.EditData itemData : editList) {

            if (itemData.inputStr != null) {
                builder.append(itemData.inputStr);
                Log.d("RichEditor", "commit inputStr=" + itemData.inputStr);
            }else if(itemData.bitmap != null){
                //content中添加标记
                builder.append("|pic" + count + "|");
                //将图片输出到本地
                bitmaps.add(itemData.bitmap);
                count++;
            }
        }
        content = builder.toString();
        Intent intent = new Intent();
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        intent.putExtra("status",0);

        BitmapHelper.output(bitmaps,title);
        //保存时的逻辑处理
        if(title.equals("") || content.equals("")){
            Toast.makeText(WriteActivity.this,"标题或内容不能为空",Toast.LENGTH_SHORT).show();
        }else if(start.getBooleanExtra("isNew",false) == true){
            Toast toast = Toast.makeText(WriteActivity.this,"保存成功",Toast.LENGTH_SHORT);
            switch(Database.add(intent)){
                case Database.FAILED_TO_SAVE:
                    toast.setText("保存失败");
                    toast.show();
                    break;
                    //return false;
                case Database.SAVE_SUCCEED:
                    toast.show();
                    finish();
                    break;
                    //return true;
                case Database.TITLE_EXIST:
                    toast.setText("标题已存在");
                    toast.show();
                    break;
                    //return false;
            }
        }else{
            if(title.equals(start.getStringExtra("title")) || (Database.queryId(title) == -1)){
                intent.putExtra("id",start.getIntExtra("id",-1));
                Database.update(intent);
                finish();
            }else{
                    Toast.makeText(WriteActivity.this,"标题已存在",Toast.LENGTH_SHORT).show();
            }
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
                        ||(contentCode.charAt(i) == '|' && contentCode.substring(i,(i+4)>contentCode.length()? contentCode.length():(i+4)).equals("|pic"))){
                    if((i+4)>contentCode.length()){
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
                    //检测图片编号是一位数还是两位数
                    String tempImageNumber = null;
                    if(contentCode.charAt(i + 5) == '|'){
                        tempImageNumber = contentCode.substring(i + 4,i + 5);
                        former = i + 6;
                        i += 5;//因为循环中会自增
                    }else if(contentCode.charAt(i + 6) == '|'){
                        tempImageNumber = contentCode.substring(i + 4,i + 6);
                        former = i + 7;
                        i += 6;//因为循环中会自增
                    }
                    int imageNumber;
                    imageNumber = new Integer(tempImageNumber);
                    String address = Environment. getExternalStorageDirectory()+"/notebookdata/" + title + imageNumber + ".jpg";
                    Log.d("holo",address);
                    insertBitmap(address);
                }
            }
    }
    @Override
    public void onBackPressed() {
        if(start.getBooleanExtra("isNew",false) == false){
            Toast.makeText(WriteActivity.this,"未保存的笔记将会丢失，请保存后退出",Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }

    }

}



























//public class WriteActivity extends BaseActivity {
//    private Button save;
//    private Button quit;
//    private Button clean;
//    private EditText title;
//    private EditText content;
//    private Boolean isNew;
//    private String oldTitle;
//    private int id=-1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.write);
//        save = (Button)findViewById(R.id.save);
//        clean = (Button)findViewById(R.id.clean);
//        title = (EditText)findViewById(R.id.title);
//        content = (EditText)findViewById(R.id.content);
//        isNew = getIntent().getBooleanExtra("isNew",false);
//        if(!isNew){
//            oldTitle = getIntent().getStringExtra("title");
//            title.setText(oldTitle);
//            content.setText(getIntent().getStringExtra("content"));
//            id = getIntent().getIntExtra("id",-1);
//        }
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String article_title = title.getText().toString();
//                String article_content = content.getText().toString();
//
//                if(!(article_content.equals("")||article_title.equals(""))){
//                    Intent intent = new Intent();
//                    intent.putExtra("title",article_title);
//                    intent.putExtra("content", article_content);
//                    intent.putExtra("status",0);
//                    if(id ==-1){
//                        int result = Database.add(intent);
//                        if(result == Database.SAVE_SUCCEED){
//                            Toast.makeText(WriteActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
//                            finish();
//                        }else if(result == Database.TITLE_EXIST){
//                            Toast.makeText(WriteActivity.this,"标题已存在",Toast.LENGTH_SHORT).show();
//                        }else if(result == Database.FAILED_TO_SAVE){
//                            Toast.makeText(WriteActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
//                        }
//
//                    }else{
//                        intent.putExtra("id",id);
//                        Database.update(intent);
//                        finish();
//                    }
//                }else{
//                    Toast.makeText(WriteActivity.this,"未输入标题或内容",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        clean.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                title.setText("");
//                content.setText("");
//            }
//        });
//
//    }
//}
