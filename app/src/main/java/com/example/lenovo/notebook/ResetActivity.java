package com.example.lenovo.notebook;

import android.content.SharedPreferences;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.notebook.Base.BaseActivity;

/**
 * Created by lenovo on 2016/5/13.
 */
public class ResetActivity extends BaseActivity {
    EditText old_password;
    EditText new_password;
    EditText con_password;
    Button reset;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset);
        old_password = (EditText)findViewById(R.id.old_password);
        new_password = (EditText)findViewById(R.id.new_password);
        con_password = (EditText)findViewById(R.id.con_password);
        reset = (Button)findViewById(R.id.reset_password);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("password",MODE_PRIVATE);
                String password = sp.getString("pass",null);
                if(old_password.getText().toString().equals("")){
                    Toast.makeText(ResetActivity.this,"还没有输入旧密码哦",Toast.LENGTH_SHORT).show();
                }else if(new_password.getText().toString().equals("")){
                    Toast.makeText(ResetActivity.this,"还没有输入新密码哦",Toast.LENGTH_SHORT).show();
                }else if(con_password.getText().toString().equals("")){
                    Toast.makeText(ResetActivity.this,"还没有输入确认密码哦",Toast.LENGTH_SHORT).show();
                }else if(!password.equals(old_password.getText().toString())){
                    Toast.makeText(ResetActivity.this,"旧密码不正确",Toast.LENGTH_SHORT).show();
                }else if(!con_password.getText().toString().equals(new_password.getText().toString())){
                    Toast.makeText(ResetActivity.this,"两次输入的新密码不一致哦",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor spe = getSharedPreferences("password", MODE_PRIVATE).edit();
                    spe.putString("pass",new_password.getText().toString());
                    spe.commit();
                    Toast.makeText(ResetActivity.this,"密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
