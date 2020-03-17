package com.nix.futuredelivery.transportation.tsolver.model;

import lombok.*;

/**
 * Transportation solver model that represent one logical unit of distribution plan.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class DistributionCell {
    public static final int EMPTY_FULLNESS_PLACEHOLDER = -1;

    private final MatrixPosition position;

    @Getter
    private final double tariffCost;
    @Getter
    @Setter
    private int fullness;
    @Getter
    @Setter
    private double potentialSum;

    /**
     * Returns cell cost depend on tariff cost and it's fullness
     *
     * @return product of tariffCost and fullness.
     */
    public double getCellCost() {
        return fullness == EMPTY_FULLNESS_PLACEHOLDER ? 0 : tariffCost * fullness;
    }

    public boolean isFullnessNull(){
        return fullness == EMPTY_FULLNESS_PLACEHOLDER;
    }

    public boolean isFullnessEmpty(){
        return fullness == 0;
    }

    public void setFullnessEmpty(){
        fullness = EMPTY_FULLNESS_PLACEHOLDER;
    }

    public int getX(){
        return position.getX();
    }

    public int getY(){
        return position.getY();
    }

}
