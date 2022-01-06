package dz.chat.server;

import dz.chat.server.managers.SocketGroupManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int SERVER_PORT = 7777;
    public static final long AUTH_TIMEOUT = 120000;
    public static final long IDLE_TIMEOUT = 1800000;
    private final SocketGroupManager sgm;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new Server ().startServer();
    }

    private Server () {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            System.out.println("Сервер не запускается");
            System.exit(-1);
        }

        sgm = new SocketGroupManager ();
    }

    private void startServer() {
        try {
            while (true) {
                sgm.addSocket(serverSocket.accept());
            }
        } catch (IOException e) {
            System.out.println("Работа сервера нарушена");
            e.printStackTrace();
        } finally {
            sgm.dispose ();
        }
    }
}