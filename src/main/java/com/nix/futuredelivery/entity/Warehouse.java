package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.NoProductInList;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Warehouse extends AbstractStation{
    @OneToOne(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private WarehouseManager warehouseManager;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<WarehouseProductLine> productLines = new ArrayList<>();

    public Warehouse(Long id, Address address, String name, WarehouseManager warehouseManager) {
        super(id, address, name);
        this.warehouseManager = warehouseManager;
    }

    public boolean warehouseContainsProduct(Product product) {
        for (WarehouseProductLine productLine : productLines) {
            Product warehouseProduct = productLine.getProduct();
            if (warehouseProduct.getId().equals(product.getId())) {
                return true;
            }
        }
        return false;
    }
    @Transactional
    public WarehouseProductLine getWarehouseProductLine(Product product) {
        for (WarehouseProductLine line : productLines) {
            if (line.getProduct().getId().equals(product.getId())) return line;
        }
        throw new NoProductInList(product.getId(), getId(), "Warehouse");
    }
    @Transactional
    public void setWarehouseLineQuantity(WarehouseProductLine line) {
        WarehouseProductLine oldLine = getWarehouseProductLine(line.getProduct());
        oldLine.setQuantity(line.getQuantity());
    }
}

