package com.example.arrow.client;

import com.example.arrow.Action;
import com.example.arrow.Message;
import com.example.arrow.PositionsMessage;
import com.example.arrow.Positions;
import com.example.arrow.server.PlayerData;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.SplittableRandom;

public class GameController {
    private Client client = Client.GetClient();
    @FXML
    private Pane mainWindow;
    @FXML
    private Button pauseBtn;
    @FXML
    private Pane Players;
    @FXML
    private Pane labelPane;
    private ArrayList<Polygon> writePlayers = new ArrayList<>();
    private ArrayList<Label[]> labelPlayers = new ArrayList<>();
    private String name;
    private AimTarget target1;
    private AimTarget target2;
    private boolean pauseSwitcher = true;
    private boolean gameStarted = false;
    private boolean shootProcessing = false;
    private int nextLabelY = 0;
    private String nameWinner;


    @FXML
    protected void StartButtonClick() {
        if (!gameStarted) {
            gameStarted = true;
            client.SendToServer(new Message(Action.READY, ""));
        }
    }
    @FXML
    protected void PauseButtonClick() {

        if (gameStarted)
            if (pauseSwitcher) {
                client.SendToServer(new Message(Action.PAUSE, ""));
                Platform.runLater(() -> {
                    pauseBtn.setText("Продолжить");
                });
                pauseSwitcher = false;
            }
            else {
                client.SendToServer(new Message(Action.UNPAUSE, ""));
                Platform.runLater(() -> {
                    pauseBtn.setText("Пауза");
                });
                pauseSwitcher = true;
            }
    }
    @FXML
    protected void ShootButtonClick() {

        if (gameStarted && pauseSwitcher && !shootProcessing) {
            client.SendToServer(new Message(Action.SHOOT, ""));
        }
    }
    @FXML
    public void initialize(){
        Message m = null;
        do {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Подключение");
            dialog.setHeaderText("Подключение к серверу");
            dialog.setContentText("Введите имя игрока (имя должно быть уникальным)");

            Optional<String> name = dialog.showAndWait();
            if(name.isEmpty()) return;
            this.name = name.get();
            client.Connect();
            client.SendToServer(new Message(Action.CONNECTION, this.name));
            m = client.GetMessageFromServer();
            if (m.act == Action.FULL){
                Alert full = new Alert(Alert.AlertType.INFORMATION);
                full.setTitle("Нет мест");
                full.setHeaderText("Все места в игре заняты");
                full.setContentText("Попрабуйте подключиться позднее");
                full.showAndWait();
            }
            if (m.act == Action.SAMENAME){
                Alert samename = new Alert(Alert.AlertType.INFORMATION);
                samename.setTitle("Имя занято");
                samename.setHeaderText("Имя занято");
                samename.setContentText("Выберите другое имя");
                samename.showAndWait();
            }
        } while (m == null || m.act == Action.NICENAME || m.act == Action.FULL);
        PositionsMessage msg = client.GetPositionFromServer();
        if (msg == null){
            return;
        }
        Positions d = msg.getData();
        target1 = new AimTarget(d.getBigCircle().getX(), d.getBigCircle().getY(), 60, 554);
        target2 = new AimTarget(d.getSmallCircle().getX(), d.getSmallCircle().getY(), 30, 554);
        for (PlayerData p: d.players) {
            Polygon q = new Polygon();
            q.getPoints().addAll(new Double[]{
                    73d, (double) p.position,
                    0d, (double) (p.position + 40),
                    0d, (double) (p.position - 40)
            });
            q.setFill(Color.rgb(p.r, p.g, p.b));
            writePlayers.add(q);
            Players.getChildren().add(q);

            Label l1 = new Label("Игрок " + p.name);
            l1.setLayoutY(nextLabelY);
            nextLabelY += 10;
            labelPane.getChildren().add(l1);
            Label l2 = new Label("Выстрелов " + p.ShootsCount);
            l2.setLayoutY(nextLabelY);
            nextLabelY += 10;
            labelPane.getChildren().add(l2);
            Label l3 = new Label("Очков " + p.Score);
            l3.setLayoutY(nextLabelY);
            nextLabelY += 10;
            labelPane.getChildren().add(l3);
            labelPlayers.add(new Label[]{l1, l2, l3});
        }

        mainWindow.getChildren().add(target1);
        mainWindow.getChildren().add(target2);
        WaitForNewPlayer();
    }
    private void Reload(Positions d){
        Platform.runLater(() -> {
            for(Polygon p: writePlayers){
                Players.getChildren().remove(p);
            }
            writePlayers.clear();
            for(Label[] l: labelPlayers){
                labelPane.getChildren().remove(l[0]);
                labelPane.getChildren().remove(l[1]);
                labelPane.getChildren().remove(l[2]);
            }
            labelPlayers.clear();
            nextLabelY = 0;
            for (PlayerData p: d.players){
                Polygon q = new Polygon();
                q.getPoints().addAll(new Double[]{
                        73d, (double) p.position,
                        0d, (double) (p.position + 40),
                        0d, (double) (p.position - 40)
                });
                q.setFill(Color.rgb(p.r, p.g, p.b));
                writePlayers.add(q);
                Players.getChildren().add(q);
                Label l1 = new Label("Игрок " + p.name);
                l1.setLayoutY(nextLabelY);
                nextLabelY+= 10;
                labelPane.getChildren().add(l1);
                Label l2 = new Label("Выстрелов " + p.ShootsCount);
                l2.setLayoutY(nextLabelY);
                nextLabelY+= 10;
                labelPane.getChildren().add(l2);
                Label l3 = new Label("Очков " + p.Score);
                l3.setLayoutY(nextLabelY);
                nextLabelY+= 10;
                labelPane.getChildren().add(l3);
                labelPlayers.add(new Label[]{l1, l2, l3});
            }
        });

    }
    private void WaitForNewPlayer(){
        new Thread(() -> {
            Arrow[] arrows = {null, null, null, null};
            while (true) {
                PositionsMessage msg1 = client.GetPositionFromServer();
                if(msg1 == null) break;
                if (msg1.act == Action.NEWPLAYER) {
                    Reload(msg1.data);
                } else if (msg1.act == Action.GAMESTART) {
                    while (gameStarted) {
                        client.SendToServer(new Message(Action.POSITIONS, ""));
                        PositionsMessage msg = client.GetPositionFromServer();
                        if (msg.data.isPaused){
                            Message m = client.GetMessageFromServer();
                        }
                        Platform.runLater(() -> {
                            target1.SetLayout(msg.data.getLayoutBig());
                            target2.SetLayout(msg.data.getLayoutSmall());

                            for (PlayerData p : msg.data.players) {
                                if (p.isWinner){
                                    gameStarted = false;
                                    nameWinner = p.name;

                                }
                                labelPlayers.get(msg.data.players.indexOf(p))[2].setText("Очков " + p.Score);
                                labelPlayers.get(msg.data.players.indexOf(p))[1].setText("Выстрелов " + p.ShootsCount);
                                if (p.arrowLayout != -1) {
                                    if (arrows[p.number] == null) {
                                        arrows[p.number] = new Arrow(10, p.position, 20, 6);
                                        mainWindow.getChildren().add(arrows[p.number]);
                                    }
                                    arrows[p.number].SetLayout(p.arrowLayout);
                                } else {
                                    if (arrows[p.number] != null) {
                                        mainWindow.getChildren().remove(arrows[p.number]);
                                        arrows[p.number] = null;
                                    }
                                }
                            }
                        });
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Platform.runLater(() -> {
                        Alert winner = new Alert(Alert.AlertType.INFORMATION);
                        winner.setTitle("Конец игры");
                        winner.setHeaderText("Игра окончена");
                        winner.setContentText("Победитель " + nameWinner);
                        winner.showAndWait();
                    });

                }
            }
        }).start();
    }

    public EventHandler<WindowEvent> onClose(){
        return new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                client.SendToServer(new Message(Action.CLIENTCLOSE, ""));
                client.Close();
            }
        };
    }
}