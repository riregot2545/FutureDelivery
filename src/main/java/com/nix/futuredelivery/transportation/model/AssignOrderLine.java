package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Transportation model class used as {@code OrderProductLine} with buffer for convenient product
 * quantity consider in distribution.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AssignOrderLine extends OrderProductLine {
    private int assignQuantity;

    /**
     * Constructs line with prepared assignQuantity.
     *
     * @param product        product in line.
     * @param quantity       product quantity.
     * @param storeOrder     linked store order.
     * @param assignQuantity prepared assign quantity.
     */
    public AssignOrderLine(Product product, int quantity, StoreOrder storeOrder, int assignQuantity) {
        super(product, quantity, storeOrder);
        this.assignQuantity = assignQuantity;
    }

    /**
     * Change assign quantity of current line. May throw {@code IllegalArgumentException} if sum of current assign
     * quantity and additional quantity greater than all available quantity.
     * @param quantityToAdd integer non negative quantity to add.
     */
    public void addAssignQuantity(int quantityToAdd) {
        if ((assignQuantity + quantityToAdd) > quantity)
            throw new IllegalArgumentException("Sum quantity bigger than real quantity");
        assignQuantity += quantityToAdd;
    }

    /**
     * Resets assign completely
     */
    public void resetAssign() {
        resetAssign(assignQuantity);
    }

    /**
     * Subtract assign by specified quantity. May throw {@code IllegalArgumentException} if current assign quantity
     * less than specified quantity to reset.
     * @param quantityToReset integer non negative quantity to subtract.
     */
    public void resetAssign(int quantityToReset) {
        if ((assignQuantity - quantityToReset) < 0)
            throw new IllegalArgumentException("Reset quantity is bigger than real quantity");
        assignQuantity -= quantityToReset;
    }

    /**
     * Returns difference between all assign and all quantity.
     * @return remain quantity to assign.
     */
    public int getRemainQuantity() {
        return quantity - assignQuantity;
    }
}
