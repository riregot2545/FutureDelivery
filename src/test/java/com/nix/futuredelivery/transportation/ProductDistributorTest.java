package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.transportation.tsolver.ProductDistributor;
import com.nix.futuredelivery.transportation.tsolver.model.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductDistributorTest {


    private void showPlan(DistributionPlan plan) {
        for (int i = 0; i < plan.getHeight(); i++) {
            for (int j = 0; j < plan.getWidth(); j++) {
                if (!plan.getCell(i, j).isFullnessNull())
                    System.out.print(plan.getCell(i, j).getFullness() + "\t\t\t");
                else
                    System.out.print("0\t\t\t");
            }
            System.out.println();
        }
    }

    @Test
    public void testMedium4x4() throws PotentialConflictException {
        DistributionParticipants participants = new DistributionParticipants(
                Arrays.stream(new int[]{105, 60, 165, 90})
                        .mapToObj(d->new Consumer(d,null,true)).toArray(Consumer[]::new),
                Arrays.stream(new int[]{90, 60, 120, 150})
                        .mapToObj(d->new Supplier(d,null)).toArray(Supplier[]::new)
        );
        double[][] costMatrix = new double[][]{
                {6, 12, 3, 9},
                {15, 18, 15, 12},
                {9, 21, 27, 15},
                {3, 6, 6, 21}
        };

        int[][] expectedFullness = new int[][]{
                {0, 0, 90, 0},
                {0, 0, 0, 60},
                {90, 0, 0, 30},
                {15, 60, 75, 0}
        };

        DistributionPlan expectedPlan = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness,participants);
        double expectedCost = 3105D;



        ProductDistributor productDistributor = new ProductDistributor(costMatrix, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();


        showPlan(distributionPlan);
        assertEquals(expectedCost, distributionPlan.totalCost(), 0.001D);
        assertTrue(deepPlanEquals(expectedPlan,distributionPlan));
    }


    @Test
    public void testSimple4x4() throws PotentialConflictException {
        DistributionParticipants participants = new DistributionParticipants(
                Arrays.stream(new int[]{40,30,35,15})
                        .mapToObj(d->new Consumer(d,null,true)).toArray(Consumer[]::new),
                Arrays.stream(new int[]{50,20,30,20})
                        .mapToObj(d->new Supplier(d,null)).toArray(Supplier[]::new)
        );
       double[][] costMatrix = new double[][]{
                {1,3,3,4},
                {5,2,7,5},
                {6,4,8,2},
                {7,1,5,7},
        };

        int[][] expectedFullness = new int[][]{
                {40,0,10,0},
                {0,20,0,0},
                {0,10,5,15},
                {0,0,20,0}
        };

        int[][] expectedFullness2 = new int[][]{
                {40,0,10,0},
                {0,20,0,0},
                {0,0,15,15},
                {0,10,10,0}
        };

        DistributionPlan expectedPlan1 = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness,participants);
        DistributionPlan expectedPlan2 = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness2,participants);
        double expectedCost = 320D;



        ProductDistributor productDistributor = new ProductDistributor(costMatrix, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();


        showPlan(distributionPlan);
        assertEquals(expectedCost, distributionPlan.totalCost(), 0.001D);
        assertTrue(deepPlanEquals(expectedPlan1,distributionPlan) ||
                deepPlanEquals(expectedPlan2,distributionPlan));
    }


    @Test
    public void testSimple4x3() throws PotentialConflictException {
        DistributionParticipants participants = new DistributionParticipants(
                Arrays.stream(new int[]{15, 15, 40, 30})
                        .mapToObj(d->new Consumer(d,null,true)).toArray(Consumer[]::new),
                Arrays.stream(new int[]{30, 50, 20})
                        .mapToObj(d->new Supplier(d,null)).toArray(Supplier[]::new)
        );
       double[][] costMatrix = new double[][]{
                {1, 8, 2, 3D},
                {4, 7, 5, 1D},
                {6, 3, 4, 4D}
        };

        int[][] expectedFullness1 = new int[][]{
                {15,0,15,0},
                {0,0,20,30},
                {0,15,5,0},
        };
        int[][] expectedFullness2 = new int[][]{
                {0,0,30,0},
                {15,0,5,30},
                {0,15,5,0},
        };

        DistributionPlan expectedPlan1 = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness1,participants);
        DistributionPlan expectedPlan2 = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness2,participants);
        double expectedCost = 240D;


        ProductDistributor productDistributor = new ProductDistributor(costMatrix, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();


        showPlan(distributionPlan);
        assertEquals(expectedCost, distributionPlan.totalCost(), 0.001D);
        assertTrue(deepPlanEquals(expectedPlan1,distributionPlan) ||
                deepPlanEquals(expectedPlan2,distributionPlan));
    }

    @Test
    public void testBrokenCycle() throws PotentialConflictException {
        DistributionParticipants participants = new DistributionParticipants(
                Arrays.stream(new int[]{10,8,12,14,16})
                        .mapToObj(d->new Consumer(d,null,true)).toArray(Consumer[]::new),
                Arrays.stream(new int[]{12,17,18,13})
                        .mapToObj(d->new Supplier(d,null)).toArray(Supplier[]::new)
        );
       double[][] costMatrix = new double[][]{
                {6,11,20,17,8},
                {1,25,3,18,17},
                {9,29,16,30,31},
                {23,15,4,3,28}
        };

        int[][] expectedFullness = new int[][]{
                {0,0,0,0,12},
                {0,0,12,1,4},
                {10,8,0,0,0},
                {0,0,0,13,0}
        };

        DistributionPlan expectedPlan = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness,participants);
        double expectedCost = 579D;


        ProductDistributor productDistributor = new ProductDistributor(costMatrix, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();


        showPlan(distributionPlan);
        assertEquals(expectedCost, distributionPlan.totalCost(), 0.001D);
        assertTrue(deepPlanEquals(expectedPlan,distributionPlan));
    }

    @Test
    public void testMedium4x3() throws PotentialConflictException {
        DistributionParticipants participants = new DistributionParticipants(
                Arrays.stream(new int[]{30, 10, 20, 40})
                        .mapToObj(d->new Consumer(d,null,true)).toArray(Consumer[]::new),
                Arrays.stream(new int[]{35, 50, 15})
                        .mapToObj(d->new Supplier(d,null)).toArray(Supplier[]::new)
        );
       double[][] costMatrix = new double[][]{
                {1, 3, 2, 4},
                {2, 1, 4, 3},
                {3, 5, 6, 1}
        };

        int[][] expectedFullness = new int[][]{
                {15,0,20,0},
                {15,10,0,25},
                {0,0,0,15}
        };

        DistributionPlan expectedPlan = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness,participants);
        double expectedCost = 185D;


        ProductDistributor productDistributor = new ProductDistributor(costMatrix, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();


        showPlan(distributionPlan);
        assertEquals(expectedCost, distributionPlan.totalCost(), 0.001D);
        assertTrue(deepPlanEquals(expectedPlan,distributionPlan));
    }

    @Test
    public void testHard12x12() throws PotentialConflictException {
        DistributionParticipants participants = new DistributionParticipants(
                Arrays.stream(
                        new int[]{1000,1000,1000,1050,1070,1130,1110,1170,1120,1050,1150,1150})
                        .mapToObj(d->new Consumer(d,null,true)).toArray(Consumer[]::new),
                Arrays.stream(
                        new int[]{1400,1500,1100,1000,1000,1000,1000,1000,1000,1000,1000,1000})
                        .mapToObj(d->new Supplier(d,null)).toArray(Supplier[]::new)
        );
        double[][] costMatrix = new double[][]{
                {0,5,6,2,7,8,5,2,2,1,8,6},
                {5,0,5,3,2,2,2,4,2,7,8,5},
                {6,5,0,5,4,7,8,9,5,7,2,2},
                {2,3,5,0,3,4,4,5,1,2,4,1},
                {7,2,4,3,0,2,3,6,3,6,5,5},
                {8,2,7,4,2,0,2,5,5,7,6,6},
                {5,2,8,4,3,2,0,2,1,4,9,8},
                {2,4,9,5,6,5,2,0,1,3,8,6},
                {2,2,5,1,3,5,1,1,0,3,6,5},
                {1,7,7,2,6,7,4,3,3,0,7,5},
                {8,8,2,4,5,6,9,8,6,7,0,2},
                {6,5,2,1,5,6,8,6,5,5,2,0}
        };

        int[][] expectedFullness = new int[][]{
                {1000,0,0,180,0,0,0,170,0,50,0,0,},
                {0,1000,0,70,70,130,110,0,120,0,0,0,},
                {0,0,1000,0,0,0,0,0,0,0,100,0,},
                {0,0,0,800,0,0,0,0,0,0,0,200,},
                {0,0,0,0,1000,0,0,0,0,0,0,0,},
                {0,0,0,0,0,1000,0,0,0,0,0,0,},
                {0,0,0,0,0,0,1000,0,0,0,0,0,},
                {0,0,0,0,0,0,0,1000,0,0,0,0,},
                {0,0,0,0,0,0,0,0,1000,0,0,0,},
                {0,0,0,0,0,0,0,0,0,1000,0,0,},
                {0,0,0,0,0,0,0,0,0,0,1000,0,},
                {0,0,0,0,0,0,0,0,0,0,50,950}
        };

        DistributionPlan expectedPlan = DistributionPlan.fromCostFullnessArray(costMatrix,expectedFullness,participants);
        double expectedCost = 2320D;


        ProductDistributor productDistributor = new ProductDistributor(costMatrix, participants);
        DistributionPlan distributionPlan = productDistributor.distribute();


        showPlan(distributionPlan);
        assertEquals(expectedCost, distributionPlan.totalCost(), 0.001D);
        assertTrue(deepPlanEquals(expectedPlan,distributionPlan));
    }

    private boolean deepPlanEquals(DistributionPlan p1, DistributionPlan p2) {
        if (p1.getHeight() != p2.getHeight() && p1.getWidth() != p2.getWidth())
            return false;
        for (int i = 0; i < p1.getHeight(); i++) {
            for (int j = 0; j < p1.getWidth(); j++) {
                if(p1.getCell(i,j).getFullness() != p2.getCell(i,j).getFullness())
                    return false;
            }
        }
        return true;
    }
}