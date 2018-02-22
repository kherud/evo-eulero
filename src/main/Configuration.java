package main;

import main.util.MersenneTwister;

public enum Configuration {
    instance;

    public MersenneTwister random = new MersenneTwister();

    public int size = 5;
    public int offset = Integer.SIZE - Integer.numberOfLeadingZeros(size);
    public int symbol2Mask = (1 << offset) - 1;
    public int symbol1Mask = ((1 << offset * 2) - 1) ^ symbol2Mask;

    public int populationSize = 2048;
    public double mutationRatio = 0.5;
    public double crossoverRatio = 0.8;
    public double elitismRatio = 0.1;
    public double alphaScaling = 5; // self-designed scaling factor to more likely pick fitter candidates for crossover
}
