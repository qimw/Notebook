package com.example.lenovo.notebook.RichEditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/7/12.
 */
public class Decoder  {
    public static Intent Decode(Intent intent){

        String title = intent.getStringExtra("title");
        String contentCode = intent.getStringExtra("content");
        int former = 0;
        ArrayList<String> article = new ArrayList<>();
        ArrayList<String> pictureNames = new ArrayList<>();
        article.add(title);

        for(int i = 0 ; i < contentCode.length() ; i++){

            if((i == contentCode.length()-1)
                    ||(contentCode.charAt(i) == '|' &&(i + 14)<contentCode.length()&&(contentCode.charAt(i+14)) == '|')){
                if((i+14)>contentCode.length()){
                    i = contentCode.length();
                }
                //设置前面的文字
                if(former < i){
                    String tempContent = contentCode.substring(former,i);
                    article.add(tempContent);
                    if(i == contentCode.length()){
                        break;
                    }
                }
                String number = contentCode.substring(i + 1,i + 14);
                former += 15;
                i += 14;
                String address = number + ".jpg";
                pictureNames.add(address);
            }
        }
        intent.putExtra("article",article);
        intent.putExtra("pictureNames",pictureNames);
        return intent;
    }
}
