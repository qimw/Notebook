package com.example.lenovo.notebook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.global.NotebookApp;

/**
 * Created by lenovo on 2016/5/12.
 */
public class RegistActivity extends BaseActivity {
    private Button reg;
    private Button quit;
    private EditText password;
    private EditText password_confirm;

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        reg = (Button) findViewById(R.id.reg);
        quit = (Button)findViewById(R.id.quit);
        password = (EditText)findViewById(R.id.password);
        password_confirm = (EditText)findViewById(R.id.password_confirm);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                String pass_con = password_confirm.getText().toString();
                if(pass.equals("")){
                    Toast.makeText(RegistActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                }else if(pass_con.equals("")){
                    Toast.makeText(RegistActivity.this,"请输入确认密码",Toast.LENGTH_SHORT).show();
                }else if(!pass.equals(pass_con)){
                    Toast.makeText(RegistActivity.this,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor = getSharedPreferences("password",MODE_PRIVATE).edit();
                    editor.putString("pass",pass);
                    editor.putBoolean("isCreated",true);
                    editor.commit();
                    finish();
                }
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.finishAll();
            }
        });
    }

    @Override
    public void onBackPressed() {
        BaseActivity.finishAll();
    }
}
