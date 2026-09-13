
import java.sql.Timestamp;

public class Message {
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

    public String clientMessageToString(){
        timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp + "|" + type + "|" + target + "|" + payload;
    }
}
