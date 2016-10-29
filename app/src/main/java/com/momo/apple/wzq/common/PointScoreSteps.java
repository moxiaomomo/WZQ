package com.momo.apple.wzq.common;

import java.util.ArrayList;

/**
 * 用于保存关于下棋演进历史的对象, 主要元素为类型PointScore的列表
 * Created by apple on 2016/10/21.
 */

public class PointScoreSteps {
    public ArrayList<PointScore> PS;

    public PointScoreSteps(){
        PS = new ArrayList<PointScore>();
    }

    public PointScoreSteps(PointScoreSteps src) {
        this.PS = new ArrayList<PointScore>();
        if (src != null) {
            for (PointScore ps : src.PS) {
                this.PS.add(ps);
            }
        }
    }

    public int lineLength() {
        if (PS != null) {
            return PS.size();
        }
        return 0;
    }
}
