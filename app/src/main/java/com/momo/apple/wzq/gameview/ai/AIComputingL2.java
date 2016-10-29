package com.momo.apple.wzq.gameview.ai;

import com.momo.apple.wzq.common.GameGlobal;
import com.momo.apple.wzq.common.Line;
import com.momo.apple.wzq.common.Point;

import java.util.ArrayList;

/**
 * Created by apple on 2016/10/21.
 */

public class AIComputingL2 {
    public static boolean isValidPos(int[][] grid, Point a, int deltaX, int deltaY) {
        int width = grid[0].length;
        int height = grid.length;
        if (a.getX()+deltaX >= 0 && a.getX()+deltaX <width &&
                a.getY()+deltaY >= 0 && a.getY()+deltaY < height) {
            return true;
        }
        return false;
    }

    public static int[] getOrientation(Point a, Point b) {
        int x = a.getX() < b.getX()? 1 : (a.getX()==b.getX()? 0 : -1);
        int y = a.getY() < b.getY()? 1 : (a.getY()==b.getY()? 0 : -1);
        return new int[]{x,y};
    }

    public static boolean isInOneLine(int grid[][], Point start, Point end) {
        int startVal = grid[start.getX()][start.getY()];
        int endVal = grid[end.getX()][end.getY()];
        if (startVal != endVal) {
            return false;
        }

        boolean isInOneLine = true;
        int[] ori = getOrientation(start, end);
        if (ori[0] != 0) {
            int j = start.getY();
            for (int i=start.getX(); i!=end.getX(); i+=ori[0]) {
                if (grid[i][j] != startVal) {
                    isInOneLine = false;
                    return isInOneLine;
                }
                j += ori[1];
            }
        } else if (ori[1] != 0) {
            int i = start.getX();
            for (int j=start.getY(); j!=end.getY(); j+=ori[1]) {
                if (grid[i][j] != startVal) {
                    isInOneLine = false;
                    return isInOneLine;
                }
                i += ori[0];
            }
        }
        return isInOneLine;
    }

    public static ArrayList<Line> getPointsInLine(int grid[][], int flag) {
        int width = grid[0].length;
        int height = grid.length;

        ArrayList<Line> list = new ArrayList<Line>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (grid[i][j] != flag) {
                    continue;
                }
                if (j == 0 || j > 0 && grid[i][j - 1] != flag) {
                    for (int l = 4; l >= 1; l--) {
                        if (j + l < height && isInOneLine(grid, new Point(i, j), new Point(i, j + l))) {
                            list.add(new Line(new Point(i, j), new Point(i, j + l), grid));
                            break;
                        }
                    }
                }
                if (i == 0 || i > 0 && grid[i - 1][j] != flag) {
                    for (int l = 4; l >= 1; l--) {
                        if (i + l < width && isInOneLine(grid, new Point(i, j), new Point(i + l, j))) {
                            list.add(new Line(new Point(i, j), new Point(i + l, j), grid));
                            break;
                        }
                    }
                }
                if (i == 0 || j == 0 || (i > 0 && j > 0 && grid[i - 1][j - 1] != flag)) {
                    for (int l = 4; l >= 1; l--) {
                        if (i + l < width && j + l < height && isInOneLine(grid, new Point(i, j), new Point(i + l, j + l))) {
                            list.add(new Line(new Point(i, j), new Point(i + l, j + l), grid));
                            break;
                        }
                    }
                }
                if (i == 0 || j == height - 1 || (i > 0 && j < height - 1 && grid[i - 1][j + 1] != flag)) {
                    for (int l = 4; l >= 1; l--) {
                        if (i + l < width && j - l >= 0 && isInOneLine(grid, new Point(i, j), new Point(i + l, j - l))) {
                            list.add(new Line(new Point(i, j), new Point(i + l, j - l), grid));
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }

    public static boolean canBeLine5(int[][] grid, Line line, int dir) {
        int[] ori = line.getOrientation();
        int[] revOri = line.getRevrtOrientation();
        Point a = line.getA();
        Point b = line.getB();
        int leftLen = GameGlobal.LEN5 - line.getLength();
        if (dir ==0 || dir == 1) {
            for (int i = 0; i < leftLen; ) {
                if (isValidPos(grid, b, ori[0], ori[1])) {
                    int nX = b.getX() + ori[0];
                    int nY = b.getY() + ori[1];
                    if (grid[nX][nY] == GameGlobal.EMPTY || grid[nX][nY] == grid[b.getX()][b.getY()]) {
                        leftLen -= 1;
                        b = new Point(nX, nY);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (dir == 0 || dir == 2) {
            for (int i = 0; i < leftLen; ) {
                if (isValidPos(grid, a, revOri[0], revOri[1])) {
                    int nX = a.getX() + revOri[0];
                    int nY = a.getY() + revOri[1];
                    if (grid[nX][nY] == GameGlobal.EMPTY || grid[nX][nY] == grid[a.getX()][a.getY()]) {
                        leftLen -= 1;
                        a = new Point(nX, nY);
                    } else {
                        break;
                    }

                } else {
                    break;
                }
            }
        }
        if (leftLen <= 0) {
            return true;
        }
        return false;
    }

    public static long getGlobalScore(int[][] gridCopy, int flag, int rflag, int i, int j) {
        gridCopy[i][j] = flag;
        ArrayList<Line> flagLines = AIComputingL1.getPointsInLine(gridCopy, flag);
        ArrayList<Line> rflagLines = AIComputingL1.getPointsInLine(gridCopy, rflag);
        gridCopy[i][j] = GameGlobal.EMPTY;

        long tmpScore = 10;
        for (Line line : flagLines) {
            if (line.getLength() == GameGlobal.LEN5) {
                return Long.MAX_VALUE;
            }
            boolean can1 = canBeLine5(gridCopy, line, 1);
            boolean can2 = canBeLine5(gridCopy, line, 2);
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
            boolean can1 = canBeLine5(gridCopy, line, 1);
            boolean can2 = canBeLine5(gridCopy, line, 2);
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

    public static Point nextPoint(int[][] grid, int aiFlag, int humanFlag) {
        int width = grid[0].length;
        int height = grid.length;
        int[][] gridCopy = new int[width][height];

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

        for (int i=0; i< grid[0].length; ++i) {
            for (int j=0; j<grid.length; ++j) {
                gridCopy[i][j] = grid[i][j];
            }
        }

        int[] maxPoint = null;
        long maxScore = 0;
        for (int i=0; i<gridCopy[0].length; ++i) {
            for (int j=0; j<gridCopy.length; ++j) {
                if (gridCopy[i][j] == GameGlobal.EMPTY) {
                    long tmpScore = getGlobalScore(gridCopy, aiFlag, humanFlag, i, j);
                    if (tmpScore >= maxScore) {
                        maxScore = tmpScore;
                        maxPoint = new int[]{i, j};
                    }

                    tmpScore = getGlobalScore(gridCopy, humanFlag, aiFlag, i, j);
                    if (tmpScore > maxScore) {
                        maxScore = tmpScore;
                        maxPoint = new int[]{i, j};
                    }
                }
            }
        }
        if (maxPoint != null) {
            return new Point(maxPoint[0], maxPoint[1]);
        }
        if (aiPoint != null && aiPoint.getX() > 0 && aiPoint.getX() < width - 1 && aiPoint.getY() > 0 && aiPoint.getY() < height - 1) {
            for (int[] x : GameGlobal.Orientations) {
                if (gridCopy[aiPoint.getX() + x[0]][aiPoint.getY() + x[1]] == GameGlobal.EMPTY) {
                    return new Point(aiPoint.getX() + x[0], aiPoint.getY() + x[1]);
                }
            }
        }

        while (true) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            if (gridCopy[x][y] == GameGlobal.EMPTY) {
                return new Point(x, y);
            }
        }
    }
}
