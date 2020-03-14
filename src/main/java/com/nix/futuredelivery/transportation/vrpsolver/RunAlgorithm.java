package com.nix.futuredelivery.transportation.vrpsolver;

public class RunAlgorithm {
    public static void main(String[] args) {
        System.out.println("Optimized distance for travel: " + SimulatedAnnealing.simulateAnnealing(10, 10000000, 0.9995));
    }
}
