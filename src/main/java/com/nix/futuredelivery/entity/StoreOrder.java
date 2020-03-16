package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.exceptions.NoProductInList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Store store;

    private LocalDateTime creationDate;

    @OneToMany(
            mappedBy = "storeOrder",
            cascade = CascadeType.ALL
    )
    private List<OrderProductLine> productLines = new ArrayList<>();

    private boolean isClosed;
    private boolean isDistributed;

    public StoreOrder(Store store, boolean isClosed, boolean isDistributed) {
        this.store = store;
        this.isClosed = isClosed;
        this.isDistributed = isDistributed;
        this.creationDate = LocalDateTime.now();
    }

    public boolean containsProduct(Product product){
        return productLines.stream().map(AbstractProductLine::getProduct).anyMatch(orderProduct -> orderProduct.equals(product));
    }
    public OrderProductLine getLineByProduct(Product product){
        return productLines.stream().filter(line->line.getProduct().equals(product)).findAny()
                .orElseThrow(()->new NoProductInList(product.getId(), id, "Order"));
    }
    @Transactional
    public void setOrderLineQuantity(OrderProductLine productLine) {
        OrderProductLine oldLine;
        if (containsProduct(productLine.getProduct())) {
            oldLine = getLineByProduct(productLine.getProduct());
        } else {
            oldLine = new OrderProductLine(productLine.getProduct(), productLine.getQuantity(), this);
            getProductLines().add(oldLine);
        }
        oldLine.setQuantity(productLine.getQuantity());
    }
}
