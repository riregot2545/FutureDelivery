package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @JsonIgnore
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

    private boolean containsProduct(Product product){
        return productLines.stream().map(AbstractProductLine::getProduct).anyMatch(orderProduct -> orderProduct.equals(product));
    }
    public Optional<OrderProductLine> getLineByProduct(Product product){
        if(!containsProduct(product)) return Optional.empty();
        return productLines.stream().filter(line->line.getProduct().equals(product)).findAny();
    }

}
