package com.nix.futuredelivery.distribution.tsolver.model;

import java.util.Arrays;

public class PotentialArray {
    private double [] array;
    public static final double NULL_PLACEHOLDER = -Double.MAX_VALUE;

    public PotentialArray(int size) {
        this.array = new double[size];
        Arrays.fill(this.array,NULL_PLACEHOLDER);
    }

    public double get(int index){
        if(index<0)
            throw new IllegalArgumentException("Array index must be positive integer");
        else
            return array[index];
    }

    public void set(int index, double value){
        if(index<0)
            throw new IllegalArgumentException("Array index must be positive integer");
        else
            array[index] = value;
    }

    public boolean isNull(int index){
        if(index<0)
            throw new IllegalArgumentException("Array index must be positive integer");

        return array[index]==NULL_PLACEHOLDER;
    }

    public void clear(){
        Arrays.fill(array, NULL_PLACEHOLDER);
    }

    public int findIndexOfNull(){
        for (int i = 0; i < array.length; i++) {
            if (array[i] == NULL_PLACEHOLDER)
                return i;
        }
        return -1;
    }
}
