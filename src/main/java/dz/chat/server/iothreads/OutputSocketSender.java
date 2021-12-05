package dz.chat.server.iothreads;

import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutputSocketSender extends Thread {
    private final ConcurrentHashMap<Socket, ClientUnit> clients;
    private final ConcurrentLinkedQueue<MessageUnit> groupMessages;
    private static ConcurrentLinkedQueue<MessageUnit> broadcastQueue;

    public OutputSocketSender(ConcurrentHashMap<Socket, ClientUnit> clients,
                              ConcurrentLinkedQueue<MessageUnit> groupMessages) {
        this.clients = clients;
        this.groupMessages = groupMessages;
    }

    public void setBroadcastQueue(ConcurrentLinkedQueue<MessageUnit> broadcastMessagesQueue) {
        if (broadcastQueue != null) {
            broadcastQueue = broadcastMessagesQueue;
        }
    }
}
