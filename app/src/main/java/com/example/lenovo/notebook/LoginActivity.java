package com.example.lenovo.notebook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.global.NotebookApp;

/**
 * Created by lenovo on 2016/5/6.
 */
public class LoginActivity extends BaseActivity {
    private EditText password_input;
    private Button login;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = (Button)findViewById(R.id.login);
        password_input = (EditText)findViewById(R.id.password_input);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = password_input.getText().toString();
                SharedPreferences sp = getSharedPreferences("password",MODE_PRIVATE);
                String password = sp.getString("pass",null);
                if(input.equals(password)){
                    finish();
                }else{

                    Toast.makeText(LoginActivity.this,"密码输入有误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        BaseActivity.finishAll();
    }
}
