package com.example.arrow;

import com.example.arrow.server.PlayerData;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Positions {
    private Point2D smallCircle;
    private int layoutSmall = 0;
    private Point2D bigCircle;
    private int layoutBig = 0;
    public ArrayList<PlayerData> players;
    public boolean isPaused = false;
    public Positions(){
        this.smallCircle = new Point2D(500, 277);
        this.bigCircle = new Point2D(700, 277);
    }
    public Positions(Point2D smallCircle, Point2D bigCircle) {
        this.smallCircle = smallCircle;
        this.bigCircle = bigCircle;
    }

    public Point2D getSmallCircle() {
        return smallCircle;
    }

    public void setSmallCircle(Point2D smallCircle) {
        this.smallCircle = smallCircle;
    }

    public Point2D getBigCircle() {
        return bigCircle;
    }

    public void setBigCircle(Point2D bigCircle) {
        this.bigCircle = bigCircle;
    }
    public int getLayoutSmall() {
        return layoutSmall;
    }

    public void setLayoutSmall(int layoutSmall) {
        this.layoutSmall = layoutSmall;
    }

    public int getLayoutBig() {
        return layoutBig;
    }

    public void setLayoutBig(int layoutBig) {
        this.layoutBig = layoutBig;
    }
}
