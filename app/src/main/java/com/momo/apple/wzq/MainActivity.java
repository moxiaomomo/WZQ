package com.momo.apple.wzq;

import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.infoview.MyInfoActivity;
import com.momo.apple.wzq.gameview.WzqGameActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    Intent intent = new Intent();
    //定义两种颜色设置
    static final int COLOR1 = Color.parseColor("#787878");
    static final int COLOR2 = Color.parseColor("#ffffff");

    //定义四个按钮切换按钮
    private Button ai_battle_btn = null;
    private Button bluetooth_battle_btn = null;
    private Button myinfo_btn = null;
    private Button net_battle_btn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams p =this.getWindow().getAttributes();
        p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(p);

        // 默认选中的tab id
        GameGlobal.current_tab_id = 1;
        View viewBelow = findViewById(R.id.below_view);
        new FootTapView(MainActivity.this, viewBelow);
        initBtns();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * 初始化按钮
     * */
    public void initBtns() {
        ai_battle_btn = (Button) findViewById(R.id.main_start_btn);
        ai_battle_btn.setOnClickListener(this);
        bluetooth_battle_btn = (Button) findViewById(R.id.main_bluegame_btn);
        bluetooth_battle_btn.setOnClickListener(this);
        myinfo_btn = (Button) findViewById(R.id.main_mynotes_btn);
        myinfo_btn.setOnClickListener(this);
        net_battle_btn = (Button) findViewById(R.id.main_network_btn);
        net_battle_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_start_btn:
                intent.setClass(getApplicationContext(), WzqGameActivity.class);
                startActivity(intent);
                break;
            case R.id.main_bluegame_btn:
                Toast.makeText(MainActivity.this, "sorry, not implemented now", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_mynotes_btn:
                intent.setClass(getApplicationContext(), MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.main_network_btn:
                Toast.makeText(MainActivity.this, "sorry, not implemented now", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        //当Activity不可见的时候停止切换
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}