package dz.chat.server.socketgroups;

import dz.chat.server.iothreads.InputSocketListener;
import dz.chat.server.iothreads.OutputSocketSender;
import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketGroup extends Thread {

    //Общая очередь сообщений для всех групп
    private static ConcurrentLinkedQueue <MessageUnit> broadcastMessages;

    private  final ConcurrentLinkedQueue <MessageUnit> groupMessages = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap <Socket, ClientUnit> clients = new ConcurrentHashMap<>();
    private final InputSocketListener inThread = new InputSocketListener(clients, groupMessages);
    private final OutputSocketSender outThread = new OutputSocketSender(clients, groupMessages);


    public SocketGroup(ConcurrentLinkedQueue<MessageUnit> broadcastQueue) {
        if (broadcastMessages == null) {
            broadcastMessages = broadcastQueue;
        }
        initGroup();
    }

    public SocketGroup() {
        initGroup();
    }

    private void initGroup () {
        //Тред, который принимает данные у группы клиентов
        inThread.setBroadcastQueue (broadcastMessages);
        inThread.start ();

        //И тред, который отправляет данные группе
        outThread.setBroadcastQueue (broadcastMessages);
        outThread.start ();
    }

    public int getSize () {
        return clients.size();
    }

    public void addSocket(Socket newSocket) {
        DataInputStream dis;
        DataOutputStream dos;

        try {
            dis = new DataInputStream(newSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Ошибка создания потока ввода для сокета " + newSocket.getInetAddress() +
                    ":" + newSocket.getPort());
            try {
                newSocket.close();
            } catch (Exception ex) {
                System.out.println("Ошибка закрытия сокета");
            }
            return; //Не получилось создать - бросаем сокет и работаем дальше
        }

        try {
            dos = new DataOutputStream(newSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Ошибка создания потока вывода для сокета " + newSocket.getInetAddress() +
                    ":" + newSocket.getPort());
            try {
                newSocket.close();
            } catch (Exception ex) {
                System.out.println("Ошибка закрытия сокета");
            }
            return; //Не получилось создать - бросаем сокет и работаем дальше
        }

        //Создаем клиента, изначально неаутентифицированного
        //TimeMillis - время начала подключения клиента
        ClientUnit cu = new ClientUnit(newSocket, dis, dos, System.currentTimeMillis());

        clients.put (newSocket, cu);
    }
}
