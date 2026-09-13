import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessageParserTest {

    @Test
    void parsesKnownCommands() {
        Message loginMessage = MessageParser.parse("LOGIN|alice");
        assertEquals("LOGIN", loginMessage.getType());
        assertEquals("alice", loginMessage.getPayload());

        Message joinMessage = MessageParser.parse("Join_Room|default");
        assertEquals("JOIN_ROOM", joinMessage.getType());
        assertEquals("default", joinMessage.getPayload());

        Message listRoomsMessage = MessageParser.parse("List_Rooms|");
        assertEquals("LIST_ROOMS", listRoomsMessage.getType());
        assertEquals("", listRoomsMessage.getPayload());

        Message listUsersMessage = MessageParser.parse("List_Users|");
        assertEquals("LIST_USERS", listUsersMessage.getType());
        assertEquals("", listUsersMessage.getPayload());

        Message quitMessage = MessageParser.parse("Quit|");
        assertEquals("QUIT", quitMessage.getType());
        assertEquals("", quitMessage.getPayload());
    }

    @Test
    void privateMessageKeepsFullPayload() {
        Message privateMessage = MessageParser.parse("Private_Message|alice|hello|there");
        assertEquals("PRIVATE_MESSAGE", privateMessage.getType());
        assertEquals("alice|hello|there", privateMessage.getPayload());
    }

    @Test
    void emptyOrNullInputProducesErrorMessage() {
        Message emptyMessage = MessageParser.parse("");
        assertEquals("ERROR", emptyMessage.getType());
        assertEquals("Message cannot be empty.", emptyMessage.getPayload());

        Message nullMessage = MessageParser.parse(null);
        assertEquals("ERROR", nullMessage.getType());
        assertEquals("No message received.", nullMessage.getPayload());
    }

    @Test
    void unknownTextIsTreatedAsChat() {
        Message textMessage = MessageParser.parse("hello world");
        assertEquals("CHAT", textMessage.getType());
        assertEquals("hello world", textMessage.getPayload());
        assertNotNull(textMessage);
    }
}
