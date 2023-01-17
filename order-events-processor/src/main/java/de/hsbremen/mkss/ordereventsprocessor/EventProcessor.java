package de.hsbremen.mkss.ordereventsprocessor;

import de.hsbremen.mkss.events.EventWithPayload;
import de.hsbremen.mkss.restservice.entity.oorder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EventProcessor {

    private AmqpTemplate amqpTemplateReply;
    public EventProcessor(AmqpTemplate amqpTemplate) {
        this.amqpTemplateReply = amqpTemplate;
    }


    @RabbitListener(queues="${my.rabbitmq.a.queue}")
    public void receiveMessage(EventWithPayload<oorder> event) {
        System.out.println(event);
        amqpTemplateReply.convertAndSend(event);
    }

}
