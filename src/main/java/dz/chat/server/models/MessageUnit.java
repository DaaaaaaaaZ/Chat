package dz.chat.server.models;

import dz.chat.MsgUnitType;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class MessageUnit {
    private String message;
    private DataOutputStream out;
    private ClientUnit source;
    private String target;
    private volatile boolean isUsing = false;
    private MsgUnitType type;

    public boolean isUsing() {
        return isUsing;
    }

    public void reset () {
        message = null;
        out = null;
        source = null;
        target = null;
        isUsing = false;
    }

    public MsgUnitType getType() {
        return type;
    }

    public void setType(MsgUnitType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        isUsing = true;
        this.message = message;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        isUsing = true;
        this.out = out;
    }

    public ClientUnit getSource() {
        return source;
    }

    public void setSource(ClientUnit source) {
        isUsing = true;
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTargetNick (String target) { //
        isUsing = true;
        this.target = target;
    }


}
