package com.momo.apple.wzq.settingview;

import com.momo.apple.wzq.FootTapView;
import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.R;
import com.momo.apple.wzq.database.DBWzq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener {

    private RelativeLayout settingRecord;
    private RelativeLayout settingMedia;
    private RelativeLayout settingActor;
    private RelativeLayout settingReset;
    private RelativeLayout settingBluetooth;
    private TextView recordList;
    private String[] musicList = new String[]{"bg0", "bg1", "bg2", "bg3", "bg4"};
    private String[] actorList = null;

    DBWzq dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        WindowManager.LayoutParams p =this.getWindow().getAttributes();
        p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(p);

        actorList = new String[]{
                this.getString(R.string.AI_1),
                this.getString(R.string.AI_2),
                this.getString(R.string.AI_3)};

        View view_below = findViewById(R.id.below_view);

        dbHelper = new DBWzq(this);
        initTitle();
        new FootTapView(SettingActivity.this, view_below);
        settingRecord = (RelativeLayout) findViewById(R.id.setting_listjilu);
        settingMedia = (RelativeLayout) findViewById(R.id.setting_yinxiao);
        settingActor = (RelativeLayout) findViewById(R.id.setting_duishou);
        settingReset = (RelativeLayout) findViewById(R.id.setting_huifu);
        settingBluetooth = (RelativeLayout) findViewById(R.id.setting_lanya);

        recordList = (TextView) settingRecord.findViewById(R.id.listbtn_showname);
        recordList.setText(this.getString(R.string.setRecord));
        recordList = (TextView) settingMedia.findViewById(R.id.listbtn_showname);
        recordList.setText(this.getString(R.string.setMedia));
        recordList = (TextView) settingActor.findViewById(R.id.listbtn_showname);
        recordList.setText(this.getString(R.string.setActor));
        recordList = (TextView) settingBluetooth.findViewById(R.id.listbtn_showname);
        recordList.setText(this.getString(R.string.setBlueTeeth));
        recordList = (TextView) settingReset.findViewById(R.id.listbtn_showname);
        recordList.setText(this.getString(R.string.setRestore));

        settingRecord.setOnClickListener(this);
        settingMedia.setOnClickListener(this);
        settingActor.setOnClickListener(this);
        settingReset.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_listjilu:
                settingRecord();
                break;
            case R.id.setting_yinxiao:
                settingMedia();
                break;
            case R.id.setting_duishou:
                settingActor();
                break;
            case R.id.setting_huifu:
                settingReset();
                break;
        }
    }

    private void initTitle() {
        View topView = findViewById(R.id.top_view);
        TextView titlename = (TextView) topView.findViewById(R.id.titlename);
        titlename.setText(this.getString(R.string.settings));
    }

    private void settingRecord() {
        new AlertDialog.Builder(this).setIcon(R.drawable.clock).setTitle("是否删除记录").setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.delall();
                        dbHelper.close();
                        showDialog("记录已清除...");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        }).show();
    }

    private void settingReset() {
        new AlertDialog.Builder(this).setIcon(R.drawable.clock).setTitle("是否恢复设置").setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameGlobal.cur_defiant_id = 1;
                        GameGlobal.cur_media_id = 1;
                        showDialog("设置恢复...");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        }).show();
    }

    private void settingMedia() {
        int i = GameGlobal.cur_media_id;

        new AlertDialog.Builder(SettingActivity.this).setIcon(R.drawable.clock).setTitle("音效设置").setSingleChoiceItems(
                musicList, i,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameGlobal.cur_media_id = which;
                        showDialog("您选择的是：" + musicList[which]);
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void settingActor() {

        int i = GameGlobal.cur_defiant_id;

        new AlertDialog.Builder(SettingActivity.this).setIcon(R.drawable.white).setTitle("对手设置").setSingleChoiceItems(
                actorList, i,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameGlobal.cur_defiant_id = which;
                        showDialog("您选择的是：" + actorList[which]);
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    //显示对话框提示信息
    private void showDialog(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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