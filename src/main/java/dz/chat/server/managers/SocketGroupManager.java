package dz.chat.server.managers;

import dz.chat.server.models.MessageUnit;
import dz.chat.server.socketgroups.SocketGroup;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketGroupManager extends Thread {
    private static final int THREAD_CROUP_SIZE = 10;
    private final ConcurrentLinkedQueue <Socket> incomingSockets;
    private final ConcurrentHashMap <Integer, SocketGroup> threadGroups;
    private final ConcurrentLinkedQueue <MessageUnit> broadcastMessages;

    public SocketGroupManager () {
        incomingSockets = new ConcurrentLinkedQueue<>();
        threadGroups = new ConcurrentHashMap<>();
        broadcastMessages = new ConcurrentLinkedQueue<>();

        //Создаем одну группу и передаем общую ссылку на broadcast-очередь сообщений
        threadGroups.put (1, new SocketGroup(broadcastMessages));

        start ();
    }

    public void addSocket (Socket socket) {
        incomingSockets.add (socket);
    }

    @Override
    public void run () {
        try {
            while (true) {
                if (!incomingSockets.isEmpty()) {
                    //Проверяем очередь новых сокетов
                    //Отправлям новый сокет далее
                    pushSocketToGroup (incomingSockets.poll());
                } else {
                    sleep(100);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            dispose();
        }
    }

    private void pushSocketToGroup(Socket newSocket) {
        boolean isSocketPushed = false;

        for (Map.Entry<Integer, SocketGroup> group : threadGroups.entrySet()) {
            SocketGroup tg =group.getValue();
            if (tg.getSize() < THREAD_CROUP_SIZE) {
                tg.addSocket (newSocket);
                isSocketPushed = true;
            }
        }
        
        if (!isSocketPushed) {
            //Если сокет никуда не передался, то создаем новую группу и добавляем в мапу
            //ToDo
        }
    }

    public void dispose() {
        //Закрытие всего открытого и освобождение всего занятого
    }
}
