package com.example.lenovo.notebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.Db.DatabaseHelper;
import com.example.lenovo.notebook.global.NotebookApp;

import org.w3c.dom.Text;

/**
 * Created by lenovo on 2016/5/7.
 */
public class WriteActivity extends BaseActivity {
    private Button save;
    private Button quit;
    private Button clean;
    private EditText title;
    private EditText content;
    private Boolean isNew;
    private String oldTitle;
    private int id=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);
        save = (Button)findViewById(R.id.save);
        clean = (Button)findViewById(R.id.clean);
        title = (EditText)findViewById(R.id.title);
        content = (EditText)findViewById(R.id.content);
        isNew = getIntent().getBooleanExtra("isNew",false);
        if(!isNew){
            oldTitle = getIntent().getStringExtra("title");
            title.setText(oldTitle);
            content.setText(getIntent().getStringExtra("content"));
            id = getIntent().getIntExtra("id",-1);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String article_title = title.getText().toString();
                String article_content = content.getText().toString();

                if(!(article_content.equals("")||article_title.equals(""))){
                    Intent intent = new Intent();
                    intent.putExtra("title",article_title);
                    intent.putExtra("content", article_content);
                    intent.putExtra("status",0);
                    if(id ==-1){
                        int result = Database.add(intent);
                        if(result == Database.SAVE_SUCCEED){
                            Toast.makeText(WriteActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(result == Database.TITLE_EXIST){
                            Toast.makeText(WriteActivity.this,"标题已存在",Toast.LENGTH_SHORT).show();
                        }else if(result == Database.FAILED_TO_SAVE){
                            Toast.makeText(WriteActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        intent.putExtra("id",id);
                        Database.update(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(WriteActivity.this,"未输入标题或内容",Toast.LENGTH_LONG).show();
                }

            }
        });

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                content.setText("");
            }
        });

    }
}
