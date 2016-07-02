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
    private List<String> titleList; // 表示滑动的每一页的标题
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce);
        getSupportActionBar().hide();
        initComponent();
        // 动态加载布局
        View view1 = LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.tab1, null);
        View view2 = LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.tab2, null);
        View view3 = LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.tab3, null);

        // 把动态布局装载到List<View>中
        list = new ArrayList<View>();
        list.add(view1);
        list.add(view2);
        list.add(view3);


        // 把动态布局的标题装载到List<String>中
        titleList = new ArrayList<String>();
        titleList.add("————————————");
        titleList.add("————————————");
        titleList.add("————————————");

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
            // TODO Auto-generated method stub

            ((ViewPager) container).addView(list.get(position));
            return list.get(position);
        }

        /*
         * 从ViewPager的实现过程中，可以发现，当我们在向左或者向右滑动的过程中，实际是在往PagerAdapter容器
         * 中添加动态的布局文件，所以这边需要销毁的目标应该是要销毁的动态布局文件
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView(list.get(position));
        }

        // ViewPager中的包含的List<View>布局的个数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        /*
         * @see
         * android.support.v4.view.PagerAdapter#isViewFromObject(android.view
         * .View, java.lang.Object) 通过调用：instantiateItem(ViewGroup, int)
         * 决定是否一个page视图是否去关联特殊的对象，对于一个正常运行的PagerAdapter 这个方法是需要被执行的。
         * 如果这个方法返回true表示，表示视图view是有关联到Object这个对象的
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            // TODO Auto-generated method stub
            return view == object;
        }

        // 获得每一个pager的标题名称
        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            return titleList.get(position);
        }

    }

    @Override
    public void onBackPressed() {
        BaseActivity.finishAll();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initComponent() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pagertitle);
    }
}
