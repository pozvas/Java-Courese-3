package com.example.arrow.client;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class AimTarget extends Pane {
    private Line line;
    private Circle circle;
    public AimTarget(double centX, double centY, double width, double height){
        line = new Line(centX, centY - height / 2, centX, centY + height / 2);
        circle = new Circle(centX, centY, width / 2);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.WHITE);
        super.getChildren().addAll(line, circle);
    }
    public void SetLayout(int n){
        circle.setLayoutY(n);
    }
}
