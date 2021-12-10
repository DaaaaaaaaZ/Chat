package dz.chat.server.managers;

import dz.chat.server.messages.InputMessageProcessor;
import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageManager extends Thread {
    private final ExecutorService queries;
    private final ExecutorService waiters;
    private final InputMessageProcessor inputProcessor;
    private final ConcurrentLinkedQueue<MessageUnit> groupMessages;
    private final ConcurrentLinkedQueue<MessageUnit> broadcastMessages;

    public MessageManager(ConcurrentLinkedQueue<MessageUnit> groupMessages,
                          ConcurrentLinkedQueue<MessageUnit> broadcastQueue) {
        queries = Executors.newCachedThreadPool();
        waiters = Executors.newCachedThreadPool();
        this.broadcastMessages = broadcastQueue;
        this.groupMessages = groupMessages;

        inputProcessor = new InputMessageProcessor();
    }

    public void pushMessage(String msgIn, ClientUnit clientUnit) {
        MessageUnit mu;
        mu = inputProcessor.unparseMessage (msgIn, clientUnit);
        if (mu != null) {
            switch (mu.getType()) {
                case BROADCAST: {
                    broadcastMessages.add(mu);
                }
                break;
            }
        }
    }
}
