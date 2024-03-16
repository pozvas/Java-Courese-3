package com.example.arrow;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class GameController {
    @FXML
    private Pane mainWindow;
    @FXML
    private Label label;
    @FXML
    private Label shoots;
    @FXML
    private Button pauseBtn;
    private Arrow arrow;
    private AimTarget target1;
    private AimTarget target2;
    private int score = 0;
    private int shootsCounter = 0;
    private boolean pauseSwitcher = true;
    private boolean gameStarted = false;
    private boolean shootProcessing = false;


    @FXML
    protected void StartButtonClick() {
        if (!gameStarted) {
            gameStarted = true;
            target1.flag = true;
            target2.flag = true;
            target1.StartGame();
            target2.StartGame();
        }
    }
    @FXML
    protected void StopButtonClick() {
        if (gameStarted) {
            if (!pauseSwitcher) {
                PauseButtonClick();
            }
            gameStarted = false;
            target1.flag = false;
            target2.flag = false;
            score = 0;
            shootsCounter = 0;
            Platform.runLater(() -> {
                label.setText(String.valueOf(score));
                shoots.setText(String.valueOf(shootsCounter));
            });
            if (arrow != null)
                arrow.flag = false;
        }
    }
    @FXML
    protected void PauseButtonClick() {
        if (gameStarted)
            if (pauseSwitcher) {
                target1.pause = true;
                target2.pause = true;
                if (arrow != null)
                    arrow.pause = true;
                Platform.runLater(() -> {
                    pauseBtn.setText("Продолжить");
                });
                pauseSwitcher = false;
            }
            else {
                synchronized (this) {
                    this.notifyAll();
                }
                Platform.runLater(() -> {
                    pauseBtn.setText("Пауза");
                });
                pauseSwitcher = true;
            }
    }
    @FXML
    protected void ShootButtonClick() {
        if (gameStarted && pauseSwitcher && !shootProcessing) {
            shootProcessing = true;
            shootsCounter++;
            arrow = new Arrow(10, 277, 20, 6);
            arrow.pauseObj = this;
            mainWindow.getChildren().add(arrow);
            new Thread(() -> {
                Platform.runLater(() -> {
                    shoots.setText(String.valueOf(shootsCounter));
                });
                score += arrow.Shoot(target1, target2);
                Platform.runLater(() -> {
                    label.setText(String.valueOf(score));

                    mainWindow.getChildren().remove(arrow);
                    arrow = null;
                });
                shootProcessing = false;
            }).start();
        }
    }
    @FXML
    public void initialize(){
        target1 = new AimTarget(500, 277, 60, 554);
        target2 = new AimTarget(700, 277, 30, 554);
        label.setText(String.valueOf(score));
        shoots.setText(String.valueOf(shootsCounter));
        target2.Timeout = 100;
        target1.pauseObj = this;
        target2.pauseObj = this;
        mainWindow.getChildren().add(target1);
        mainWindow.getChildren().add(target2);
    }

}