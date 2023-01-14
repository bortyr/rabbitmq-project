import de.hsbremen.mkss.events.Event;
import de.hsbremen.mkss.events.EventWithPayload;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        EventWithPayload<String> event
                = EventWithPayload.<String>builder()
                .type(Event.EventType.CREATED)
                .payload("New Order")
                .build();
    }
}