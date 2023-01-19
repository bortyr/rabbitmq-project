package de.hsbremen.mkss.events;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@ToString
public abstract class Event {


    public enum EventType {CREATED, CHANGED, DELETED};

    private final static AtomicInteger counter = new AtomicInteger(0);

    private int id;
    private EventType type;
    private Date date;

    private EventStatus status = EventStatus.EMPTY;

    public Event(EventType type) {
        this.id = counter.incrementAndGet();
        this.date = new Date();
        this.type = type;
        this.status = EventStatus.EMPTY;
    }

    public Event(int id, EventType type, Date date, EventStatus status) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.status = status;
    }
}
