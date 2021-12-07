package dz.chat.server.messages;

import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

public class InputMessageProcessor {
    private final MessagesPool msgPool;

    public InputMessageProcessor() {
        msgPool = new MessagesPool();
    }

    public MessageUnit unparseMessage(String msgIn, ClientUnit clientUnit) {
        String [] msgArr = msgIn.split(" ");
        if (msgArr.length == 1) {
            //Сообщение содержит одно слово
            return msgPool.obtain (
                    msgIn,

                    );
        }

    }
}
