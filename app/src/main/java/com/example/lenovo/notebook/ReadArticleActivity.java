package com.example.lenovo.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.R;

import org.w3c.dom.Text;

/**
 * Created by lenovo on 2016/5/9.
 */
public class ReadArticleActivity extends BaseActivity{
    private TextView title;
    private TextView content;
    private Button edit_article;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_article);
        edit_article = (Button)findViewById(R.id.edit_article);
        Intent intent = getIntent();
        title = (TextView)findViewById(R.id.read_title);
        content = (TextView)findViewById(R.id.read_content);
        title.setText(intent.getStringExtra("title"));
        content.setText(intent.getStringExtra("content"));
        edit_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadArticleActivity.this,WriteActivity.class);
                intent.putExtra("isNew",false);
                intent.putExtra("id", Database.queryId(title.getText().toString()));
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("content",content.getText().toString());
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
    }
    @Override
    protected void onRestart(){
        super.onRestart();

    }
}
