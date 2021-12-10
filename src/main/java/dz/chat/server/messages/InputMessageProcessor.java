package dz.chat.server.messages;

import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

public class InputMessageProcessor {
    private final MessagesPool msgPool;

    public InputMessageProcessor() {
        msgPool = new MessagesPool();
    }

    public MessageUnit unparseMessage(String msgIn, ClientUnit clientUnit) {
        String [] msgArr;
        try {
            if (msgIn.startsWith("/")) {
                msgArr = msgIn.split(" ");
                if (msgArr.length == 1) {
                    //Получена строка, начинающаяся на "/" и состоящая из одного слова
                    //ToDo Отправить клиенту информацию по использованию команды
                    return null; //Пока игнорируем такое сообщение
                } else {
                    return null;
                }
            } else {
                //Сообщение не содержит команды - просто отправляем его в broadcast очередь
                System.out.println(msgIn);
                return msgPool.obtain (msgIn, clientUnit);
            }
        } catch (Exception e) {
            //
            return null;
        }
    }
}
