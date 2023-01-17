package de.hsbremen.mkss.ordereventsprocessor;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${my.rabbitmq.an.exchange}")
    String exchangeName;

    @Value("${my.rabbitmq.a.queue.reply}")
    String queueName;

    @Value("${my.rabbitmq.a.routing.key.reply}")
    String routingKeyName;

    // BEGIN: Template code for direct exchanges and fanout exchanges

    //
    // Template code: Configuration of a direct exchange (uses routing key)
    //

    // Exchanges are required for emitting and receiving event messages
    @Bean
    DirectExchange someExchange() {
        return new DirectExchange(exchangeName);
    }

    // Queues are required for receiving event messages
    @Bean
    Queue someQueue() {
        return new Queue(queueName, false);
    }

    // Bindings are required for receiving event messages:
    // connecting of a queue to an exchange
    @Bean
    Binding someBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKeyName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, Queue queue) {
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(queue);
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchangeName);
        rabbitTemplate.setRoutingKey(routingKeyName);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}

