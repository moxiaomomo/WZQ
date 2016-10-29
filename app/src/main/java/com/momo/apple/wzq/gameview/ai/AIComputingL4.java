package com.momo.apple.wzq.gameview.ai;

import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.common.Line;
import com.momo.apple.wzq.common.Point;
import com.momo.apple.wzq.common.PointScore;
import com.momo.apple.wzq.common.PointScoreHeap;
import com.momo.apple.wzq.common.PointScoreSteps;

import java.util.ArrayList;

/**
 * Created by apple on 2016/10/21.
 */

public class AIComputingL4 {
    private static final int DEPTH = 4;
    public static Point nextPoint(int[][] grid, int aiFlag, int humanFlag) {
        int width = grid[0].length;
        int height = grid.length;
        boolean isFull = true;
        Point aiPoint = null;
        Point hmPoint = null;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (grid[i][j] == GameGlobal.EMPTY) {
                    isFull = false;
                } else if (grid[i][j] == aiFlag) {
                    aiPoint = new Point(i, j);
                } else if (grid[i][j] == humanFlag) {
                    hmPoint = new Point(i, j);
                }
            }
        }
        if (isFull) return null;

        ArrayList<PointScoreSteps> psList = new ArrayList<PointScoreSteps>();
        calNextPoint(grid, psList, null, aiFlag, humanFlag, DEPTH, aiFlag);
        Point nextPoint = null;
        long maxScore = Long.MIN_VALUE;
        for (int i=0; i<DEPTH; ++i) {
            long mScore = Long.MIN_VALUE;
            for (PointScoreSteps psl : psList) {
                if (psl.PS.size()>i && psl.PS.get(i).Score >= Long.MAX_VALUE-2) {
                    nextPoint = psl.PS.get(i).P;
                    if (psl.PS.get(i).Score > mScore) {
                        mScore = psl.PS.get(i).Score;
                    }
                    if (mScore == Long.MAX_VALUE)
                        break;
                }
            }
            if (nextPoint != null)break;
        }
        if (nextPoint == null) {
            for (PointScoreSteps psl : psList) {
                long tmpScore = 0;
                for (PointScore ps : psl.PS) {
                    tmpScore += (ps.Score / psl.lineLength());
                }
                if (tmpScore > maxScore) {
                    maxScore = tmpScore;
                    nextPoint = psl.PS.get(0).P;
                }
            }
        }
        if (nextPoint != null) {
            return nextPoint;
        }

        Point pNearby = pointNearby(aiPoint, grid);
        if (pNearby != null)
            return pNearby;
        else {
            pNearby = pointNearby(hmPoint, grid);
            if (pNearby != null)
                return pNearby;
        }

        while (true) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            if (grid[x][y] == GameGlobal.EMPTY) {
                return new Point(x, y);
            }
        }
    }

    private static Point pointNearby(Point src, int[][] grid) {
        int width = grid[0].length;
        int height = grid.length;
        if (src != null && src.getX() > 0 && src.getX() < width - 1 && src.getY() > 0 && src.getY() < height - 1) {
            for (int[] x : GameGlobal.Orientations) {
                if (grid[src.getX() + x[0]][src.getY() + x[1]] == GameGlobal.EMPTY) {
                    return new Point(src.getX() + x[0], src.getY() + x[1]);
                }
            }
        }
        return null;
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

    public static long getGlobalScore(int[][] gridCopy, int flag, int rflag) {
        ArrayList<Line> flagLines = AIComputingL2.getPointsInLine(gridCopy, flag);
        ArrayList<Line> rflagLines = AIComputingL2.getPointsInLine(gridCopy, rflag);

        long tmpScore = 10;
        int[][] sgrid = new int[gridCopy.length][gridCopy[0].length];
        for (Line line : flagLines) {
            if (line.getLength() == GameGlobal.LEN5) {
                return Long.MAX_VALUE;
            }
            if (line.isCanLeft5() || line.isCanRight5()) {
                tmpScore += (int)(Math.pow(20, line.getLength()));
                if (line.isCanLeft5() && line.isCanRight5()) {
                    if (line.getLength() == GameGlobal.LEN4) {
                        return Long.MAX_VALUE-1;
                    } else if (line.getLength() == GameGlobal.LEN3) {
                        for (Point p : line.linePoints()) {
                            sgrid[p.getX()][p.getY()] += 1;
                        }
                    }
                    tmpScore += (long)(Math.pow(40, line.getLength()));
                }
            }
        }
        for (int i=0; i<sgrid.length; ++i) {
            for (int j=0; j<sgrid[0].length; ++j) {
                if (sgrid[i][j] >= 2) {
                    return Long.MAX_VALUE-2;
                }
            }
        }
        for (Line line : rflagLines) {
            if (line.isCanLeft5() || line.isCanRight5()) {
                int lineLen = line.getLength();
                tmpScore -= (long)(Math.pow(lineLen*20, lineLen));
                if (line.isCanLeft5() && line.isCanRight5()) {
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

        PointScoreHeap psh = new PointScoreHeap(20);
        long[][] scoreGrid = new long[height][width];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                if (srcGrid[i][j] != GameGlobal.EMPTY){
                    scoreGrid[i][j] = Long.MIN_VALUE;
                    continue;
                }
                int[][] gridCopy = copyGrid(srcGrid);
                gridCopy[i][j] = nextFlag;
                long s1 = getGlobalScore(gridCopy, nextFlag, oppositeFlag);

                gridCopy[i][j] = oppositeFlag;
                long s2 = getGlobalScore(gridCopy, oppositeFlag, nextFlag);
                scoreGrid[i][j] = s1 > s2 ?s1 : s2;
                psh.add(new PointScore(new Point(i, j), scoreGrid[i][j]));
            }
        }

        if (nextFlag != aiFlag) {
            for (PointScore ps : psh.PointScores()) {
                PointScoreSteps psl = new PointScoreSteps(psLine);
                psl.PS.add(ps);

                if (depth > 1 && ps.Score < Long.MAX_VALUE) {
                    int[][] gridCopy = copyGrid(srcGrid);
                    gridCopy[ps.P.getX()][ps.P.getY()] = nextFlag;
                    calNextPoint(gridCopy, pslList, psl, aiFlag, humanFlag, depth - 1, oppositeFlag);
                } else {
                    pslList.add(psl);
                }
            }
        } else {
            PointScoreSteps psl = new PointScoreSteps(psLine);
            PointScore ps = psh.PointScores().get(0);
            psl.PS.add(ps);
            if (depth > 1 && ps.Score < Long.MAX_VALUE) {
                int[][] gridCopy = copyGrid(srcGrid);
                gridCopy[ps.P.getX()][ps.P.getY()] = nextFlag;
                calNextPoint(gridCopy, pslList, psl, aiFlag, humanFlag, depth - 1, oppositeFlag);
            } else {
                pslList.add(psl);
            }
        }
    }
}
