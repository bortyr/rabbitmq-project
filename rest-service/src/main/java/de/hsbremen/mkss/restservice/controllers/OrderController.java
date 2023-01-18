package de.hsbremen.mkss.restservice.controllers;

import de.hsbremen.mkss.restservice.controllers.entity.LineItem;
import de.hsbremen.mkss.restservice.controllers.entity.OrderState;

import de.hsbremen.mkss.restservice.controllers.entity.Oorder;
import de.hsbremen.mkss.restservice.exceptions.OorderItemNotFoundException;
import de.hsbremen.mkss.restservice.exceptions.OorderNotFoundException;
import de.hsbremen.mkss.restservice.exceptions.OorderNotInPreparationException;
import de.hsbremen.mkss.restservice.repository.LineItemRepository;
import de.hsbremen.mkss.restservice.repository.oorderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
public class OrderController {

    private final OrderEventsProducer eventsProducer ;

    public OrderController(OrderEventsProducer eventsProducer, oorderRepository repository, LineItemRepository lineItemRepository) {
        this.eventsProducer = eventsProducer;
        this.repository = repository;
        this.lineItemRepository = lineItemRepository;
    }

    @GetMapping("/health")
    String health() {
        Oorder order = new Oorder();
        Date date = new Date();
        order.setDate(date);
        order.setCustomerName("helloname");
        //eventsProducer.emitCreateEvent(order);
        eventsProducer.sendMsg();

        return "OK";
    }

    private final oorderRepository repository;
    private final LineItemRepository lineItemRepository;


    //Retrieving all orders (including associated line items)
    @GetMapping("/orders")
    List<Oorder> all() {
        return (List<Oorder>) repository.findAll();
    }

    //Retrieving an order with a given id
    @GetMapping("/orders/{id}")
    Oorder one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OorderNotFoundException(id));
    }

    //Retrieving all line items of an order with a given id
    @GetMapping("/order/{id}/item/{itemId}")
    public ResponseEntity<LineItem> getLineItem(@PathVariable long id, @PathVariable long itemId) {
        Oorder order = repository.findById(id).orElseThrow(() -> new OorderItemNotFoundException(id, itemId) );
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
    public ResponseEntity<Oorder> create(@RequestParam String customerName) {
        Oorder order = new Oorder();
        Date date = new Date();
        order.setDate(date);
        order.setCustomerName(customerName);
        order.setItems(new HashSet<>());
        repository.save(order);
        eventsProducer.emitCreateEvent(order);

        return ResponseEntity.ok(order);
    }

    //Adding a line item to an order
    @PostMapping("/order/{orderId}/item")
    public ResponseEntity<LineItem> addLineItem(@RequestParam String Product_Name, @RequestParam float Price, @RequestParam int Quantity, @PathVariable long orderId) {
        Oorder order = repository.findById(orderId).orElseThrow(() -> new OorderNotFoundException(orderId) );

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
        Oorder order = repository.findById(id).orElseThrow(() -> new OorderItemNotFoundException(id, itemId) );
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
    public ResponseEntity<Oorder> commitOrder(@PathVariable long id)
    {
        Oorder order = repository.findById(id).orElseThrow(() -> new OorderNotInPreparationException(id));
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
