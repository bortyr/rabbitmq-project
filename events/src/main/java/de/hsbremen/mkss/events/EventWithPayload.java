package de.hsbremen.mkss.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString(callSuper=true)
public class EventWithPayload<T> extends Event {

    private T payload;

    // Public constructor with Builder pattern (Lombok)
    @Builder
    public EventWithPayload(EventType type, T payload) {
        super(type);
        this.payload = payload;
    }

    // Private constructor only for JSON deserialization (Jackson)
    @JsonCreator
    private EventWithPayload(@JsonProperty("id") int eventId,
                            @JsonProperty("date") Date date,
                            @JsonProperty("type") EventType type,
                            @JsonProperty("payload") T payload) {
        super(eventId, type, date);
        this.payload = payload;
    }

}
