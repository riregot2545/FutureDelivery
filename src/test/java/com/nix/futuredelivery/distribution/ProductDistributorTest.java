package com.nix.futuredelivery.distribution;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ProductDistributorTest {

    private final double nullPlaceholder = -1111.221122;

    @Test
    public void distribute() {

        DistributionParticipants participants = new DistributionParticipants();
        participants.consumers = Arrays.stream(new double[]{105, 60, 165, 90})
                .mapToObj(Consumer::new).toArray(Consumer[]::new);

        participants.suppliers = Arrays.stream(new double[]{90, 60, 120, 150})
                .mapToObj(Supplier::new).toArray(Supplier[]::new);


        DistributionCell[][] distributionCells = Arrays.stream(new double[][]{
                {6, 12, 3, 9},
                {15, 18, 15, 12},
                {9, 21, 27, 15},
                {3, 6, 6, 21}
        }).map(i -> Arrays.stream(i).mapToObj(DistributionCell::new).toArray(DistributionCell[]::new))
                .toArray(DistributionCell[][]::new);


        for (int i = 0; i < distributionCells.length; i++) {
            for (int l = 0; l < distributionCells[0].length; l++) {
                distributionCells[i][l].fullness = nullPlaceholder;
                distributionCells[i][l].x = i;
                distributionCells[i][l].y = l;
            }
        }


        ProductDistributor productDistributor = new ProductDistributor(distributionCells, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();
        showPlan(distributionPlan);
    }

    private void showPlan(DistributionPlan plan) {
        for (int i = 0; i < plan.height; i++) {
            for (int j = 0; j < plan.width; j++) {
                if (plan.plan[i][j].fullness != nullPlaceholder)
                    System.out.print(plan.plan[i][j].fullness+"\t\t\t");
                else
                    System.out.print("0\t\t\t");
            }
            System.out.println();
        }
    }
}