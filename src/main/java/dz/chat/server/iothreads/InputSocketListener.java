package dz.chat.server.iothreads;

import dz.chat.server.Server;
import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputSocketListener extends Thread {
    private final ConcurrentHashMap<Socket, ClientUnit> clients;
    private final ConcurrentLinkedQueue<MessageUnit> groupMessages;
    private static ConcurrentLinkedQueue<MessageUnit> broadcastQueue;

    public InputSocketListener(ConcurrentHashMap<Socket, ClientUnit> clients,
                               ConcurrentLinkedQueue<MessageUnit> groupMessages) {
        this.clients = clients;
        this.groupMessages = groupMessages;
    }

    @Override
    public void run() {
        while (true) {
            for (Map.Entry<Socket, ClientUnit> client : clients.entrySet()) {
                try {
                    ClientUnit cu =client.getValue();
                    if (cu.getIn().available() > 0) {
                        processMessage (cu);
                    } else if (System.currentTimeMillis() - cu.getLastMessageTime() > Server.IDLE_TIMEOUT) {
                        disconnectClient(cu); //Отключаем не отвечающего клиента
                    }
                } catch (RuntimeException e) {
                    continue;
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    private void processMessage(ClientUnit clientUnit) {
        String msgIn;
        try {
            if (clientUnit.isAuthOK()) {
                msgIn = clientUnit.getIn().readUTF();
                clientUnit.setLastMessageTime(System.currentTimeMillis());
                //ToDo распарсить строку
            } else {
                if (System.currentTimeMillis() - clientUnit.getConnectionTime() > Server.AUTH_TIMEOUT) {
                    disconnectClient(clientUnit);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(clientUnit.getInfoString() + " - ошибка чтения");
        }
    }

    private void disconnectClient(ClientUnit clientUnit) throws RuntimeException {
        try {
            clients.remove(clientUnit.getSocket());
        } catch (Exception e) {
            System.out.println("Ошибка удаления клиента " + clientUnit.getInfoString() + " из группы");
        }

        try {
            if (clientUnit.getIn() != null) {
                clientUnit.getIn().close ();
            }
        } catch (IOException e) {
            System.out.println("Ошибка закрытия ввода у сокета " + clientUnit.getInfoString());
        }

        try {
            if (clientUnit.getOut() != null) {
                clientUnit.getOut().close ();
            }
        } catch (IOException e) {
            System.out.println("Ошибка закрытия вывода у сокета " + clientUnit.getInfoString());
        }

        try {
            if (clientUnit.getSocket() != null) {
                clientUnit.getSocket().close ();
            }
        } catch (IOException e) {
            System.out.println("Ошибка закрытия сокета " + clientUnit.getInfoString());
        }
    }

    public void setBroadcastQueue(ConcurrentLinkedQueue<MessageUnit> broadcastMessageQueue) {
        if (broadcastQueue != null) {
            broadcastQueue = broadcastMessageQueue;
        }
    }
}
