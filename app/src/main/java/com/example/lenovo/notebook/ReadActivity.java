package com.example.lenovo.notebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/6.
 */
public class ReadActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private List<Article> articleList = new ArrayList<Article>();
    private  TextView note_of_no_title;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);
        note_of_no_title = (TextView)findViewById(R.id.note_of_no_article);
        initArticles();


        recyclerView = (RecyclerView)findViewById(R.id.article_recycle_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ArticleAdapter(articleList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



//        final ArticleAdapter adapter = new ArticleAdapter(ReadActivity.this,R.layout.article,articleList);
//        final ListView listView = (ListView)findViewById(R.id.article_list);
//        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView articleTitle = (TextView) view.findViewById(R.id.article_title);
//                TextView articleContent = (TextView) view.findViewById(R.id.article_content);
//                Intent intent = new Intent(ReadActivity.this, ReadArticleActivity.class);
//                intent.putExtra("title", articleTitle.getText().toString());
//                intent.putExtra("content", articleContent.getText().toString());
//                finish();
//                startActivity(intent);
//            }
//        });
//
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
//                AlertDialog.Builder ab = new AlertDialog.Builder(ReadActivity.this);
//                ab.setMessage("是否删除笔记？");
//                ab.setCancelable(true);
//                final Article article = (Article)listView.getItemAtPosition(position);
//                ab.setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d("holo", "5555555555555555555" + new Integer(which).toString()+"   "+new Integer(position).toString());
//                        Intent intent = new Intent();
//                        intent.putExtra("title", article.getTitle());
//                        Database.remove(intent);
//                        articleList.remove(article);
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//                ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                ab.show();
//                return true;
//            }
//        });
        if(articleList.size() == 0){
            note_of_no_title.setText("还没有写笔记哦");
        }
    }
    public void initArticles(){
        Cursor cursor = Database.query();
        if(cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Article article = new Article(title,content);
                articleList.add(article);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
//    @Override
//    protected  void onRestart(){
//        super.onRestart();
//        initArticles();
//        final ArticleAdapter adapter = new ArticleAdapter(ReadActivity.this,R.layout.article,articleList);
//        final ListView listView = (ListView)findViewById(R.id.article_list);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView articleTitle = (TextView) view.findViewById(R.id.article_title);
//                TextView articleContent = (TextView) view.findViewById(R.id.article_content);
//                Intent intent = new Intent(ReadActivity.this, ReadArticleActivity.class);
//                intent.putExtra("title", articleTitle.getText().toString());
//                intent.putExtra("content", articleContent.getText().toString());
//                finish();
//                startActivity(intent);
//            }
//        });
//
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
//                AlertDialog.Builder ab = new AlertDialog.Builder(ReadActivity.this);
//                ab.setMessage("是否删除笔记？");
//                ab.setCancelable(true);
//                final Article article = (Article)listView.getItemAtPosition(position);
//                ab.setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d("holo", "5555555555555555555" + new Integer(which).toString()+"   "+new Integer(position).toString());
//                        Intent intent = new Intent();
//                        intent.putExtra("title", article.getTitle());
//                        Database.remove(intent);
//                        articleList.remove(article);
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//                ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                ab.show();
//                return true;
//            }
//        });
//    }
}
