package com.example.lenovo.notebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import com.example.lenovo.notebook.RichEditor.DataImageView;
import com.example.lenovo.notebook.RichEditor.Decoder;
import com.example.lenovo.notebook.RichEditor.RichTextEditor;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

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
        final Intent article = getIntent();
         final String title = article.getStringExtra("title");
         final String content = article.getStringExtra("content");

        decodeContent(article);
        edit_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadArticleActivity.this,WriteActivity.class);
                intent.putExtra("isNew",false);
                intent.putExtra("id", article.getLongExtra("id",0));
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
    }


    private void decodeContent(Intent intent){
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int id = 1;
        //添加标题
        String title = intent.getStringExtra("title");
        TextView article_title = new TextView(this);
        article_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        article_title.setText(title);
        linearLayout.addView(article_title);
        id++;
        //添加内容
        String contentCode = intent.getStringExtra("content");
        int former = 0;

        for(int i = 0 ; i < contentCode.length() ; i++){
            if((i == contentCode.length()-1)
                    ||(contentCode.charAt(i) == '|' &&(i + 14)<contentCode.length()&&(contentCode.charAt(i+14)) == '|')){
                if((i+14)>contentCode.length()){
                    i = contentCode.length();
                }
                //设置前面的文字
                if(former < i){
                    String tempContent = contentCode.substring(former,i);
                    Log.d("holo","former:" + former + "i:" + i);
                    TextView content = new TextView(this);

                    content.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                    Spanned spanned = RichTextEditor.trimTrailingWhitespace((SpannableStringBuilder) Html.fromHtml(tempContent));
                    content.setText(spanned);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW,id-1);
                    content.setLayoutParams(params);
                    linearLayout.addView(content);
                    id++;
                    if(i == contentCode.length()){
                        break;
                    }
                }
                String number = contentCode.substring(i + 1,i + 14);
                Log.d("holo","from decode in read id=" + number);
                former = i;
                former += 15;
                i += 14;
                String location = Environment. getExternalStorageDirectory()+"/notebookdata/" + number + ".jpg";
                Bitmap tempBitmap = RichTextEditor.getScaledBitmap(location,
                        ReadArticleActivity.this.getWindowManager().getDefaultDisplay().getWidth());
                ImageView image = new ImageView(this);
                image.setImageBitmap(tempBitmap);

                int imageHeight = ReadArticleActivity.this.getWindowManager().getDefaultDisplay().getWidth() * tempBitmap.getHeight() / tempBitmap.getWidth();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        imageHeight);
                image.setLayoutParams(params);
                linearLayout.addView(image);
                id++;
            }
        }
        scrollView.addView(linearLayout);
    }

}
