package com.example.lenovo.notebook;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.LeanCloud.LeanCloud;
import com.example.lenovo.notebook.global.NotebookApp;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/7/10.
 */
public class Cloud extends BaseActivity {

    Button updateCloud;
    Button updateLocal;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud);

        updateCloud = (Button)findViewById(R.id.updateCloud);
        updateLocal = (Button)findViewById(R.id.updateLocal);

        updateCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOpenNetwork()){
                    LeanCloud.updateCloud();
                }else{
                    Toast.makeText(Cloud.this,"网络未连接，请检查网络设置。",Toast.LENGTH_SHORT).show();
                }


            }
        });
        updateLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpenNetwork()){
                    LeanCloud.updateLocal();
                }else{
                    Toast.makeText(Cloud.this,"网络未连接，请检查网络设置。",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
