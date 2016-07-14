package com.example.lenovo.notebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.lenovo.notebook.Base.BaseActivity;
import com.example.lenovo.notebook.Db.Database;
import com.example.lenovo.notebook.R;
import com.example.lenovo.notebook.RichEditor.Decoder;
import com.example.lenovo.notebook.RichEditor.RichTextEditor;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/5/9.
 */
public class ReadArticleActivity extends BaseActivity{
    private Button edit_article;
    private ScrollView scrollView;


    @Override
    protected  void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_article);
        edit_article = (Button)findViewById(R.id.edit_article);
        scrollView = (ScrollView) findViewById(R.id.article);
        Intent intent = getIntent();
         final String title = intent.getStringExtra("title");
         final String content = intent.getStringExtra("content");
//        title = (TextView)findViewById(R.id.read_title);
//        content = (TextView)findViewById(R.id.read_content);
//        title.setText(intent.getStringExtra("title"));
//        content.setText(intent.getStringExtra("content"));
        decodeContent(intent);
        edit_article.bringToFront();
        edit_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadArticleActivity.this,WriteActivity.class);
                intent.putExtra("isNew",false);
                intent.putExtra("id", Database.queryId(title));
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
    }


    private void decodeContent(Intent intent){

        int top,bottom,right,left;
        top = bottom = 0;
        right = left = 10;

//        Intent result = Decoder.DecodeForRead(intent);
//        ArrayList<String> article = result.getStringArrayListExtra("article");
//        ArrayList<String> pictureAddress = result.getStringArrayListExtra("pictureAddress");


        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        linearLayoutParams.setMargins(left,top,top,bottom);
        int id = 1;
        //添加标题
        String title = intent.getStringExtra("title");
        TextView article_title = new TextView(this);
        article_title.setId(id);
        article_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        article_title.setText(title);
        relativeLayout.addView(article_title);
        id++;
        //添加内容
        String contentCode = intent.getStringExtra("content");
        int former = 0;
        for(int i = 0 ; i < contentCode.length() ; i++){

            if((i == contentCode.length()-1)
                    ||(contentCode.charAt(i) == '|' && contentCode.substring(i,(i+4)>contentCode.length()? contentCode.length():(i+4)).equals("|pic"))){
                if((i+4)>contentCode.length()){
                    i = contentCode.length();
                }
                //设置前面的文字
                if(former < i){
                    String tempContent = contentCode.substring(former,i);
                    TextView content = new TextView(this);
                    content.setId(id);
                    content.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                    content.setText(tempContent);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW,id-1);
                    content.setLayoutParams(params);
                    relativeLayout.addView(content);
                    id++;
                    if(i == contentCode.length()){
                        break;
                    }
                }

                String tempImageNumber = null;
                //检测图片编号是一位数还是两位数
                if(contentCode.charAt(i + 5) == '|'){
                    tempImageNumber = contentCode.substring(i + 4,i + 5);
                    former = i + 6;
                    i += 5;//因为循环中会自增
                }else if(contentCode.charAt(i + 6) == '|'){
                    tempImageNumber = contentCode.substring(i + 4,i + 6);
                    former = i + 7;
                    i += 6;//因为循环中会自增
                }
                int imageNumber;
                imageNumber = new Integer(tempImageNumber);
                String location = Environment. getExternalStorageDirectory()+"/notebookdata/" + title + imageNumber + ".jpg";
                Bitmap tempBitmap = RichTextEditor.getScaledBitmap(location,relativeLayoutParams.width-10);
                ImageView image = new ImageView(this);
                image.setImageBitmap(tempBitmap);
                image.setId(id);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,0,0);
                params.addRule(RelativeLayout.BELOW,id-1);
                image.setLayoutParams(params);
                relativeLayout.addView(image);
                id++;
            }
        }
        scrollView.addView(relativeLayout);
    }

}
