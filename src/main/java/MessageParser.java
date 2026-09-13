public class MessageParser {

    public static Message parse(String rawMessage) {
        if (rawMessage == null) {
            return new Message("ERROR", "No message received.");
        }

        String trimmedMessage = rawMessage.trim();
        if (trimmedMessage.isEmpty()) {
            return new Message("ERROR", "Message cannot be empty.");
        }

        String[] fields = trimmedMessage.split("\\|", 5);
        String commandType = fields[0];

        switch (commandType) {
            case "LOGIN":
                return new Message("LOGIN", extractValue(fields, 1));
            case "Join_Room":
                return new Message("JOIN_ROOM", extractValue(fields, 1));
            case "List_Rooms":
                return new Message("LIST_ROOMS", "");
            case "List_Users":
                return new Message("LIST_USERS", "");
            case "Private_Message":
                return new Message("PRIVATE_MESSAGE", extractPrivateMessagePayload(fields));
            case "Quit":
                return new Message("QUIT", "");
            default:
                return new Message("CHAT", trimmedMessage);
        }
    }

    private static String extractValue(String[] fields, int index) {
        if (fields.length <= index) {
            return "";
        }
        return fields[index].trim();
    }

    private static String extractPrivateMessagePayload(String[] fields) {
        if (fields.length < 3) {
            return "";
        }

        String targetUsername = fields[1].trim();
        if (targetUsername.isEmpty()) {
            return "";
        }

        StringBuilder messagePayload = new StringBuilder();
        for (int index = 2; index < fields.length; index++) {
            if (index > 2) {
                messagePayload.append('|');
            }
            messagePayload.append(fields[index].trim());
        }

        return targetUsername + "|" + messagePayload;
    }
}
