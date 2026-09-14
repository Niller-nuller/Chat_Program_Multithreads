import Exceptions.BlankException;
import Exceptions.CommandLengthException;
import Exceptions.TypeException;

import java.lang.reflect.Type;

public class MessageParser implements AutoCloseable {

    public MessageParser() {
    }

    public Message parseClientStringToMessage(String stringMessage, String username) {
        String[] parts = new String[3];
        try {
            parts = validateClientInput(stringMessage);
        } catch (Exception e) {
            IO.println(e.getMessage());

        }
        Message message;
        switch (parts[0]) {
            case "CHATROOMS":
                message = new Message(parts[0], username);
                break;
            default:
                message = new Message(parts[0], parts[1], parts[2]);
                break;
        }
        return message;
    }
    public String parseServerMessageToString(Message message) {
        String messageInString = message.serverMessageToString();
        return messageInString;
    }



    private static String[] validateClientInput(String stringMessage) throws TypeException, CommandLengthException, BlankException {
        if (stringMessage.isEmpty()) {
            throw new BlankException("No command was provided");
        }
        if (!stringMessage.contains("|")) {
            throw new CommandLengthException("The command has an invalid format");
        }
        String[] parts = stringMessage.split("\\|", 3);
        switch (parts[0]) {
            case "LOGIN":
                break;
            case "TEXT":
                break;
            case "PRIVATE":
            default:
                throw new TypeException(parts[0] + " is not a valid type");
        }
        if (parts.length < 3) {
            throw new CommandLengthException("Command too short");
        } else if (parts.length > 3) {
            throw new CommandLengthException("Command too long");
        }
        if (parts[2].isBlank()) {
            throw new BlankException("No payload provided");
        }
        return parts;
    }

    @Override
    public void close() throws Exception {

    }
}
