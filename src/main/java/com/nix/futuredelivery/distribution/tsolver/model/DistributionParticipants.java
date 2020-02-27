package com.nix.futuredelivery.distribution.tsolver.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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
        if(index<0 || index>=consumersCount())
            throw new IllegalArgumentException("Consumer array index must be positive integer and be less than count.");
        return consumers[index].getDemand();
    }

    public int getSupplierSupply(int index){
        if(index<0 || index>=suppliersCount())
            throw new IllegalArgumentException("Supplier array index must be positive integer and be less than count.");
        return suppliers[index].getSupply();
    }

    public void setConsumerDemand(int index, int demand){
        if(index<0 || index>=consumersCount())
            throw new IllegalArgumentException("Consumer array index must be positive integer and be less than count.");
        consumers[index].setDemand(demand);
    }

    public void setSupplierSupply(int index, int supply){
        if(index<0 || index>=suppliersCount())
            throw new IllegalArgumentException("Supplier array index must be positive integer and be less than count.");
        suppliers[index].setSupply(supply);
    }

    public DistributionParticipants clone(){
        return new DistributionParticipants(
                Arrays.stream(consumers).map(c->new Consumer(c.getDemand())).toArray(Consumer[]::new),
                Arrays.stream(suppliers).map(s->new Supplier(s.getSupply())).toArray(Supplier[]::new)
        );
    }

    public static class Builder{
        private Consumer[] consumers;
        private Supplier[] suppliers;

        private int consumerIndex = 0;
        private int supplierIndex = 0;

        public Builder(int consumerCount, int supplierCount) {
            this.consumers = new Consumer[consumerCount];
            this.suppliers = new Supplier[supplierCount];
        }


        public Builder addConsumer(int demand){
            if(consumerIndex>=consumers.length)
                throw new IllegalStateException("Consumer array index out of bounds: "+ consumerIndex);
            consumers[consumerIndex] = new Consumer(demand);
            consumerIndex++;
            return this;
        }

        public Builder addSupplier(int supply){
            if(supplierIndex>=suppliers.length)
                throw new IllegalStateException("Consumer array index out of bounds: "+ supplierIndex);
            suppliers[supplierIndex] = new Supplier(supply);
            supplierIndex++;
            return this;
        }

    }
}
