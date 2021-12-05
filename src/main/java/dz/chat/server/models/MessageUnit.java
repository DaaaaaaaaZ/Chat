package dz.chat.server.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class MessageUnit {
    private String message;
    private DataOutputStream out;
    private DataInputStream in;
    private ClientUnit source;
    private ClientUnit target;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public ClientUnit getSource() {
        return source;
    }

    public void setSource(ClientUnit source) {
        this.source = source;
    }

    public ClientUnit getTarget() {
        return target;
    }

    public void setTarget(ClientUnit target) {
        this.target = target;
    }
}
