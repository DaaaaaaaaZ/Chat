package dz.chat.server.iothreads;

import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutputSocketSender extends Thread {
    private final ConcurrentHashMap<Socket, ClientUnit> clients;
    private final ConcurrentLinkedQueue<MessageUnit> groupMessages;
    private static ConcurrentLinkedQueue<MessageUnit> broadcastQueue;

    public OutputSocketSender(ConcurrentHashMap<Socket, ClientUnit> clients,
                              ConcurrentLinkedQueue<MessageUnit> groupMessages,
                              ConcurrentLinkedQueue<MessageUnit> broadcastMessagesQueue) {
        this.clients = clients;
        this.groupMessages = groupMessages;
        if (broadcastQueue != null) {
            broadcastQueue = broadcastMessagesQueue;
        }
    }

    @Override
    public void run() {
        StringBuilder msgOut = new StringBuilder();
        ClientUnit cu = null;
        MessageUnit mu = null;
        while (true) {
            msgOut.setLength(0);
            try {
                if (!groupMessages.isEmpty()) {
                    mu = groupMessages.poll();
                    switch (mu.getType()) {
                        case BROADCAST: {
                            cu = mu.getSource();
                            for (Map.Entry<Socket, ClientUnit> pair : clients.entrySet()) {
                                DataOutputStream dos = pair.getValue().getOut();
                                //msgOut.append(cu.getNick()); ToDo Исправить
                                msgOut.append("Юзверь");
                                msgOut.append(": ");
                                msgOut.append(mu.getMessage());
                                dos.writeUTF(msgOut.toString());
                            }
                            mu.reset(); //Обнуление модели сообщения
                        } break;
                        case NEED_AUTH: {
                            cu = mu.getSource();
                            cu.getOut().writeUTF(mu.getMessage());
                            mu.reset();
                        } break;
                    }
                }
            } catch (IOException e) {
                if (cu != null) {
                    System.out.println("Ошибка при отправке " + cu.getInfoString());
                } else {
                    System.out.println("Ошибка при отправке сообщения");
                }
            } finally {
                if (mu != null && mu.isUsing()) {
                    mu.reset();
                }
            }
        }
    }
}
