package com.example.lenovo.notebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.LeanCloud.LeanCloud;
import com.example.lenovo.notebook.global.NotebookApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lenovo on 2016/7/10.
 */
public class Cloud extends BaseActivity {

    Button updateCloud;
    Button updateLocal;
    TextView noInternet;
    RecyclerView articleInCloud;
    Button getArticleListInCloud;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud);

        updateCloud = (Button)findViewById(R.id.updateCloud);
        updateLocal = (Button)findViewById(R.id.updateLocal);
        getArticleListInCloud = (Button)findViewById(R.id.get_cloud_article_list);
       // noInternet = (TextView)findViewById(R.id.no_internet);
       // articleInCloud = (RecyclerView)findViewById(R.id.article_in_cloud);



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

        getArticleListInCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpenNetwork()){
                    final ArrayList<Article> articles = new ArrayList<>();
                    AVQuery<AVObject> query = new AVQuery<>("Notes");
                    query.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            for(int i = 0;i < list.size();i++){
                                AVObject avObject = list.get(i);
                                String title = avObject.getString("title");
                                String content = avObject.getString("content");
                                long id = avObject.getLong("id");
                                Article article = new Article(title,content,0,id);
                                articles.add(article);
                            }
                            articleInCloud = (RecyclerView)findViewById(R.id.article_in_cloud);
                            final CloudArticleAdapter cloudArticleAdapter = new CloudArticleAdapter(articles);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Cloud.this);
                            articleInCloud.setLayoutManager(layoutManager);
                            articleInCloud.setAdapter(cloudArticleAdapter);
                            articleInCloud.setHasFixedSize(true);
                            ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
                                @Override
                                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                    return false;
                                }
                                @Override
                                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                    final int position = viewHolder.getAdapterPosition();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Cloud.this);
                                    builder.setCancelable(true);
                                    builder.setTitle("是否要从云端删除这条笔记？");
                                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            articleInCloud.setAdapter(cloudArticleAdapter);
                                        }
                                    });
                                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.putExtra("title",(articles.get(position)).getTitle());
                                            LeanCloud.remove(articles.get(position));
                                            articles.remove(position);
                                            cloudArticleAdapter.notifyItemRemoved(position);
                                        }
                                    });
                                    builder.create().show();
                                }
                            };
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
                            itemTouchHelper.attachToRecyclerView(articleInCloud);
                            articleInCloud.setVisibility(View.VISIBLE);
                            articleInCloud.setAdapter(cloudArticleAdapter);
                        }
                    });
                    Log.d("holo","4444");
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
class CloudArticleAdapter extends RecyclerView.Adapter<CloudArticleAdapter.ViewHolder>{

    class  ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView content;
        TextView id;
        public ViewHolder(View v){
            super(v);
            this.title = (TextView)v.findViewById(R.id.article_title);
            this.content = (TextView)v.findViewById(R.id.article_content);
            this.id = (TextView)v.findViewById(R.id.article_id);
        }
    }
    private List<Article> articleList = new ArrayList<Article>();
    public CloudArticleAdapter(ArrayList<Article> list){
        this.articleList = list;
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
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article,parent,false);
        CloudArticleAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}
