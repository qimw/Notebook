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
                    ||(contentCode.charAt(i) == '|' && contentCode.substring(i,(i+4)>contentCode.length()? contentCode.length():(i+4)).equals("|pic"))){
                if((i+4)>contentCode.length()){
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
                String address = title + imageNumber + ".jpg";
                pictureNames.add(address);
            }
        }
        intent.putExtra("article",article);
        intent.putExtra("pictureNames",pictureNames);
        return intent;
    }
}
