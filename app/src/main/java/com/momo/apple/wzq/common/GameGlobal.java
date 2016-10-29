package com.momo.apple.wzq.common;

public class GameGlobal {
    public static final int LEVEL_0 = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;

    public static int cur_defiant_id = LEVEL_2;//对手
    public static int cur_media_id = 5;//音乐
    public static int current_tab_id = 1;//菜单栏

    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int DRAW = 3; // 平局

    public static final int DIR_HORIZONAL = 1;
    public static final int DIR_VERTICAL = 2;
    public static final int DIR_LEAN = 3;

    public static final int LEN5 = 5;
    public static final int LEN4 = 4;
    public static final int LEN3 = 3;
    public static final int LEN2 = 2;

    public static final int[][] Orientations = {
            {1, 0}, {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };
}
