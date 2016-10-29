package com.momo.apple.wzq;

import com.momo.apple.wzq.infoview.InfoActivity;
import com.momo.apple.wzq.settingview.SettingActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FootTapView implements OnClickListener {

    public Button homeTap, settingTap, disTap;
    public RelativeLayout homeRL, settingRL, disRL;
    public TextView homeText, settingText, disText;
    public View view_below;
    public Intent intent = null;
    public Activity activity;
    public static int currentid = 1;
    static final int COLOR1 = Color.parseColor("#5eaa5e");

    public FootTapView(Activity activity, View v) {
        this.activity = activity;

        homeTap = (Button) v.findViewById(R.id.homeTap);
        settingTap = (Button) v.findViewById(R.id.settingTap);
        disTap = (Button) v.findViewById(R.id.discoverTap);

        homeRL = (RelativeLayout) v.findViewById(R.id.homeRL);
        settingRL = (RelativeLayout) v.findViewById(R.id.settingRL);
        disRL = (RelativeLayout) v.findViewById(R.id.discoverRL);

        homeText = (TextView) v.findViewById(R.id.home_text);
        settingText = (TextView) v.findViewById(R.id.setting_text);
        disText = (TextView) v.findViewById(R.id.discover_text);
        setbackground();

        homeTap.setOnClickListener(this);
        settingTap.setOnClickListener(this);
        disTap.setOnClickListener(this);

        homeRL.setOnClickListener(this);
        settingRL.setOnClickListener(this);
        disRL.setOnClickListener(this);

        homeText.setOnClickListener(this);
        settingText.setOnClickListener(this);
        homeText.setOnClickListener(this);
    }

    public void setbackground() {
        switch (currentid) {
            case 1:
                homeTap.setBackgroundResource(R.drawable.icon_1_c);
                homeText.setTextColor(COLOR1);
                break;
            case 2:
                settingTap.setBackgroundResource(R.drawable.icon_2_c);
                settingText.setTextColor(COLOR1);
                break;
            case 4:
                disTap.setBackgroundResource(R.drawable.icon_4_c);
                disText.setTextColor(COLOR1);
                break;
        }
    }

    public void setIntent(int id) {
        intent = new Intent();
        switch (id) {
            case 1:
                intent.setClass(activity.getApplicationContext(), MainActivity.class);
                break;
            case 2:
                intent.setClass(activity.getApplicationContext(), SettingActivity.class);
                break;
            case 4:
                intent.setClass(activity.getApplicationContext(), InfoActivity.class);
                break;
        }
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeTap:
            case R.id.homeRL:
            case R.id.home_text:
                if (currentid == 1) {

                } else {
                    currentid = 1;
                    setIntent(1);
                }
                break;
            case R.id.settingTap:
            case R.id.settingRL:
            case R.id.setting_text:
                if (currentid == 2) {

                } else {
                    currentid = 2;
                    setIntent(2);
                }
                break;
            case R.id.discoverTap:
            case R.id.discoverRL:
            case R.id.discover_text:
                if (currentid == 4) {

                } else {
                    currentid = 4;
                    setIntent(4);
                }
                break;
        }
    }

}
