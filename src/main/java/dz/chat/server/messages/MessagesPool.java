package dz.chat.server.messages;

import dz.chat.MsgUnitType;
import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessagesPool extends Thread {
    private final ConcurrentLinkedQueue <MessageUnit> freeMessages;
    private final ConcurrentLinkedQueue <MessageUnit> busyMessages;

    public MessagesPool() {
        busyMessages = new ConcurrentLinkedQueue<>();
        freeMessages = new ConcurrentLinkedQueue<>();
        setDaemon(true);
        start ();
    }

    public MessageUnit obtain(String msgIn, ClientUnit source) {
        return obtain(msgIn, MsgUnitType.BROADCAST, source, null); //broadcast
    }

    public MessageUnit obtain (String msgIn, MsgUnitType type, ClientUnit source, String targetNick) {
        MessageUnit mu;

        if (!freeMessages.isEmpty()) {
            mu = freeMessages.poll();
        } else {
            mu = new MessageUnit();
        }

        mu.setMessage(msgIn);
        mu.setType(type);
        mu.setSource(source);
        mu.setTargetNick(targetNick);
        busyMessages.add (mu);
        return mu;
    }

    //Тред проверяет очередь занятых сообщений и переносит их в очередь свободных при смене флага "isUsing"
    @Override
    public void run() {
        while (true) {
            if (!busyMessages.isEmpty()) {
                try {
                    Iterator<MessageUnit> it = busyMessages.iterator();
                    while (it.hasNext()) {
                        MessageUnit mu = it.next();
                        if (mu != null) {
                            if (!mu.isUsing()) {
                                it.remove();
                                freeMessages.add (mu);
                            }
                        }
                    }
                } catch (NoSuchElementException e) {
                    continue;
                } catch (IllegalStateException e) {
                    //Если по каким-то причинам не можем удалить элемент, то проверям самый первый
                    //Когда-то ведь он должен освободиться
                    if (!busyMessages.isEmpty()) {
                        MessageUnit mu = busyMessages.poll();
                        if (!mu.isUsing()) {
                            freeMessages.add(mu);
                        } else {
                            busyMessages.add (mu);
                        }
                    }
                    continue;
                }
            } else {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }
}
