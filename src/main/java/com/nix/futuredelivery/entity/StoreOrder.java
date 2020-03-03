package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.OrderProductLine;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


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
    private List<OrderProductLine> productLines;

    private boolean isClosed;
    private boolean isDistributed;

    public StoreOrder(Store store, boolean isClosed, boolean isDistributed) {
        this.store = store;
        this.isClosed = isClosed;
        this.isDistributed = isDistributed;
        this.creationDate = LocalDateTime.now();
    }
}
