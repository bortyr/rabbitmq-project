package de.hsbremen.mkss.restservice.controllers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.hsbremen.mkss.events.CrudEventProducer;
import de.hsbremen.mkss.events.Event;
import de.hsbremen.mkss.events.EventWithPayload;
import de.hsbremen.mkss.restservice.controllers.entity.Oorder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class OrderEventsProducer implements CrudEventProducer<Oorder> {

    private AmqpTemplate amqpTemplate;

    /*
    @Value("${localhost:5672}")
    String anExchangeName;
     */
    String anExchangeName = "localhost:5672";

    /*
    @Value("${}")
    String aRoutingKeyName;
     */
    String aRoutingKeyName = "";


    public OrderEventsProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }


    private EventWithPayload<Oorder> buildEvent(Event.EventType type, Oorder payload) {
        EventWithPayload<Oorder> event = EventWithPayload.<Oorder>builder()
                .type(type)
                .payload(payload)
                .build();
        return event;
    }

    // sendMsg - example of working connection to rabbitmq
    public void sendMsg() {
        String QUEUE_NAME = "hello";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sending message to RabbitMQ '" + message + "'");
        } catch (Exception e) {
            System.out.println("ERROR: RabbitMQ send went wrong");
        }
    }

    @Override
    public void emitCreateEvent(Oorder payload) {
        EventWithPayload<Oorder> event = buildEvent(Event.EventType.CREATED, payload);
        try {
            amqpTemplate.convertAndSend("order.routing.key", event);
        } catch (Exception exception) {
			exception.printStackTrace();
			return;
        }
        // Print info about that
        System.out.println("Sent event = " + event + " using exchange " + anExchangeName + " with routing key " + aRoutingKeyName);
    }

    @Override
    public void emitUpdateEvent(Oorder payload) {
        // TODO: Implementation for update events (e.g. changed order)
		EventWithPayload<Oorder> event = buildEvent(Event.EventType.CHANGED, payload);
		try {
			amqpTemplate.convertAndSend("order.routing.key", event);
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
		// Print info about that
		System.out.println("Changed event = " + event + " using exchange " + anExchangeName + " with routing key " + aRoutingKeyName);
    }

    @Override
    public void emitDeleteEvent(Oorder payload) {
        // TODO: Implementation for delete events (e.g. deleted order)
		EventWithPayload<Oorder> event = buildEvent(Event.EventType.DELETED, payload);
		try {
			amqpTemplate.convertAndSend("order.routing.key", event);
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
		// Print info about that
		System.out.println("Delete event = " + event + " using exchange " + anExchangeName + " with routing key " + aRoutingKeyName);
    }
}
