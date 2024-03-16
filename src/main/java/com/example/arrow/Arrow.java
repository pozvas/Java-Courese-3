package com.example.arrow;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class Arrow extends Pane {
    private Line mainLine;
    private Line upLine;
    private Line downLine;
    public boolean flag = true;
    private int points = 0;
    public Object pauseObj;
    public boolean pause = false;

    public Arrow(double endX, double endY, double width, double height) {
        mainLine = new Line(endX - width, endY, endX, endY);
        upLine = new Line(endX - width / 5, endY - height, endX, endY);
        downLine = new Line(endX - width / 5, endY + height, endX, endY);
        super.getChildren().addAll(mainLine, upLine, downLine);
    }

    public int Shoot(AimTarget first, AimTarget second) {

        while (flag) {
            if (pause) {
                synchronized (pauseObj) {
                    try {
                        pauseObj.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            pause = false;
            Platform.runLater(() -> {
                mainLine.setLayoutX(mainLine.getLayoutX() + 10);
                upLine.setLayoutX(upLine.getLayoutX() + 10);
                downLine.setLayoutX(downLine.getLayoutX() + 10);

                Point2D arrowP = new Point2D(mainLine.getEndX() + mainLine.getLayoutX(), mainLine.getEndY());
                Point2D circle1P = new Point2D(first.GetCircle().getCenterX(), first.GetCircle().getCenterY() + first.GetCircle().getLayoutY());
                Point2D circle2P = new Point2D(second.GetCircle().getCenterX(), second.GetCircle().getCenterY()+ second.GetCircle().getLayoutY());

                if (arrowP.distance(circle1P) < first.GetCircle().getRadius()){
                    points = 1;
                    flag = false;
                }
                if (arrowP.distance(circle2P) < second.GetCircle().getRadius()){
                    points = 2;
                    flag = false;
                }
                if (arrowP.getX() >= ((Pane)super.getParent()).getWidth()){
                    points = 0;
                    flag = false;
                }
            });
            try {

                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return points;
    }
}

