package de.hsbremen.mkss.ordereventsprocessor;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    String anExchangeName;

    @Value("${my.rabbitmq.a.queue}")
    String aQueueName;

    @Value("${my.rabbitmq.a.routing.key}")
    String aRoutingKeyName;

    // BEGIN: Template code for direct exchanges and fanout exchanges

    //
    // Template code: Configuration of a direct exchange (uses routing key)
    //

    // Exchanges are required for emitting and receiving event messages
    @Bean("orderExchange")
    DirectExchange someExchange() {
        return new DirectExchange(anExchangeName);
    }

    // Queues are required for receiving event messages
    @Bean("orderQueue")
    Queue someQueue() {
        return new Queue(aQueueName, false);
    }

    // Bindings are required for receiving event messages:
    // connecting of a queue to an exchange
    @Bean
    Binding someBinding(@Qualifier("orderQueue") Queue queue, @Qualifier("orderExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(aRoutingKeyName);
    }

    // END: Template code for direct exchanges and fanout exchanges

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        return connectionFactory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
