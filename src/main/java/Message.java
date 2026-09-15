
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Scanner;

public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    private Timestamp timestamp;
    private String type;
    private String target;
    private String sender;
    private String payload;
    public Message() {}
    public Message(String type, String target, String payload) {
        this.type = type;
        this.target = target;
        this.payload = payload;
    }
    public Message(String type, String sender){
        this.type = type;
        this.sender = sender;
    }
    public Message(Timestamp timeStamp, String type, String target, String sender, String payload) {
        this.timestamp = timeStamp;
        this.type = type;
        this.target = target;
        this.sender = sender;
        this.payload = payload;
    }

    public String clientMessageToString(){
        timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp + "|" + type + "|" + target + "|" + payload;
    }
    public String serverMessageToString(){
        return timestamp + "|" + type + "|" + sender + "|" + payload;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTarget(){
        return target;
    }
    public void setTarget(String target){
        this.target = target;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
