package com.momo.apple.wzq.common;

import java.util.ArrayList;

/**
 * 维持一个一定数量的最大堆, 元素为PointScore类型
 * Created by apple on 2016/10/22.
 */

public class PointScoreHeap {
    private int heapSize;
    private ArrayList<PointScore> psl;

    public PointScoreHeap(int size) {
        heapSize = size;
        psl = new ArrayList<PointScore>();
    }

    public void add(PointScore ps) {
        int psl_size = psl.size();
        for (int i=0; i<psl_size; ++i){
            if (ps.Score >= psl.get(i).Score) {
                psl.add(i, ps);
                break;
            }
        }
        if(psl.size() < heapSize && psl_size == psl.size()) {
            psl.add(ps);
        }
        for (int i=psl.size()-heapSize; i>0; --i) {
            psl.remove(psl.size()-1);
        }
    }

    public ArrayList<PointScore> PointScores() {
        return psl;
    }
}
