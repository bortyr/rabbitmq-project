package de.hsbremen.mkss.ordereventsprocessor;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EventProcessor {

    @Autowired
    private final AmqpTemplate amqpTemplate;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public EventProcessor(AmqpTemplate amqpTemplate) {
            this.amqpTemplate = amqpTemplate;
        }

    @PostConstruct
    public void start() {
            executorService.submit(this::pollMessages);
        }

    private void pollMessages() {
        while (true) {
            Object message = amqpTemplate.receiveAndConvert();
            System.out.println("Received message: " + message);

        }
    }
}
