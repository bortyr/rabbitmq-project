package de.hsbremen.mkss.restservice.controllers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.hsbremen.mkss.events.CrudEventProducer;
import de.hsbremen.mkss.events.Event;
import de.hsbremen.mkss.events.EventWithPayload;
import de.hsbremen.mkss.restservice.entity.oorder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class OrderEventsProducer implements CrudEventProducer<oorder> {

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


	private EventWithPayload<oorder> buildEvent(Event.EventType type, oorder payload) {
		EventWithPayload<oorder> event = EventWithPayload.<oorder> builder()
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
	public void emitCreateEvent(oorder payload) {
		EventWithPayload<oorder> event = buildEvent(Event.EventType.CREATED, payload);
	
		// TODO: send event to RabbitMQ exchange

		// Configuration
		String QUEUE_NAME = "hello";
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");

		// Perform connection
		try (Connection connection = factory.newConnection();
			 Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			// Set message
			String message = "New order" +
					" Id: " + payload.getId() +
					" Name: " + payload.getCustomerName() +
					" State: " + payload.getState() +
					" Items: " + payload.getItems() +
					" Date: " + payload.getDate();
			// Send the message
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
			// Print if success
			System.out.println("Sending message to RabbitMQ '" + message + "'");
		} catch (Exception e) {
			System.out.println("ERROR: RabbitMQ send went wrong");
		}

		// Print info about that
		System.out.println("Sent event = " + event + " using exchange " + anExchangeName + " with routing key " + aRoutingKeyName);
	}

	@Override
	public void emitUpdateEvent(oorder payload) {
		// TODO: Implementation for update events (e.g. changed order)
	}

	@Override
	public void emitDeleteEvent(oorder payload) {
		// TODO: Implementation for delete events (e.g. deleted order)
	}
}
