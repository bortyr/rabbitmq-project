package de.hsbremen.mkss.restservice.replyprocessor;

import de.hsbremen.mkss.events.EventWithPayload;
import de.hsbremen.mkss.restservice.controllers.entity.Oorder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ReplyProcessor {

    @RabbitListener(queues="${my.rabbitmq.a.queue.reply}")
    public void receiveMessage(EventWithPayload<Oorder> event) {
        System.out.println(event);
    }
}
