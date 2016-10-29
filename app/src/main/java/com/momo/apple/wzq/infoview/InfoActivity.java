package com.momo.apple.wzq.infoview;

import com.momo.apple.wzq.FootTapView;
import com.momo.apple.wzq.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InfoActivity extends Activity {
    private int[] imageResId;//图片ID
    private TextView tv_title;
    private List<ImageView> imageViews;    //滑动的图片集合
    private int currentItem = 0;//当前图片的索引号
    private ViewPager viewPager;
    private String[] titles;//图片标题
    private List<View> dots; //图片标题正文的那些点集合
    private ScheduledExecutorService scheduledExecutorService;

    //切换到当前显示的图片
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(currentItem);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        WindowManager.LayoutParams p =this.getWindow().getAttributes();
        p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(p);
        initTitle();
        // 生成滚动图片
        initImageFlow();

        View view_below = findViewById(R.id.below_view);
        new FootTapView(InfoActivity.this, view_below);
    }

    private void initTitle() {
        View topView = findViewById(R.id.top_view);
        TextView titlename = (TextView) topView.findViewById(R.id.titlename);
        titlename.setText(this.getString(R.string.discover));
    }

    @Override
    public void onStart() {
        //图片自动进行切换，每隔两秒进行一次切换
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //图片自动进行切换，每隔两秒进行一次切换
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1,2, TimeUnit.SECONDS);
        super.onStart();
    }

    @Override
    protected void onStop() {
        //当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
        super.onStop();
    }

    	/*
     * 图片切换功能
	 * */

    private void initImageFlow() {
        //放置图片资源
        imageResId = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
        titles = new String[imageResId.length];
        imageViews = new ArrayList<ImageView>();

        //初始化图片资源
        for (int i = 0; i < imageResId.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResId[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
        }

        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.v_dot0));
        dots.add(findViewById(R.id.v_dot1));
        dots.add(findViewById(R.id.v_dot2));
        dots.add(findViewById(R.id.v_dot3));
        dots.add(findViewById(R.id.v_dot4));

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(titles[0]);

        //填充viewPager页面的适配器
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setAdapter(new InfoActivity.MyAdapter());
        //设置监听器，当ViewPager中的页面改变时调用
        viewPager.setOnPageChangeListener(new InfoActivity.MyPageChangeListener());
    }

    /**
     * 换行切换任务
     *
     * @author user36
     */
    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            event.startTracking();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            // *** DO ACTION HERE ***

            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * 填充ViewPager页面的适配器
     *
     * @author xiaomo
     */
    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageResId.length;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    /***
     * 当viewPager页面中的状态发生改变时调用
     *
     * @author user36
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            tv_title.setText(titles[position]);
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }

    }
}
