import java.time.Instant;

public class Message {

    private final String sender;
    private final String text;
    private final Instant timestamp;

    public Message(String sender, String text, Instant timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp.toString() + "] " + sender + ": " + text;
    }
}
