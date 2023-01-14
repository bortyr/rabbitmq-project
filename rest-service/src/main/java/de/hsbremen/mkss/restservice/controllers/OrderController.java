package de.hsbremen.mkss.restservice.controllers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.hsbremen.mkss.restservice.entity.LineItem;
import de.hsbremen.mkss.restservice.entity.OrderState;
import de.hsbremen.mkss.restservice.entity.oorder;
import de.hsbremen.mkss.restservice.exceptions.OorderItemNotFoundException;
import de.hsbremen.mkss.restservice.exceptions.OorderNotFoundException;
import de.hsbremen.mkss.restservice.exceptions.OorderNotInPreparationException;
import de.hsbremen.mkss.restservice.repository.LineItemRepository;
import de.hsbremen.mkss.restservice.repository.oorderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Send {

    private final static String QUEUE_NAME = "hello";

    public void sendMsg() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch (Exception e) {
            System.out.println("ERROR: RabbitMQ send went wrong");
        }
    }
}
@RestController
public class OrderController {
    private final Send sendOkRabbit = new Send();
    @GetMapping("/health")
    String health() {
        sendOkRabbit.sendMsg();
        return "OK";
    }

    private final oorderRepository repository;
    private final LineItemRepository lineItemRepository;

    OrderController(oorderRepository repository, LineItemRepository lineItemRepository) {
        this.repository = repository;
        this.lineItemRepository = lineItemRepository;
    }

    //Retrieving all orders (including associated line items)
    @GetMapping("/orders")
    List<oorder> all() {
        return (List<oorder>) repository.findAll();
    }

    //Retrieving an order with a given id
    @GetMapping("/orders/{id}")
    oorder one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OorderNotFoundException(id));
    }

    //Retrieving all line items of an order with a given id
    @GetMapping("/order/{id}/item/{itemId}")
    public ResponseEntity<LineItem> getLineItem(@PathVariable long id, @PathVariable long itemId) {
        oorder order = repository.findById(id).orElseThrow(() -> new OorderItemNotFoundException(id, itemId) );
        Set<LineItem> items = order.getItems();
        for (LineItem item : items) {
            if (item.getId() == itemId) {
                return ResponseEntity.ok(item);
            }
        }
        throw new OorderItemNotFoundException(id, itemId);
    }

    //Creating a new order (with the customer’s name)
    @PostMapping("/neworder")
    public ResponseEntity<oorder> create(@RequestParam String customerName) {
        oorder order = new oorder();
        Date date = new Date();
        order.setDate(date);
        order.setCustomerName(customerName);
        order.setItems(new HashSet<>());
        repository.save(order);

        return ResponseEntity.ok(order);
    }

    //Adding a line item to an order
    @PostMapping("/order/{orderId}/item")
    public ResponseEntity<LineItem> addLineItem(@RequestParam String Product_Name, @RequestParam float Price, @RequestParam int Quantity, @PathVariable long orderId) {
        oorder order = repository.findById(orderId).orElseThrow(() -> new OorderNotFoundException(orderId) );

        if (order.getState() == OrderState.EMPTY || order.getState() == OrderState.IN_PREPARATION) {
            LineItem item = new LineItem();
            item.setProductName(Product_Name);
            item.setPrice(Price);
            item.setQuantity(Quantity);
            item.setOrder(order);

            order.setState(OrderState.IN_PREPARATION);
            repository.save(order);
            lineItemRepository.save(item);
            return ResponseEntity.ok(item);
        }
        else {
            throw new OorderNotFoundException(orderId);
        }
    }

    //Removing a line item from an order
    @DeleteMapping("/order/{id}/item/{itemId}")
    void removeLineItem(@PathVariable long id, @PathVariable long itemId) {
        oorder order = repository.findById(id).orElseThrow(() -> new OorderItemNotFoundException(id, itemId) );
        order.removeItem(itemId);
        if (order.getState() == OrderState.IN_PREPARATION) {
            lineItemRepository.delete(lineItemRepository.findById(itemId).orElseThrow(() -> new OorderNotInPreparationException(id)));
            if (order.getItems().isEmpty()) {
                order.setState(OrderState.EMPTY);
            }
            repository.save(order);
        }
    }


    //Deleting an order
    @DeleteMapping("/orders/{id}")
    void deleteOrder(@PathVariable Long id) {
        repository.deleteById(id);
    }
    // Purchasing (commit)
    @PostMapping("/order/{id}/commit")
    public ResponseEntity<oorder> commitOrder(@PathVariable long id)
    {
        oorder order = repository.findById(id).orElseThrow(() -> new OorderNotInPreparationException(id));
        if(order.getState() == OrderState.IN_PREPARATION)
        {
            order.setState(OrderState.COMMITTED);
            repository.save(order);
            return ResponseEntity.ok(order);
        }
        else if ( order.getState() == OrderState.COMMITTED || order.getState() == OrderState.ACCEPTED || order.getState() == OrderState.REJECTED)
        {
            throw new OorderNotInPreparationException(id);
        }
        else if(order.getState() == OrderState.EMPTY)
        {
            throw new OorderNotInPreparationException(id);
        }
        throw new OorderNotInPreparationException(id);
    }

}
