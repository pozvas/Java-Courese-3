package com.example.arrow;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class AimTarget extends Pane {
    private Line line;
    private Circle circle;
    public boolean flag = true;
    private int cof = 1;
    public int Timeout = 200;
    public Object pauseObj;
    public boolean pause = false;
    public AimTarget(double centX, double centY, double width, double height){
        line = new Line(centX, centY - height / 2, centX, centY + height / 2);
        circle = new Circle(centX, centY, width / 2);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.WHITE);
        super.getChildren().addAll(line, circle);
    }
    public void StartGame(){
        new Thread(() -> {
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
                    circle.setLayoutY(circle.getLayoutY() + cof * 10);
                    if (circle.getLayoutY() > ((Pane) super.getParent()).getHeight() / 2 - 1.5 * circle.getRadius() ||
                            circle.getLayoutY() < 1.5 * circle.getRadius() - ((Pane) super.getParent()).getHeight() / 2)  {
                        //circle.setLayoutY(2 * circle.getRadius() - super.getHeight() / 2);
                        cof *= -1;
                    }

                });
                try {
                    Thread.sleep(Timeout);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Platform.runLater(() -> {
                circle.setLayoutY(0);
            });

        }).start();
    }

    public Circle GetCircle() {
        return circle;
    }
}
