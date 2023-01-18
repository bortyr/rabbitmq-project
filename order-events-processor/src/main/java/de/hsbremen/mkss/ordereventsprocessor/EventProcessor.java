package de.hsbremen.mkss.ordereventsprocessor;

import de.hsbremen.mkss.events.Event;
import de.hsbremen.mkss.events.EventWithPayload;
import de.hsbremen.mkss.restservice.controllers.entity.Oorder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EventProcessor {

    private AmqpTemplate amqpTemplateReply;
    public EventProcessor(AmqpTemplate amqpTemplate) {
        this.amqpTemplateReply = amqpTemplate;
    }

    public boolean ReplyFailure() {
        Random replyStatus = new Random();
        Boolean replyProceeded = replyStatus.nextBoolean();
        return replyProceeded;
    }

    @RabbitListener(queues="${my.rabbitmq.a.queue}")
    public void receiveMessage(EventWithPayload<Oorder> event) {
        System.out.println(event);

        if (ReplyFailure()){
            amqpTemplateReply.convertAndSend(event);
            event.setStatus(Event.EventStatus.ACCEPTED);
        }else{
            event.setStatus(Event.EventStatus.REJECTED);
        }
        System.out.println("Order status:" + event.getStatus());



    }
}
