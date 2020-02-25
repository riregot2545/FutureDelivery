package com.nix.futuredelivery.distribution;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class CycleMover {
    private DistributionPlan potentialPlan;
    private DistributionParticipants participants;



    public CycleMover(DistributionPlan potentialPlan, DistributionParticipants participants) {
        this.potentialPlan = potentialPlan;
        this.participants = participants;
    }

    public DistributionPlan cycle(DistributionCell cell) {
       throw new NotImplementedException();
    }
}
