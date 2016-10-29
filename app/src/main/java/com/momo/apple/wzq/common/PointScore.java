package com.momo.apple.wzq.common;

/**
 * 保存Point与相应Score的映射关系
 * Created by apple on 2016/10/21.
 */

public class PointScore {
    public Point P;
    public long Score;

    public PointScore(Point p, long score) {
        this.P = p;
        this.Score = score;
    }
}
