package com.example.arrow.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    private static SocketServer serv = null;
    private InetAddress ip = null;
    private int port = 54324;
    private ServerSocket serverSocket = null;


    private SocketServer() {}

    public static SocketServer GetServer(){
        if (serv == null) {
            serv = new SocketServer();
        }
        return serv;
    }
    public void Start(){
        try {
            ip = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(port, 0, ip);
            System.out.println("Server start\n");
        } catch (IOException ex) {
            System.out.println("Error");
        }
    }
    public Socket AcceptClient(){
        try {
            Socket buf = serverSocket.accept();
            System.out.println("+Client");
            return buf;

        } catch (IOException e ){

        }
        return null;
    }
    public void CloseConnection(Socket soc){
        try {
            soc.close();

        } catch (IOException e){

        }
    }
}
