package com.example.lenovo.notebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lenovo.notebook.global.NotebookApp;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/7.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        TextView title;

        public ViewHolder(View v){
            super(v);
            content = (TextView)v.findViewById(R.id.article_title);
            title = (TextView)v.findViewById(R.id.article_content);
        }
    }

    private List<Article> articleList = new ArrayList<Article>();

    public ArticleAdapter(List list){
        this.articleList = list;
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
