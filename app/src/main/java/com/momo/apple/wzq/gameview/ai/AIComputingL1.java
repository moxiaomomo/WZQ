package com.momo.apple.wzq.gameview.ai;

import com.momo.apple.wzq.common.Line;
import com.momo.apple.wzq.common.Point;
import com.momo.apple.wzq.common.GameGlobal;

import java.util.ArrayList;

/**
 * Created by apple on 2016/10/20.
 */

public class AIComputingL1 {
    public static int calSlope(Point start, Point end) {
        if (start.getX() == end.getX()) {
            return -2;
        }
        return (int) ((end.getY() - start.getY()) * 1000.0 / (end.getX() - start.getX()));
    }

    public static boolean isPointInLine(Line line, Point p) {
        Point start = line.getA();
        if (start.getX() == p.getX() && start.getY()==p.getY()) {
            start = line.getB();
        }
        Point A = line.getA();
        Point B = line.getB();
        int x0 = A.getX() < B.getX() ? A.getX() : B.getX();
        int y0 = A.getY() < B.getY() ? A.getY() : B.getY();
        int x1 = (x0 == A.getX()) ? B.getX() : A.getX();
        int y1 = (y0 == A.getY()) ? B.getY() : A.getY();
        int s1 = Math.abs(calSlope(start, p));
        int s2 = Math.abs(calSlope(start, p));
        if (s1==s2 && p.getX()>=x0 && p.getX()<=x1 && p.getY()>=y0 && p.getY()<=y1) {
            return true;
        }
        return false;
    }

    public static boolean isInOneLine(int grid[][], Point start, Point end) {
        int x0 = start.getX() < end.getX() ? start.getX() : end.getX();
        int y0 = start.getY() < end.getY() ? start.getY() : end.getY();
        int x1 = (x0 == start.getX()) ? end.getX() : start.getX();
        int y1 = (y0 == start.getY()) ? end.getY() : start.getY();

        int slope = calSlope(start, end);

        int value = grid[start.getX()][start.getY()];
        for (int i = x0; i <= x1; i++) {
            for (int j = y0; j <= y1; ++j) {
                if (calSlope(start, new Point(i, j)) != slope) {
                    continue;
                }
                if (grid[i][j] != value) {
                    return false;
                }
            }
        }
        return true;
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
                            list.add(new Line(new Point(i, j), new Point(i, j + l)));
                            break;
                        }
                    }
                }
                if (i == 0 || i > 0 && grid[i - 1][j] != flag) {
                    for (int l = 4; l >= 1; l--) {
                        if (i + l < width && isInOneLine(grid, new Point(i, j), new Point(i + l, j))) {
                            list.add(new Line(new Point(i, j), new Point(i + l, j)));
                            break;
                        }
                    }
                }
                if (i == 0 || j == 0 || (i > 0 && j > 0 && grid[i - 1][j - 1] != flag)) {
                    for (int l = 4; l >= 1; l--) {
                        if (i + l < width && j + l < height && isInOneLine(grid, new Point(i, j), new Point(i + l, j + l))) {
                            list.add(new Line(new Point(i, j), new Point(i + l, j + l)));
                            break;
                        }
                    }
                }
                if (i == 0 || j == height - 1 || (i > 0 && j < height - 1 && grid[i - 1][j + 1] != flag)) {
                    for (int l = 4; l >= 1; l--) {
                        if (i + l < width && j - l >= 0 && isInOneLine(grid, new Point(i, j), new Point(i + l, j - l))) {
                            list.add(new Line(new Point(i, j), new Point(i + l, j - l)));
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }

    public static Point genPointForLines(int[][] grid, ArrayList<Line> lines, int specifiedLen) {
        for (Line line : lines) {
            if (line.getLength() == specifiedLen) {
                Point N = genPointForLine(grid, line);
                if (N != null) {
                    return N;
                }
            }
        }
        return null;
    }

    public static Point genPointForLine(int[][] grid, Line line) {
        int width = grid[0].length;
        int height = grid.length;
        Point A = line.getA();
        Point B = line.getB();
        int dir = line.getDirection();

        if (dir == GameGlobal.DIR_HORIZONAL) {
            if (A.getX() > 0 && grid[A.getX() - 1][A.getY()] == GameGlobal.EMPTY) {
                return new Point(A.getX() - 1, A.getY());
            } else if (B.getX() < width - 1 && grid[B.getX() + 1][B.getY()] == GameGlobal.EMPTY) {
                return new Point(B.getX() + 1, B.getY());
            }
        } else if (dir == GameGlobal.DIR_VERTICAL) {
            if (A.getY() > 0 && grid[A.getX()][A.getY() - 1] == GameGlobal.EMPTY) {
                return new Point(A.getX(), A.getY() - 1);
            } else if (B.getY() < height - 1 && grid[B.getX()][B.getY() + 1] == GameGlobal.EMPTY) {
                return new Point(B.getX(), B.getY() + 1);
            }
        } else if (A.getY() < B.getY()) {
            if (A.getX() > 0 && A.getY() > 0 && grid[A.getX() - 1][A.getY() - 1] == GameGlobal.EMPTY) {
                return new Point(A.getX() - 1, A.getY() - 1);
            } else if (B.getX() < width - 1 && B.getY() < height - 1 && grid[B.getX() + 1][B.getY() + 1] == GameGlobal.EMPTY) {
                return new Point(B.getX() + 1, B.getY() + 1);
            }
        } else {
            if (A.getX() > 0 && A.getY() < height - 1 && grid[A.getX() - 1][A.getY() + 1] == GameGlobal.EMPTY) {
                return new Point(A.getX() - 1, A.getY() + 1);
            } else if (B.getX() < width - 1 && B.getY() > 0 && grid[B.getX() + 1][B.getY() - 1] == GameGlobal.EMPTY) {
                return new Point(B.getX() + 1, B.getY() - 1);
            }
        }
        return null;
    }

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

        Point point = new Point();
        ArrayList<Line> lines = getPointsInLine(grid, aiFlag);
        ArrayList<Line> hmlines = getPointsInLine(grid, humanFlag);

        Point NAI = genPointForLines(grid, lines, GameGlobal.LEN5);
        if (NAI != null) {
            return NAI;
        }
        Point N = genPointForLines(grid, hmlines, GameGlobal.LEN5);
        if (N != null) {
            return N;
        }

        NAI = genPointForLines(grid, lines, GameGlobal.LEN4);
        if (NAI != null) {
            return NAI;
        }
        N = genPointForLines(grid, hmlines, GameGlobal.LEN4);
        if (N != null) {
            return N;
        }

        N = genPointForLines(grid, hmlines, GameGlobal.LEN3);
        if (N != null) {
            return N;
        }
        NAI = genPointForLines(grid, lines, GameGlobal.LEN3);
        if (NAI != null) {
            return NAI;
        }

        N = genPointForLines(grid, hmlines, GameGlobal.LEN2);
        if (N != null) {
            return N;
        }
        NAI = genPointForLines(grid, lines, GameGlobal.LEN2);
        if (NAI != null) {
            return NAI;
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
                point.setX(x);
                point.setY(y);
                return point;
            }
        }
    }
}
