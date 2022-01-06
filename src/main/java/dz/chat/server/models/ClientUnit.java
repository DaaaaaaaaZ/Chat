package dz.chat.server.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientUnit {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final long connectionTime;
    private String nick;
    private boolean isAuthOK = false;
    private long lastMessageTime; //Время последнего сообщения от клиента (для закрытия отключённых сокетов)

    public ClientUnit(Socket socket, DataInputStream in, DataOutputStream out, long connectionTime) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.connectionTime = connectionTime;
        lastMessageTime = connectionTime; //Если клиент не прислал ни одного сообщения, то считаем время одинаково
        isAuthOK = false;
    }

    public String getInfoString () {
        return (isAuthOK ? "(" + nick + ") " : "") + socket.getInetAddress() + ":" + socket.getPort();
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public long getConnectionTime() {
        return connectionTime;
    }

    public String getNick() {
        return nick;
    }

    public boolean isAuthOK() {
        return isAuthOK;
    }

    public void setAuthOK(boolean authOK) {
        isAuthOK = authOK;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public String getLogin() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
