package com.example.lenovo.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.global.NotebookApp;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/7.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView content;
        TextView title;

        public ViewHolder(View v){
            super(v);
            content = (TextView)v.findViewById(R.id.article_title);
            title = (TextView)v.findViewById(R.id.article_content);
            v.setOnClickListener(this);
//            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
                            TextView articleTitle = (TextView) view.findViewById(R.id.article_title);
                TextView articleContent = (TextView) view.findViewById(R.id.article_content);
                Intent intent = new Intent(NotebookApp.getInstance().getApplicationContext(), ReadArticleActivity.class);
                intent.putExtra("title", articleTitle.getText().toString());
                intent.putExtra("content", articleContent.getText().toString());
            Log.d("holo","from click");
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            NotebookApp.getInstance().getApplicationContext().startActivity(intent);
            context.overridePendingTransition(R.anim.anim_in,R.anim.anim_out);

        }

//        @Override
//        public boolean onLongClick(View v) {
//
//                AlertDialog.Builder ab = new AlertDialog.Builder(context);
//                ab.setMessage("是否删除笔记？");
//                ab.setCancelable(true);
//                final View view = v;
//                ab.setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//            ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });


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




    }

    private List<Article> articleList = new ArrayList<Article>();
    private AppCompatActivity context;

    public ArticleAdapter(List list,AppCompatActivity context){
        this.articleList = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(articleList.get(position).getTitle());
        holder.title.setText(articleList.get(position).getContent());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article,parent,false);
        ArticleAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}
