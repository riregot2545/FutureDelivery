package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AssignOrderLine extends OrderProductLine {
    private int assignQuantity;

    public AssignOrderLine(Product product, int quantity, StoreOrder storeOrder, int assignQuantity) {
        super(product, quantity, storeOrder);
        this.assignQuantity = assignQuantity;
    }

    public void addAssignQuantity(int quantityToAdd) {
        if ((assignQuantity + quantityToAdd) > quantity)
            throw new IllegalArgumentException("Sum quantity bigger than real quantity");
        assignQuantity += quantityToAdd;
    }

    public void resetAssign() {
        resetAssign(assignQuantity);
    }

    public void resetAssign(int quantityToReset) {
        if ((assignQuantity - quantityToReset) < 0)
            throw new IllegalArgumentException("Reset quantity is bigger than real quantity");
        assignQuantity -= quantityToReset;
    }

    public int getRemainQuantity() {
        return quantity - assignQuantity;
    }
}
