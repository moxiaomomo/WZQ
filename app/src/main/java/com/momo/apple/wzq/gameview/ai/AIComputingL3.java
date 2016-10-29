package com.momo.apple.wzq.gameview.ai;

import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.common.Line;
import com.momo.apple.wzq.common.Point;
import com.momo.apple.wzq.common.PointScore;
import com.momo.apple.wzq.common.PointScoreSteps;

import java.util.ArrayList;

/**
 * Created by apple on 2016/10/21.
 */

public class AIComputingL3 {
    private static final int DEPTH = 4;
    public static Point nextPoint(int[][] grid, int aiFlag, int humanFlag) {
        int width = grid[0].length;
        int height = grid.length;
        boolean isFull = true;
        Point aiPoint = null;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (grid[i][j] == GameGlobal.EMPTY) {
                    isFull = false;
                } else if (grid[i][j] == aiFlag) {
                    aiPoint = new Point(i, j);
                }
            }
        }
        if (isFull) return null;
        if (aiPoint == null) {
            while (true) {
                int x = (int) (Math.random() * width);
                int y = (int) (Math.random() * height);
                if (grid[x][y] == GameGlobal.EMPTY) {
                    return new Point(x, y);
                }
            }
        }

        ArrayList<PointScoreSteps> psList = new ArrayList<PointScoreSteps>();
        calNextPoint(grid, psList, null, aiFlag, humanFlag, DEPTH, aiFlag);
        Point nextPoint = null;
        long maxScore = Long.MIN_VALUE;
        for (PointScoreSteps psl : psList) {
            long tmpScore = 0;
            for(PointScore ps : psl.PS) {
                tmpScore += (ps.Score/psl.lineLength());
            }
            if(tmpScore > maxScore) {
                maxScore = tmpScore;
                nextPoint = psl.PS.get(0).P;
            }
        }
        if (nextPoint != null) {
            return nextPoint;
        }

        if (aiPoint != null && aiPoint.getX() > 0 && aiPoint.getX() < width - 1 && aiPoint.getY() > 0 && aiPoint.getY() < height - 1) {
            for (int[] x : GameGlobal.Orientations) {
                if (grid[aiPoint.getX() + x[0]][aiPoint.getY() + x[1]] == GameGlobal.EMPTY) {
                    return new Point(aiPoint.getX() + x[0], aiPoint.getY() + x[1]);
                }
            }
        }

        while (true) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            if (grid[x][y] == GameGlobal.EMPTY) {
                return new Point(x, y);
            }
        }
    }

    private static int[][] copyGrid(int[][] srcGrid) {
        int width = srcGrid[0].length;
        int height = srcGrid.length;
        int[][] gridCopy = new int[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                gridCopy[i][j] = srcGrid[i][j];
            }
        }
        return gridCopy;
    }

    public static long getGlobalScore(int[][] gridCopy, int flag, int rflag, int i, int j) {
        ArrayList<Line> flagLines = AIComputingL1.getPointsInLine(gridCopy, flag);
        ArrayList<Line> rflagLines = AIComputingL1.getPointsInLine(gridCopy, rflag);

        long tmpScore = 10;
        for (Line line : flagLines) {
            if (line.getLength() == GameGlobal.LEN5) {
                return Long.MAX_VALUE;
            }
            boolean can1 = AIComputingL2.canBeLine5(gridCopy, line, 1);
            boolean can2 = AIComputingL2.canBeLine5(gridCopy, line, 2);
            if (can1 || can2) {
                tmpScore += (int)(Math.pow(20, line.getLength()));
                if (can1 && can2) {
                    if (line.getLength() == GameGlobal.LEN4) {
                        return Long.MAX_VALUE-1;
                    }
                    tmpScore += (long)(Math.pow(40, line.getLength()));
                }
            }
        }
        for (Line line : rflagLines) {
            boolean can1 = AIComputingL2.canBeLine5(gridCopy, line, 1);
            boolean can2 = AIComputingL2.canBeLine5(gridCopy, line, 2);
            if (can1 || can2) {
                int lineLen = line.getLength();
                tmpScore -= (long)(Math.pow(lineLen*20, lineLen));
                if (can1 && can2) {
                    tmpScore -= (long)(Math.pow(lineLen*40, lineLen));
                }
            }
        }
        return tmpScore;
    }

    private static void calNextPoint(int[][] srcGrid,
                                     ArrayList<PointScoreSteps> pslList,
                                     PointScoreSteps psLine,
                                     int aiFlag, int humanFlag,
                                     int depth, int nextFlag) {
        int width = srcGrid[0].length;
        int height = srcGrid.length;
        int oppositeFlag = -1;
        if (nextFlag == aiFlag) {
            oppositeFlag = humanFlag;
        } else {
            oppositeFlag = aiFlag;
        }

        long gmaxScore = Long.MIN_VALUE;
        int gmaxX = -1;
        int gmaxY = -1;
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                if (srcGrid[i][j] != GameGlobal.EMPTY){
                    continue;
                }

                int[][] gridCopy = copyGrid(srcGrid);
                gridCopy[i][j] = nextFlag;
                long maxScore = Long.MIN_VALUE;
                long tmpScore = getGlobalScore(gridCopy, nextFlag, oppositeFlag, i, j);
                if (tmpScore >= maxScore)
                    maxScore = tmpScore;

                gridCopy[i][j] = oppositeFlag;
                tmpScore = getGlobalScore(gridCopy, oppositeFlag, nextFlag, i, j);
                if (tmpScore > maxScore)
                    maxScore = tmpScore;

                if (maxScore >= gmaxScore) {
                    gmaxScore = maxScore;
                    gmaxX = i;
                    gmaxY = j;
                }

                if (nextFlag != aiFlag) {
                    PointScoreSteps psl = new PointScoreSteps(psLine);
                    psl.PS.add(new PointScore(new Point(i, j), maxScore));

                    if (depth > 1 && maxScore < Long.MAX_VALUE) {
                        gridCopy[i][j] = nextFlag;
                        calNextPoint(gridCopy, pslList, psl, aiFlag, humanFlag, depth - 1, oppositeFlag);
                    } else {
                        pslList.add(psl);
                    }
                }
            }
        }
        if (nextFlag == aiFlag) {
            PointScoreSteps psl = new PointScoreSteps(psLine);
            psl.PS.add(new PointScore(new Point(gmaxX, gmaxY), gmaxScore));
            if (depth > 1 && gmaxScore < Long.MAX_VALUE) {
                int[][] gridCopy = copyGrid(srcGrid);
                gridCopy[gmaxX][gmaxY] = nextFlag;
                calNextPoint(gridCopy, pslList, psl, aiFlag, humanFlag, depth - 1, oppositeFlag);
            } else {
                pslList.add(psl);
            }
        }
    }
}
