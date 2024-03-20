package com.example.arrow.client;

import com.example.arrow.Message;
import com.example.arrow.PositionsMessage;
import com.example.arrow.Point2DAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.geometry.Point2D;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    final private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Point2D.class, new Point2DAdapter())
            .create();
    private static Client client = null;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private InetAddress serverIp = null;
    private int port = 54324;
    private Client(){}
    public static Client GetClient(){
        if (client == null){
            client = new Client();
        }
        return client;
    }
    public void Connect(){
        try {
            serverIp = InetAddress.getLocalHost();
            socket = new Socket(serverIp, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e){

        }
    }
    public void Close(){
        if(outputStream == null) return;
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void SendToServer(Message o){
        if(outputStream == null) return;
        try {
            String msg = gson.toJson(o);
            System.out.println(msg);
            outputStream.writeUTF(msg);
        } catch (IOException e){
            Thread.currentThread().interrupt();
        }
    }
    public PositionsMessage GetPositionFromServer(){
        try{
            String msg = inputStream.readUTF();
            System.out.println(msg);
            return gson.fromJson(msg, PositionsMessage.class);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }
    public Message GetMessageFromServer(){
        try{
            String msg = inputStream.readUTF();
            System.out.println(msg);
            return gson.fromJson(msg, Message.class);
        }
        catch (IOException e) {

        }

        return null;
    }

}
