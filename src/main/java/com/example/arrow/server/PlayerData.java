package com.example.arrow.server;

import javafx.scene.paint.Color;

import java.net.Socket;
import java.util.Random;

public class PlayerData {
    public int number;
    public String name;
    public int Score = 0;
    public int ShootsCount = 0;
    public int position = 0;
    public int arrowLayout = -1;
    public boolean isWinner = false;
    public boolean isDisconnected = false;
    public int r;
    public int g;
    public int b;

    public PlayerData(int num) {
        Random random = new Random();
        this.number = num;
        r = random.nextInt(0, 255);
        g = random.nextInt(0, 255);
        b = random.nextInt(0, 255);
    }

}
