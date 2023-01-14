package de.hsbremen.mkss.restservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "line_item")
public class LineItem {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
    private Long id;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
    @Getter
    @Setter
    @NonNull
    @Column(name = "Product_Name")
    private String productName;

    @Getter
    @Setter
    @NonNull
    @Column(name = "Price")
    private float price;

    @Getter
    @Setter
    @NonNull
    @Column(name = "Quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "orderid", referencedColumnName = "id", nullable = false)
    @Getter
    @Setter
    @JsonIgnore
    private oorder Order;

}


