package com.momo.apple.wzq.common;

import java.util.ArrayList;

/**
 * 由点组成的直线对象
 * Created by apple on 2016/10/20.
 */

public class Line {
    public Line(Point a, Point b) {
        init(a, b);
    }

    public Line(Point a, Point b, int[][] grid) {
        init(a, b);

        this.canLeft5 = canBeLine5(grid, 1);
        this.canRight5 = canBeLine5(grid, 2);
    }

    public Point getA() {
        return A;
    }

    public Point getB() {
        return B;
    }

    public int getLength() {
        return length;
    }

    public int getDirection() {
        return direction;
    }

    public int[] getOrientation() {
        return ori;
    }

    public int[] getRevrtOrientation() { return revOri; }

    public boolean isValidPos(int[][] grid, Point a, int deltaX, int deltaY) {
        int width = grid[0].length;
        int height = grid.length;
        if (a.getX()+deltaX >= 0 && a.getX()+deltaX <width &&
                a.getY()+deltaY >= 0 && a.getY()+deltaY < height) {
            return true;
        }
        return false;
    }

    public boolean canBeLine5(int[][] grid, int dir) {
        int leftLen = GameGlobal.LEN5 - length;
        if (dir ==0 || dir == 1) {
            Point b = B;
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
            Point a = A;
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

    private void init(Point a, Point b) {
        this.canLeft5 = false;
        this.canRight5 = false;
        this.A = a;
        this.B = b;
        this.length = Math.abs(a.getY() - b.getY()) + 1;
        if (a.getX() == b.getX()) {
            this.direction = GameGlobal.DIR_VERTICAL;

        } else if (a.getY() == b.getY()) {
            this.direction = GameGlobal.DIR_HORIZONAL;
            this.length = Math.abs(a.getX() - b.getX()) + 1;
        } else {
            this.direction = GameGlobal.DIR_LEAN;
        }

        int x = a.getX() < b.getX()? 1 : (a.getX()==b.getX()? 0 : -1);
        int y = a.getY() < b.getY()? 1 : (a.getY()==b.getY()? 0 : -1);
        ori = new int[]{x,y};

        int rx = -x;
        int ry = -y;
        revOri = new int[]{rx, ry};
    }

    public void copy(Line src) {
        this.canLeft5 = src.canLeft5;
        this.canRight5 = src.canRight5;
        this.A = src.A;
        this.B = src.B;
        this.length = src.length;
        this.direction = src.direction;
        this.ori = src.ori;
        this.revOri = src.revOri;
    }

    public boolean isCanLeft5() {
        return canLeft5;
    }

    public boolean isCanRight5() {
        return canRight5;
    }

    public ArrayList<Point> linePoints() {
        ArrayList<Point> pl = new ArrayList<Point>();
        if (ori[0] != 0) {
            int j = A.getY();
            for (int i=A.getX(); i!=B.getX(); i+=ori[0]) {
                pl.add(new Point(i, j));
                j += ori[1];
            }
            pl.add(B);
        } else if (ori[1] != 0) {
            int i = A.getX();
            for (int j=A.getY(); j!=B.getY(); j+=ori[1]) {
                pl.add(new Point(i, j));
                i += ori[0];
            }
            pl.add(B);
        }
        return pl;
    }

    private boolean canLeft5;
    private  boolean canRight5;
    private Point A;
    private Point B;
    private int length;
    private int direction;
    private int[] ori;
    private int[] revOri;
}
