package dz.chat.server.messages;

import dz.chat.Commands;
import dz.chat.MsgUnitType;
import dz.chat.server.models.ClientUnit;
import dz.chat.server.models.MessageUnit;

public class InputMessageProcessor {
    private final static int MIN_SIZE_LOGIN = 3;
    private final static int MAX_SIZE_LOGIN = 12;
    private final static int MIN_SIZE_PASSWORD = 5;
    private final static int MAX_SIZE_PASSWORD = 24;
    private final static int MIN_SIZE_NICK = 2;
    private final static int MAX_SIZE_NICK = 10;

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
                    //То либо это "пинг" сервера CMD_TEST, либо команда неправильная
                    //ToDo Отправить клиенту информацию по использованию команды
                    return buildInfoOrOKResponse(msgArr [0], clientUnit, null);
                } else {
                    return buildReaction(msgIn, msgArr, clientUnit);
                }
            } else {
                //Сообщение не содержит команды - просто отправляем его в broadcast очередь
                if (clientUnit.isAuthOK()) {
                    return msgPool.obtain(msgIn, clientUnit);
                } else {//Или предлагаем ввести логин и пароль, если до сих пор нет оного
                    return msgPool.obtain(MsgUnitType.NEED_AUTH, clientUnit);
                }
            }
        } catch (Exception e) {
            //
            return null;
        }
    }

    private MessageUnit buildInfoOrOKResponse(String cmdString, ClientUnit clientUnit, MsgUnitType mut) {
        if (mut == null) {
            if (cmdString.startsWith(Commands.CMD_TEST)) {
                return msgPool.obtain(MsgUnitType.SYSTEM_OK, clientUnit);
            } else {
                return null;
                //ToDo вернуть информацию по командам
            }
        } else {
            return msgPool.obtain(MsgUnitType.SYSTEM_OK, clientUnit);
        }
    }

    private MessageUnit buildReaction(String msgIn, String[] msgArr, ClientUnit clientUnit) {
        //В этом методе мы точно знаем, что в сообщении есть команда и что-то еще, т.е. msgArr.length > 1
        try {
            if (msgArr [0].startsWith(Commands.CMD_WISPER)) {
                if (clientUnit.isAuthOK()) {
                    if (msgArr [1].length() >= MIN_SIZE_NICK && msgArr [1].length() <= MAX_SIZE_NICK) {
                        StringBuilder sb;
                        if (msgArr.length > 2) {
                            sb = new StringBuilder();
                            for (int i = 2; i < msgArr.length; i++) {
                                sb.append(msgArr [i]);
                                if (i != msgArr.length - 1) {
                                    sb.append(" ");
                                }
                            }
                            return msgPool.obtain(sb.toString(), MsgUnitType.WISP,
                                    clientUnit, msgArr [1]);
                        } else {
                            return null; //Сообщение содержит только команду и имя другого участника
                        }

                    } else {
                        return null; //ToDo Такого пользователя нет
                    }
                } else {
                    return msgPool.obtain(MsgUnitType.NEED_AUTH, clientUnit);
                }
            }

            if (msgArr [0].startsWith(Commands.CMD_AUTH)) {
                if (!clientUnit.isAuthOK()) {
                    if (msgArr[1].length() >= MIN_SIZE_LOGIN && msgArr[1].length() <= MAX_SIZE_LOGIN &&
                            msgArr[2].length() >= MIN_SIZE_PASSWORD && msgArr[2].length() <= MAX_SIZE_PASSWORD) {
                        return msgPool.obtain(msgArr[1] + " " + msgArr[2], MsgUnitType.NEED_QUERY,
                                clientUnit, null);
                    } else {
                        return msgPool.obtain(MsgUnitType.NEED_AUTH, clientUnit);
                    }
                } else {
                    //Если пользователь прошел аутентификацию и еще раз присылает запрос - игнорируем сообщение
                    return null;
                }
            }

            if (msgArr [0].startsWith(Commands.CMD_NEW_NICK)) {
                //ToDo сделать переименование
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
