package com.example.lenovo.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.lenovo.notebook.Base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/14.
 */
public class IntroduceActivity extends BaseActivity {
    private Button in;
    private ViewPager viewPager;
    private PagerTitleStrip pagerTitleStrip; // 表示滑动的每一页的标题
    private List<View> list; // 表示装载滑动的布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce);
        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        View view1 = LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.tab1, null);
        View view2 = LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.tab2, null);
        View view3 = LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.tab3, null);

        list = new ArrayList<View>();
        list.add(view1);
        list.add(view2);
        list.add(view3);

        viewPager.setAdapter(new MyAdapter());
        in = (Button)findViewById(R.id.in);
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroduceActivity.this,RegistActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    class MyAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void onBackPressed() {
        BaseActivity.finishAll();
    }

}
