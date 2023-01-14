package de.hsbremen.mkss.restservice.events;

import de.hsbremen.mkss.events.CrudEventProducer;
import de.hsbremen.mkss.events.Event;
import de.hsbremen.mkss.events.EventWithPayload;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class XYZEventsProducer implements CrudEventProducer<YourPayloadClass> {

	private AmqpTemplate amqpTemplate;

    @Value("${my.rabbitmq.an.exchange}")
    String anExchangeName;

    @Value("${my.rabbitmq.a.routing.key}")
    String aRoutingKeyName;


	public XYZEventsProducer(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}


	private EventWithPayload<YourPayloadClass> buildEvent(Event.EventType type, YourPayloadClass payload) {
		EventWithPayload<YourPayloadClass> event = EventWithPayload.<YourPayloadClass> builder()
				.type(type)
				.payload(payload)
				.build();
		return event;
	}

	@Override
	public void emitCreateEvent(YourPayloadClass payload) {
		EventWithPayload<YourPayloadClass> event = buildEvent(Event.EventType.CREATED, payload);
	
		// TODO: send event to RabbitMQ exchange  

		System.out.println("Sent event = " + event + " using exchange " + anExchangeName + " with routing key " + aRoutingKeyName);
	}

	@Override
	public void emitUpdateEvent(YourPayloadClass payload) {
		// TODO: Implementation for update events (e.g. changed order)
	}

	@Override
	public void emitDeleteEvent(YourPayloadClass payload) {
		// TODO: Implementation for delete events (e.g. deleted order)
	}
}
