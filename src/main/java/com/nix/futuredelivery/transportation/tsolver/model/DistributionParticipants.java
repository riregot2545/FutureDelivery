package com.nix.futuredelivery.transportation.tsolver.model;

import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class DistributionParticipants {
    private final Consumer[] consumers;
    private final Supplier[] suppliers;

    public int consumersCount(){
        return consumers.length;
    }
    public int suppliersCount(){
        return suppliers.length;
    }

    public int getConsumerDemand(int index){
       checkConsumerIndex(index);
        return consumers[index].getDemand();
    }

    public int getSupplierSupply(int index){
        checkSupplierIndex(index);
        return suppliers[index].getSupply();
    }

    public Address getConsumerAddress(int index){
        checkConsumerIndex(index);
        return consumers[index].getStore().getAddress();
    }

    public Address getSupplierAddress(int index){
        checkSupplierIndex(index);
        return suppliers[index].getWarehouse().getAddress();
    }

    public Store getConsumerStore(int index){
        checkConsumerIndex(index);
        return consumers[index].getStore();
    }

    public Warehouse getSupplierWarehouse(int index){
        checkSupplierIndex(index);
        return suppliers[index].getWarehouse();
    }

    public void setConsumerDemand(int index, int demand){
        checkConsumerIndex(index);
        consumers[index].setDemand(demand);
    }

    public void setSupplierSupply(int index, int supply){
        checkSupplierIndex(index);
        suppliers[index].setSupply(supply);
    }

    public boolean isConsumerFictive(int index){
        checkConsumerIndex(index);
        return consumers[index].isFictive();
    }

    public DistributionParticipants clone(){
        return new DistributionParticipants(
                Arrays.stream(consumers).map(c->new Consumer(c.getDemand(),c.getStore(),c.isFictive())).toArray(Consumer[]::new),
                Arrays.stream(suppliers).map(s->new Supplier(s.getSupply(),s.getWarehouse())).toArray(Supplier[]::new)
        );
    }

    private void checkSupplierIndex(int index){
        if(index<0 || index>=suppliersCount())
            throw new IllegalArgumentException("Supplier array index must be positive integer and be less than count.");
    }

    private void checkConsumerIndex(int index){
        if(index<0 || index>=consumersCount())
            throw new IllegalArgumentException("Consumer array index must be positive integer and be less than count.");
    }

    public static class Builder{
        private List<Consumer> consumers;
        private List<Supplier> suppliers;

        public Builder() {
            this.consumers = new ArrayList<>();
            this.suppliers = new ArrayList<>();
        }

        public Builder addConsumer(Store store, int demand){
            consumers.add(new Consumer(demand,store,false));
            return this;
        }

        public Builder addFictiveConsumer(int demand){
            consumers.add(new Consumer(demand,null,true));
            return this;
        }

        public Builder addSupplier(Warehouse warehouse, int supply){
            suppliers.add(new Supplier(supply,warehouse));
            return this;
        }

        public DistributionParticipants build() {
            return build(true);
        }

        public DistributionParticipants build(boolean normalize){
            if(normalize)
                normalize();
            Consumer[] consumersArray = new Consumer[consumers.size()];
            consumers.toArray(consumersArray);
            Supplier[] suppliersArray = new Supplier[suppliers.size()];
            suppliers.toArray(suppliersArray);
            return new DistributionParticipants(consumersArray,suppliersArray);
        }

        public void normalize(){
            int consumerSum = consumers.stream().map(Consumer::getDemand).reduce(Integer::sum).orElse(0);
            int supplierSum = suppliers.stream().map(Supplier::getSupply).reduce(Integer::sum).orElse(0);
            if(consumerSum!=supplierSum){
                if(consumerSum>supplierSum){
                    throw new IllegalStateException("Normalize error, consumption is bigger than stock : "+ consumerSum+">"+supplierSum);
                }else {
                    addFictiveConsumer(supplierSum-consumerSum);
                }
            }
        }
    }
}
