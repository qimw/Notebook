package com.example.lenovo.notebook;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collection;
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
        adapter = new ArticleAdapter(articleList,ReadActivity.this);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space_of_item);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Intent intent = new Intent();
                intent.putExtra("title",(articleList.get(position)).getTitle());
                Database.remove(intent);
                articleList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if(articleList.size() == 0){
            note_of_no_title.setText("还没有写笔记哦");
            note_of_no_title.setVisibility(View.VISIBLE);
        }
    }
    public void initArticles(){
        Cursor cursor = Database.query();
        if(cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                Article article = new Article(title,content,status);
                articleList.add(article);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    protected  void onRestart(){
        super.onRestart();
        articleList.removeAll(articleList);
        adapter.notifyDataSetChanged();
        initArticles();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space_of_item);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Intent intent = new Intent();
                intent.putExtra("title",(articleList.get(position)).getTitle());
                Database.remove(intent);
                articleList.remove(position);
                adapter.notifyItemRemoved(position);

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        if(articleList.size() == 0){
            note_of_no_title.setText("还没有写笔记哦");
            note_of_no_title.setVisibility(View.VISIBLE);
        }
    }
}
