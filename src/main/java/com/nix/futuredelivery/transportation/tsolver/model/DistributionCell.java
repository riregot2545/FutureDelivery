package com.nix.futuredelivery.transportation.tsolver.model;

import lombok.*;

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
