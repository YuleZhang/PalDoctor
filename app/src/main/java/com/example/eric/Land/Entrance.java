package com.example.eric.Land;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.eric.R;

/**
 *
 * Created by Eric_ on 2018/7/16.
 */

public class Entrance extends Activity {
    Button btLogin;
    Button btRegist;
    private ViewPager viewPager;
    private ImageView[] tips;//提示性点点数组
    private int[] images;//图片ID数组
    private int currentPage=0;//当前展示的页码
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eric_entrance);
        btLogin = findViewById(R.id.ent_btlogin);
        viewPager=(ViewPager)findViewById(R.id.image_slide_page);
        btRegist = findViewById(R.id.ent_btregist);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Entrance.this, Login.class);
                startActivityForResult(intent,0);
            }
        });
        btRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Entrance.this,Regist.class);
                startActivityForResult(intent,0);
            }
        });
        Init_View();
    }
    void Init_View(){
        viewPager=(ViewPager)findViewById(R.id.image_slide_page);
        //初始化图片资源
        images=new int[]{R.drawable.function1,R.drawable.function2,R.drawable.function3};
        //初始化 提示点点
        tips=new ImageView[3];
        for(int i=0;i<tips.length;i++){
            ImageView img=new ImageView(this);
            img.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            tips[i]=img;
            if(i==0)
            {
                img.setBackgroundResource(R.drawable.function1);
            }
            else{
                img.setBackgroundResource(R.drawable.function2);
            }
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            params.leftMargin=5;
            params.rightMargin=5;
        }
        //-----初始化PagerAdapter------
        PagerAdapter adapter=new PagerAdapter(){
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return tips.length;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object o){
                //  container.removeViewAt(position);
            }
            @Override
            public Object instantiateItem(ViewGroup container,int position){
                Log.d("tag",String.valueOf(position));
                ImageView im=new ImageView(Entrance.this);
                im.setImageResource(images[position]);
                container.addView(im);
                return im;
            }
        };
        viewPager.setAdapter(adapter);
        //更改当前tip
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                Log.e("rf",String.valueOf(position));
                // tips[currentPage].setBackgroundResource(R.drawable.p1);
                currentPage=position;
                //   tips[position].setBackgroundResource(R.drawable.p2);
            }
        });
    }
}
