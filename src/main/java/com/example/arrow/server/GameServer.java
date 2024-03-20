package com.example.arrow.server;

import com.example.arrow.Action;
import com.example.arrow.Message;
import com.example.arrow.Positions;
import com.example.arrow.PositionsMessage;
import javafx.geometry.Point2D;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

public class GameServer {
    private static GameServer gameServer = null;
    private SocketServer socketServer = null;
    private ArrayList<Socket> sockets = new ArrayList<>();
    private int maxPlayers = 4;
    private Positions figures;
    private Phaser startGame = null;
    private boolean gameEnd = false;
    private boolean isGameStarted = false;
    private boolean isFirst = true;
    private Semaphore pasuseSem = new Semaphore(1);
    private Semaphore connectionSem = new Semaphore(1);

    private GameServer(){
        socketServer = SocketServer.GetServer();
        figures = new Positions();
        figures.players = new ArrayList<>();
    }
    public static GameServer GetGameServer(){

        if (gameServer == null){
            gameServer = new GameServer();
        }
        return gameServer;
    }

    public void Progress(){
        socketServer.Start();
        new Thread(() -> {
            while (true) {
                synchronized (figures) {
                    try {
                        figures.wait();
                        connectionSem.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int delCount = 0;
                    for (int i = 0; i < figures.players.size(); i++) {
                        if (figures.players.get(i).isDisconnected) {
                            sockets.remove(figures.players.indexOf(figures.players.get(i)));
                            figures.players.remove(figures.players.get(i));
                            i = 0;
                            delCount++;

                        }
                        if (delCount != 0){
                            figures.players.get(i).number = i;
                            figures.players.get(i).position = 111 * (figures.players.get(i).number + 1);
                        }
                    }
                    while (delCount != 0){
                        startGame.arrive();
                        delCount--;
                    }
                    for (Socket s : sockets) {
                        ServerIO io = new ServerIO(s);
                        io.SendToClient(new PositionsMessage(Action.NEWPLAYER, figures));
                    }
                    connectionSem.release();
                }
            }
        }).start();
        while (true) {

            Socket buf = socketServer.AcceptClient();
            ServerIO check = new ServerIO(buf);
            Message m = check.GetFromClient();
            boolean isChecked = true;
            for(PlayerData p : figures.players){
                if (Objects.equals(p.name, m.data)){

                    isChecked = false;
                    break;
                }
            }
            if (!isChecked){
                check.SendToClient(new Message(Action.SAMENAME, "Имя занято"));
                socketServer.CloseConnection(buf);
                continue;
            } else if (figures.players.size() >= maxPlayers || isGameStarted) {
                check.SendToClient(new PositionsMessage(Action.FULL, null));
                socketServer.CloseConnection(buf);
                check = null;
                continue;
            }
            else {
                check.SendToClient(new Message(Action.NICENAME, ""));
            }
            check = null;
            try {
                connectionSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            figures.players.add(new PlayerData(figures.players.size()));
            figures.players.getLast().position = 111 * (figures.players.getLast().number + 1);
            if (isFirst)
                startGame = new Phaser(1);
            new Thread(new ClientMessageHandler(figures.players.getLast(), buf, figures, startGame, pasuseSem, m.data)).start();
            if (isFirst) {
                new Thread(() -> {
                    this.Game();
                }).start();
                isFirst = false;
            }

            /*synchronized (figures.players.getLast()) {
                try {
                    figures.players.getLast().wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }*/
            for (Socket s : sockets) {
                ServerIO io = new ServerIO(s);
                io.SendToClient(new PositionsMessage(Action.NEWPLAYER, figures));
            }
            sockets.add(buf);
            connectionSem.release();

        }
    }
    private void Game(){
        while (true) {
            startGame.arriveAndAwaitAdvance();
            for (PlayerData p: figures.players){
                p.isWinner = false;
                p.ShootsCount = 0;
                p.Score = 0;
            }
            isGameStarted = true;
            gameEnd = false;
            for (Socket s: sockets){
                ServerIO io = new ServerIO(s);
                io.SendToClient(new PositionsMessage(Action.GAMESTART, null));
            }
            int coefSmall = 1, coefBig = 1;
            int loopSmall = 0, loopBig = 0;
            while (!gameEnd) {
                if (!pasuseSem.tryAcquire()) {
                    try {
                        figures.isPaused = true;
                        pasuseSem.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    pasuseSem.release();
                    figures.isPaused = false;
                    for (Socket s: sockets){
                        ServerIO io = new ServerIO(s);
                        io.SendToClient(new Message(Action.UNPAUSE, ""));
                    }
                }
                else {
                    pasuseSem.release();
                }
                for (PlayerData i : figures.players) {
                    if (i.arrowLayout != -1){
                        i.arrowLayout += 10;
                        Point2D arrowP = new Point2D(i.arrowLayout, i.position);
                        Point2D circle1P = new Point2D(figures.getBigCircle().getX(), figures.getBigCircle().getY() + figures.getLayoutBig());
                        Point2D circle2P = new Point2D(figures.getSmallCircle().getX(), figures.getSmallCircle().getY() + figures.getLayoutSmall());

                        if (arrowP.distance(circle1P) < 23){
                            i.Score += 1;
                            i.arrowLayout = -1;
                        }
                        if (arrowP.distance(circle2P) < 12){
                            i.Score += 2;
                            i.arrowLayout = -1;
                        }
                        if (arrowP.getX() >= 826){
                            i.arrowLayout = -1;
                        }
                    }
                    if (i.Score >= 6) {
                        gameEnd = true;
                        i.isWinner = true;
                        break;
                    }
                }
                if (loopSmall == 10) {
                    figures.setLayoutSmall(figures.getLayoutSmall() + coefSmall * 10);
                    loopSmall = 0;
                    if (figures.getLayoutSmall() >= 262 || figures.getLayoutSmall() <= -262){
                        coefSmall *= -1;
                    }
                }
                if (loopBig == 20) {
                    figures.setLayoutBig(figures.getLayoutBig() + coefBig * 10);
                    loopBig = 0;
                    if (figures.getLayoutBig() >= 247 || figures.getLayoutBig() <= -247){
                        coefBig *= -1;
                    }
                }
                loopBig++;
                loopSmall++;
                if (gameEnd) {
                    figures.setLayoutBig(0);
                    figures.setLayoutSmall(0);
                    for (PlayerData p : figures.players){
                        p.arrowLayout = -1;
                    }
                    isGameStarted = false;
                    synchronized (figures) {
                        figures.notifyAll();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}


