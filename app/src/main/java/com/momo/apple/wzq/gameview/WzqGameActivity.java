package com.momo.apple.wzq.gameview;

import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.R;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class WzqGameActivity extends Activity {

    WzqGameView gameView;
    private TextView time_black_note;
    private TextView time_white_note;
    private TextView cur_role_note;

    TimeThread timeThread;
    public MediaPlayer mPlayer = null;
    private boolean playing_media = true;

    public int black_time = 0;
    public int white_time = 0;
    public int cur_music_len = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            final String sec = getApplication().getString(R.string.second);
            time_white_note.setText(white_time + sec);
            time_black_note.setText(black_time + sec);
            if (gameView.isTimeOfPlayer) {
                cur_role_note.setText(getApplication().getString(R.string.player_time));
                cur_role_note.setTextColor(Color.rgb(200, 0, 200));
            } else {
                cur_role_note.setText(getApplication().getString(R.string.ai_time));
                cur_role_note.setTextColor(Color.rgb(0, 200, 200));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);
        WindowManager.LayoutParams p =this.getWindow().getAttributes();
        p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(p);

        time_white_note = (TextView) findViewById(R.id.time_baiqi);
        time_black_note = (TextView) findViewById(R.id.time_heiqi);
        cur_role_note = (TextView) findViewById(R.id.cur_role_note);
        gameView = (WzqGameView) this.findViewById(R.id.gobangview);
        gameView.setTextView((TextView) this.findViewById(R.id.text), this);
        initTitle();
        initBackgroundMusic();

        timeThread = new TimeThread();
        timeThread.setTimeRunning(true);
        timeThread.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mPlayer.isPlaying()) {
            try {
                mPlayer.prepare();
                mPlayer.seekTo(cur_music_len);
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!timeThread.timeRunning) {
            timeThread = new TimeThread();
            timeThread.setTimeRunning(true);
            timeThread.start();
        }
    }

    /*
     * 初始化音效设置
     * */
    private void initBackgroundMusic() {
        switch (GameGlobal.cur_media_id) {
            case 0:
                mPlayer = MediaPlayer.create(this, R.raw.bg0);
                break;
            case 1:
                mPlayer = MediaPlayer.create(this, R.raw.bg1);
                break;
            case 2:
                mPlayer = MediaPlayer.create(this, R.raw.bg2);
                break;
            case 3:
                mPlayer = MediaPlayer.create(this, R.raw.bg3);
                break;
            case 4:
                mPlayer = MediaPlayer.create(this, R.raw.bg4);
                break;
            case 5:
                mPlayer = MediaPlayer.create(this, R.raw.gamesound);
                break;

        }
        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.start();
        }
    }

    /*
     * 初始化页面顶部参数
     * */
    private void initTitle() {
        View topView = findViewById(R.id.top_view);
        TextView titlename = (TextView) topView.findViewById(R.id.titlename);
        ImageButton backbtn = (ImageButton) topView.findViewById(R.id.btn_back);
        Button soundbtn = (Button) topView.findViewById(R.id.rightbtn);

        backbtn.setVisibility(View.VISIBLE);
        soundbtn.setVisibility(View.VISIBLE);

        backbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        soundbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (playing_media) {
                    mPlayer.pause();
                    playing_media = false;
                } else {
                    mPlayer.start();
                    playing_media = true;
                }
            }
        });

        String name = this.getString(R.string.AI_1);
        switch (GameGlobal.cur_defiant_id) {
            case 0:
                name = this.getString(R.string.AI_1);
                break;
            case 1:
                name = this.getString(R.string.AI_2);
                break;
            case 2:
                name = this.getString(R.string.AI_3);
                break;

        }
        titlename.setText(name);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            cur_music_len = mPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onStop() {
        mPlayer.stop();
        timeThread.setTimeRunning(false);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mPlayer.release();
        timeThread.setTimeRunning(false);
        super.onDestroy();
    }

    class TimeThread extends Thread {
        private boolean timeRunning = true;
        public void setTimeRunning(boolean timeRunning) {
            this.timeRunning = timeRunning;
        }

        @Override
        public void run() {
            while (timeRunning) {
                if (gameView.isTimeOfPlayer == true && gameView.mGameState == WzqGameView.GAMESTATE_RUN) {
                    black_time++;
                    handler.sendMessage(handler.obtainMessage());
                } else if (gameView.isTimeOfPlayer == false && gameView.mGameState == WzqGameView.GAMESTATE_RUN) {
                    white_time++;
                    handler.sendMessage(handler.obtainMessage());
                } else if (gameView.mGameState == WzqGameView.GAMESTATE_END) {
                    black_time = 0;
                    white_time = 0;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            timeRunning = false;
        }
    }
}
