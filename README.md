::VEJLEDNING::





1, find Chatserver

2, click på den grønne pil til venstre fra "public class ChatServer"

3, find chatclient og klik på den grønne pil til venstre fra "public class ChatServer"

4, i konolen kan du nu indtaste et bruger navn

5, du er nu i generelt chat rummet. forskellige muligheder er nu åbne til dig

6, ved brugen af join er det forslået at man bruger /rooms kommandoen for at se listen over all chatroom tilgængelig

7, for at join en chatroom så skal man bare indtast /join + navn på chatroomet og vis chatroomet ikke allerede eksister så vil det blive oprettet

8, /msg er for direkte private beskeder for at bruge den skal man indtaste /msg + navn på modtager + beskeden.

9, /who for at se en liste af bruger i det samme chatrum som du er i.

10, /history henter en liste af alle beskeder sent i det chatrum som du befinder dig i

11, /help vil bringe op kommando listen igen

12, /quit vil lukke klienten


::PROTOCOL BESKRIVELSE::
(PLACEHOLDER)

•
Transport: TCP (port 5000), line-orienteret tekst (println / readLine).
•

Handshake: server sender "Enter username:", klient sender brugernavn; server svarer "Welcome..." og placerer klient i "general".
•

Kommandoer (start med '/'): /join, /rooms, /who, /msg <user> <msg>, /history, /help, /quit.
•

Beskeder: plaintext broadcast i rummet; format Message.toString() = "[timestamp] sender: text".
•

Private beskeder: /msg => sender ser "[DM to X] …", modtager "[DM from Y] …".
•

Historik: per-room ConcurrentLinkedDeque, kan hente N seneste.
•

System: systembeskeder med sender "SYSTEM".
•

Bemærk: ingen kryptering, ingen binær framing, maks 3 samtidige klienter (thread-pool).






der er blevet arbejde med både GitHub copilot og Claude ai model sonnet 5 (medium)

Claude har givet forslag og eksempler på forskellige måder at gemme chatrooms og dens beskeder. niels fandt selv cuncurrenthashmap hvor efter jeg brugte Claude til at undersøg dens funktioner og om den passer til projektet 

ideer om hvordan navigation af de forskellige chatrooms og konsol blev udarbejde med claude som igen gav eksempler og forslag

disse samtaler med Claude var så givet til GitHub copilot for context og derefter udarbejde den en implementerings plan og issues 
