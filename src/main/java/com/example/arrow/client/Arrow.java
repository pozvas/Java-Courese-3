package com.example.arrow.client;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class Arrow extends Pane {
    private Line mainLine;
    private Line upLine;
    private Line downLine;

    public Arrow(double endX, double endY, double width, double height) {
        mainLine = new Line(endX - width, endY, endX, endY);
        upLine = new Line(endX - width / 5, endY - height, endX, endY);
        downLine = new Line(endX - width / 5, endY + height, endX, endY);
        super.getChildren().addAll(mainLine, upLine, downLine);
    }

    public void SetLayout(int n) {
        mainLine.setLayoutX(n);
        upLine.setLayoutX(n);
        downLine.setLayoutX(n);
    }
}

