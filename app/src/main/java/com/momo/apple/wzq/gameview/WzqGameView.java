package com.momo.apple.wzq.gameview;

import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.R;
import com.momo.apple.wzq.common.Point;
import com.momo.apple.wzq.database.DBWzq;
import com.momo.apple.wzq.gameview.ai.AIComputingL1;
import com.momo.apple.wzq.gameview.ai.AIComputingL2;
import com.momo.apple.wzq.gameview.ai.AIComputingL4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.graphics.BitmapFactory;

public class WzqGameView extends View {

    protected static int GRID_COUNT = 10;
    protected int GRID_WIDTH = 30; // 棋盘格的宽度
    protected int CHESS_DIAMETER = 26; // 棋的直径
    protected static int mStartX;// 棋盘定位的左上角X
    protected static int mStartY;// 棋盘定位的左上角Y
    private Bitmap[] mChessBW; // 黑棋和白棋
    private static int[][] mGridArray; // 网格

    boolean key = false;
    int wbflag = 1; //该下白棋了=2，该下黑棋了=1. 这里先下黑棋（黑棋以后设置为机器自动下的棋子）
    int mWinFlag = 0;

    int mGameState = GAMESTATE_RUN; //游戏阶段：0=尚未游戏，1=正在进行游戏，2=游戏结束
    static final int GAMESTATE_PRE = 0;
    static final int GAMESTATE_RUN = 1;
    static final int GAMESTATE_PAUSE = 2;
    static final int GAMESTATE_END = 3;

    public TextView mStatusTextView; //  根据游戏状态设置显示的文字

    public boolean isTimeOfPlayer = true;//是否为玩家走棋

    int curWinFlag = GameGlobal.BLACK;
    CharSequence mText;
    CharSequence STRING_WIN = getContext().getString(R.string.win);
    CharSequence STRING_LOSE = getContext().getString(R.string.lose);
    CharSequence STRING_EQUAL = getContext().getString(R.string.equal);

    Context context;

    /*
     * ��Ϣ����
     * */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            onRedraw();
            if (mGameState == GAMESTATE_END) {
                showTextView(mText);
            }
        }
    };

    /*
     * 重绘界面
     * */
    public void onRedraw() {
        this.invalidate();
    }

    public WzqGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WzqGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);  //20090530
        this.setFocusableInTouchMode(true);
        init();
    }


    // 初始化参数
    public void init() {
        mGameState = 1;
        wbflag = GameGlobal.BLACK;
        mWinFlag = 0;
        mGridArray = new int[GRID_COUNT + 1][GRID_COUNT + 1];

        mChessBW = new Bitmap[2];

        Bitmap bitmap = Bitmap.createBitmap(CHESS_DIAMETER, CHESS_DIAMETER, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Resources r = this.getContext().getResources();

        Drawable tile = r.getDrawable(R.drawable.black);
        tile.setBounds(0, 0, CHESS_DIAMETER, CHESS_DIAMETER);
        tile.draw(canvas);
        mChessBW[0] = bitmap;

        tile = r.getDrawable(R.drawable.white);
        tile.setBounds(0, 0, CHESS_DIAMETER, CHESS_DIAMETER);
        tile.draw(canvas);
        mChessBW[1] = bitmap;
        calIconSize();
    }

    /*
     * 计算Icon大小
     * */
    private void calIconSize() {
        DisplayMetrics dm = new DisplayMetrics();
        // 获取当前屏幕显示
        ((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        // GRID_WIDTH=dm.widthPixels/GRID_COUNT;//指定格子的宽度值
        //指定棋子的宽度
        CHESS_DIAMETER = GRID_WIDTH - 4;
    }

    public void setTextView(TextView tv, Context context) {
        mStatusTextView = tv;
        mStatusTextView.setVisibility(View.INVISIBLE);
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (this.getWidth() < this.getHeight()) {
            GRID_WIDTH = this.getWidth() / (GRID_COUNT + 1);
        } else {
            GRID_WIDTH = this.getHeight() / (GRID_COUNT + 1);
        }
        mStartX = w / 2 - GRID_COUNT * GRID_WIDTH / 2;
        mStartY = h / 2 - GRID_COUNT * GRID_WIDTH / 3 * 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (mGameState) {
            case GAMESTATE_PRE:
                break;
            case GAMESTATE_RUN: {
                int x;
                int y;
                float x0 = (event.getX() - mStartX) % GRID_WIDTH;
                float y0 = (event.getY() - mStartY) % GRID_WIDTH;
                if (x0 >= GRID_WIDTH / 2) {
                    x = (int) ((event.getX() - mStartX) / GRID_WIDTH) + 1;
                } else {
                    x = (int) ((event.getX() - mStartX) / GRID_WIDTH);
                }
                if (y0 >= GRID_WIDTH / 2) {
                    y = (int) ((event.getY() - mStartY) / GRID_WIDTH) + 1;
                } else {
                    y = (int) ((event.getY() - mStartY) / GRID_WIDTH);
                }
                if (x < -1 || x > GRID_COUNT || y < -1 || y > GRID_COUNT) {
                    break;
                }

                x = x > 0 ? (x <= GRID_COUNT ? x : GRID_COUNT) : 0;
                y = y > 0 ? (y <= GRID_COUNT ? y : GRID_COUNT) : 0;
                if (mGridArray[x][y] == 0) {
                    if (wbflag == GameGlobal.BLACK && isTimeOfPlayer) {
                        putChess(x, y, GameGlobal.BLACK);
                        onRedraw();
                        if (checkWin(GameGlobal.BLACK)) {
                            mGameState = GAMESTATE_END;
                            showTextView(mText);
                        } else if (checkFull()) {
                            mGameState = GAMESTATE_END;
                            showTextView(mText);
                        } else {
                            isTimeOfPlayer = false;
                            AIRunning();
                        }
                    }
                }
            }
            break;
            case GAMESTATE_PAUSE:
                break;
            case GAMESTATE_END:
                break;
        }
        return true;
    }

    /*
     * AI computation
     */
    private void AIRunning() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                switch (GameGlobal.cur_defiant_id) {
                    case 0:
                        Point p1 = AIComputingL1.nextPoint(mGridArray, GameGlobal.WHITE, GameGlobal.BLACK);
                        putChess(p1.getX(), p1.getY(), GameGlobal.WHITE);
                        isTimeOfPlayer = true;
                        break;
                    case 1:
                        Point p2 = AIComputingL2.nextPoint(mGridArray, GameGlobal.WHITE, GameGlobal.BLACK);
                        putChess(p2.getX(), p2.getY(), GameGlobal.WHITE);
                        isTimeOfPlayer = true;
                        break;
                    case 2:
                        Point p3 = AIComputingL4.nextPoint(mGridArray, GameGlobal.WHITE, GameGlobal.BLACK);
                        putChess(p3.getX(), p3.getY(), GameGlobal.WHITE);
                        isTimeOfPlayer = true;
                        break;
                }
                Bundle b = new Bundle();
                if (checkWin(GameGlobal.WHITE)) {
                    mGameState = GAMESTATE_END;

                } else if (checkFull()) {
                    mGameState = GAMESTATE_END;

                } else {
                    b.putString("handlerid", "onRedraw");
                }

                handler.sendMessage(handler.obtainMessage());

            }
        }).start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        //Log.e("KeyEvent.KEYCODE_DPAD_CENTER", " " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            switch (mGameState) {
                case GAMESTATE_PRE:
                    break;
                case GAMESTATE_RUN:
                    break;
                case GAMESTATE_PAUSE:
                    break;
            }
        }

        return super.onKeyDown(keyCode, msg);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        // 画棋盘
        Paint paintRect = new Paint();
        paintRect.setColor(Color.BLACK);
        paintRect.setStrokeWidth(2);
        paintRect.setStyle(Style.STROKE);

        for (int i = 0; i < GRID_COUNT; i++) {
            for (int j = 0; j < GRID_COUNT; j++) {
                int mLeft = i * GRID_WIDTH + mStartX;
                int mTop = j * GRID_WIDTH + mStartY;
                int mRright = mLeft + GRID_WIDTH;
                int mBottom = mTop + GRID_WIDTH;
                canvas.drawRect(mLeft, mTop, mRright, mBottom, paintRect);
            }
        }
        //画棋盘的外边框
        paintRect.setStrokeWidth(4);
        canvas.drawRect(mStartX, mStartY, mStartX + GRID_WIDTH * GRID_COUNT, mStartY + GRID_WIDTH * GRID_COUNT, paintRect);

        Bitmap whiteImg = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        int wHalfW = whiteImg.getWidth() / 2;
        int wHalfH = whiteImg.getHeight() / 2;
        Bitmap blackImg = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        int bHalfW = blackImg.getWidth() / 2;
        int bHalfH = blackImg.getHeight() / 2;

        for (int i = 0; i <= GRID_COUNT; i++) {
            for (int j = 0; j <= GRID_COUNT; j++) {
                if (mGridArray[i][j] == GameGlobal.BLACK) {
                    //通过图片来画
                    canvas.drawBitmap(
                            blackImg,
                            mStartX + i * GRID_WIDTH - bHalfW,
                            mStartY + j * GRID_WIDTH - bHalfH,
                            null
                    );

                } else if (mGridArray[i][j] == GameGlobal.WHITE) {
                    canvas.drawBitmap(
                            whiteImg,
                            mStartX + i * GRID_WIDTH - wHalfW,
                            mStartY + j * GRID_WIDTH - wHalfH,
                            null
                    );
                }
            }
        }
    }

    public void putChess(int x, int y, int blackwhite) {
        mGridArray[x][y] = blackwhite;
    }

    public boolean checkWin(int wbflag) {
        boolean isWin = false;
        for (int i = 0; i <= GRID_COUNT; i++)
            for (int j = 0; j <= GRID_COUNT; j++) {
                if (((i + 4) <= (GRID_COUNT)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i + 1][j] == wbflag) && (mGridArray[i + 2][j] == wbflag) && (mGridArray[i + 3][j] == wbflag) && (mGridArray[i + 4][j] == wbflag)) {
                    isWin = true;
                    break;
                }

                if (((j + 4) <= (GRID_COUNT)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i][j + 1] == wbflag) && (mGridArray[i][j + 2] == wbflag) && (mGridArray[i][j + 3] == wbflag) && (mGridArray[i][j + 4] == wbflag)) {
                    isWin = true;
                    break;
                }

                if (((j + 4) <= (GRID_COUNT)) && ((i + 4) <= (GRID_COUNT)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i + 1][j + 1] == wbflag) && (mGridArray[i + 2][j + 2] == wbflag) && (mGridArray[i + 3][j + 3] == wbflag) && (mGridArray[i + 4][j + 4] == wbflag)) {
                    isWin = true;
                    break;
                }

                if (((i - 4) >= 0) && ((j + 4) <= (GRID_COUNT)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i - 1][j + 1] == wbflag) && (mGridArray[i - 2][j + 2] == wbflag) && (mGridArray[i - 3][j + 3] == wbflag) && (mGridArray[i - 4][j + 4] == wbflag)) {
                    isWin = true;
                    break;
                }
            }

        if (isWin) {
            curWinFlag = wbflag;
            return true;
        } else
            return false;
    }

    public boolean checkFull() {
        int mNotEmpty = 0;
        for (int i = 0; i <= GRID_COUNT; i++)
            for (int j = 0; j <= GRID_COUNT; j++) {
                if (mGridArray[i][j] != 0) mNotEmpty += 1;
            }

        if (mNotEmpty >= (GRID_COUNT + 1) * (GRID_COUNT + 1)) {
            curWinFlag = GameGlobal.DRAW;
            return true;
        } else {
            return false;
        }
    }

    public void showTextView(CharSequence mT) {
        int iconId = R.drawable.black;
        if (curWinFlag == GameGlobal.BLACK) {
            mT = "You win!\n" + getContext().getString(R.string.score) + ": " + getfenshu();
        } else if (curWinFlag == GameGlobal.WHITE) {
            mT = "You lose!\n";
            iconId = R.drawable.white;
        } else {
            mT = "Game ends in a draw.\n";
        }
        new AlertDialog.Builder(context).setIcon(iconId).setTitle(mT).setPositiveButton(
                getContext().getString(R.string.again),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                }).setNegativeButton(
                getContext().getString(R.string.exit),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

    /*
    * 重新开始, 初始化参数
     */

    public void restartGame() {
        mGameState = GAMESTATE_RUN;
        this.setVisibility(View.VISIBLE);
        this.mStatusTextView.setVisibility(View.INVISIBLE);
        this.init();
        this.invalidate();
    }

    public int getfenshu() {
        int fenshu = 0;
        switch (GameGlobal.cur_defiant_id) {
            case 0:
                for (int i = 0; i <= GRID_COUNT; i++) {
                    for (int j = 0; j <= GRID_COUNT; j++) {
                        if (mGridArray[i][j] == GameGlobal.WHITE) {
                            fenshu++;
                        }
                    }
                }
                fenshu = 50 - fenshu;
                break;
            case 1:
                for (int i = 0; i <= GRID_COUNT; i++) {
                    for (int j = 0; j <= GRID_COUNT; j++) {
                        if (mGridArray[i][j] == GameGlobal.WHITE) {
                            fenshu = fenshu + 10;
                        }
                    }
                }
                fenshu = 500 - fenshu;
                break;
            case 2:
                for (int i = 0; i <= GRID_COUNT; i++) {
                    for (int j = 0; j <= GRID_COUNT; j++) {
                        if (mGridArray[i][j] == GameGlobal.WHITE) {
                            fenshu = fenshu + 50;
                        }
                    }
                }
                fenshu = 2000 - fenshu;
                break;
        }
        ContentValues values = new ContentValues();
        values.put("score", fenshu + "");
        DBWzq helper = new DBWzq(context);
        helper.insert(values);
        helper.close();
        return fenshu;
    }
}
