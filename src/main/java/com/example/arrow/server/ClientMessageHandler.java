package com.example.arrow.server;

import com.example.arrow.Action;
import com.example.arrow.Message;
import com.example.arrow.PositionsMessage;
import com.example.arrow.Positions;

import java.net.Socket;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

class ClientMessageHandler implements Runnable {
    private PlayerData player;
    private Socket playerSocket;
    private Positions figures;
    private Phaser phaser;
    private ServerIO io;
    private Semaphore pauseSem;
    public ClientMessageHandler(PlayerData p, Socket s, Positions f, Phaser ph, Semaphore pau, String name){
        playerSocket = s;
        figures = f;
        player = p;
        phaser = ph;
        phaser.register();
        pauseSem = pau;
        io = new ServerIO(playerSocket);
        synchronized (player) {
            player.name = name;
            player.notifyAll();
        }
        PositionsMessage resp = new PositionsMessage(Action.POSITIONS, figures);
        io.SendToClient(resp);

    }
    @Override
    public void run() {
        while (true) {
            Message msg = io.GetFromClient();
            if(msg != null) {
                switch (msg.getAct()) {
                    case CONNECTION -> {


                    }
                    case READY -> {
                        phaser.arrive();
                    }
                    case POSITIONS -> {
                        io.SendToClient(new PositionsMessage(Action.POSITIONS, figures));
                    }
                    case SHOOT -> {
                        player.arrowLayout = 0;
                        player.ShootsCount++;
                    }
                    case PAUSE -> {
                        try {
                            pauseSem.acquire();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case UNPAUSE -> {
                        pauseSem.release();
                    }
                    case CLIENTCLOSE -> {
                        player.isDisconnected = true;
                        synchronized (figures) {
                            figures.notifyAll();
                        }
                        return;
                    }
                }
            }
        }
    }
}
