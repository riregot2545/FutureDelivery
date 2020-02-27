package com.nix.futuredelivery.distribution.tsolver.model;

import lombok.*;

import java.beans.Transient;

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

    public boolean isFullnessEmpty(){
        return fullness == EMPTY_FULLNESS_PLACEHOLDER;
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