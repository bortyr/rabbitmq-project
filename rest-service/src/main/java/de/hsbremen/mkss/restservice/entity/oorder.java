package de.hsbremen.mkss.restservice.entity;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "oorder")
public class oorder {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @NonNull
    @DateTimeFormat(pattern = "dd.mm.yyyy")
    @Column(name = "date")
    private Date date;

    @Getter
    @Setter
    @NonNull
    @Column(name = "customer_Name")
    private String customerName;


    @Getter
    @Setter
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.EMPTY;

    @OneToMany(mappedBy = "Order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)

    @Getter
    @Setter
    private Set<LineItem> items;

//    public void addLineItem(LineItem item)
//    {
//        items.add(item);
//    }

    public void removeItem(long itemId)
    {
        items.removeIf(item -> item.getId() == itemId);
    }

}


