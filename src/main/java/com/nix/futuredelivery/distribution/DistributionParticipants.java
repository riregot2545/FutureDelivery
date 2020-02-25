package com.nix.futuredelivery.distribution;

public class DistributionParticipants {
    public Consumer[] consumers;
    public Supplier[] suppliers;

    public int consumersCount(){
        return consumers.length;
    }
    public int suppliersCount(){
        return suppliers.length;
    }
}
