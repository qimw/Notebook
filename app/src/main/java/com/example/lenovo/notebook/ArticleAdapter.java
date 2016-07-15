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
import com.example.lenovo.notebook.RichEditor.Decoder;
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
        TextView status;
        TextView id;
        public ViewHolder(View v){
            super(v);
            content = (TextView)v.findViewById(R.id.article_content);
            title = (TextView)v.findViewById(R.id.article_title);
            status = (TextView)v.findViewById(R.id.status);
            id = (TextView)v.findViewById(R.id.article_id);
            v.setOnClickListener(this);
//            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            TextView articleTitle = (TextView) view.findViewById(R.id.article_title);
            TextView articleContent = (TextView) view.findViewById(R.id.article_content);
            TextView articleId = (TextView) view.findViewById(R.id.article_id);
            Intent intent = new Intent(NotebookApp.getInstance().getApplicationContext(), ReadArticleActivity.class);
            intent.putExtra("title", articleTitle.getText().toString());
            intent.putExtra("content", articleContent.getText().toString());
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id",new Long(articleId.getText().toString()));
            Log.d("holo","from article adapter id=" + articleId.getText().toString());
            intent.putExtra("isNew",false);
            NotebookApp.getInstance().getApplicationContext().startActivity(intent);
            context.overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
        }
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
        holder.content.setText(articleList.get(position).getContent());
        holder.title.setText(articleList.get(position).getTitle());
        holder.id.setText(articleList.get(position).getId() + "");
        int status = articleList.get(position).getStatus();
        if(status == 0){
            holder.status.setText("未同步");
        }else if(status == 1){
            holder.status.setText("已同步");
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article,parent,false);
        ArticleAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}
