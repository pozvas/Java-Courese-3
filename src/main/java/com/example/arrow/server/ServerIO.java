package com.example.arrow.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.example.arrow.Message;
import com.example.arrow.PositionsMessage;
import com.example.arrow.Point2DAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.geometry.Point2D;

public class ServerIO {
    private Socket clientSocket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Point2D.class, new Point2DAdapter())
            .create();
    public ServerIO(Socket soc){
        clientSocket = soc;
        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {

        }
    }
    public boolean SendToClient(Object o){
        try {
            String msg = gson.toJson(o);
            outputStream.writeUTF(msg);
            System.out.println(msg);
            return true;
        } catch (IOException e) {

        }
        return false;
    }
    public Message GetFromClient(){
        try{
            String msg = inputStream.readUTF();
            System.out.println(msg);
            return gson.fromJson(msg, Message.class);
        } catch (IOException e) {

        }
        return null;
    }
}
