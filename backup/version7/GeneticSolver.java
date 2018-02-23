package main.Solver;

import main.Configuration;
import main.util.MersenneTwister;

import java.util.Arrays;
import java.util.Comparator;

public class GeneticSolver {

    private int[][][] population;
    private int generation = 0;

    // Configuration shortcuts
    private double mutationRatio = Configuration.instance.mutationRatio;
    private double crossoverRatio = Configuration.instance.crossoverRatio;
    private double elitismRatio = Configuration.instance.elitismRatio;
    private MersenneTwister random = Configuration.instance.random;
    private int gridSize = Configuration.instance.size;
    private int bitOffset = Configuration.instance.offset;
    private int symbol1Mask = Configuration.instance.symbol1Mask;
    private int symbol2Mask = Configuration.instance.symbol2Mask;


    public GeneticSolver() {
        population = new int[Configuration.instance.populationSize][gridSize][gridSize];
        for (int i = 0; i < population.length; i++) {
            population[i] = produceRandom();
        }
        Arrays.sort(population, Comparator.comparingInt(this::calculateFitness));
    }

    public void evolve() {
        int[][][] chromosomes = new int[population.length][gridSize][gridSize];
        int index = (int) (population.length * elitismRatio);
        System.arraycopy(population, 0, chromosomes, 0, index);

        while (index < chromosomes.length) {
            if (Configuration.instance.random.nextFloat() <= crossoverRatio) {
                int[][][] parents = selectParents();
                int[][][] children = crossover(parents[0], parents[1]);

                if (Configuration.instance.random.nextFloat() <= mutationRatio)
                    chromosomes[(index++)] = mutate(children[0]);
                else
                    chromosomes[(index++)] = children[0];

                if (index < chromosomes.length)
                    if (Configuration.instance.random.nextFloat() <= mutationRatio)
                        chromosomes[index] = mutate(children[1]);
                    else
                        chromosomes[index] = children[1];
            } else if (Configuration.instance.random.nextFloat() <= mutationRatio) {
                chromosomes[index] = mutate(population[index]);
            } else {
                chromosomes[index] = population[index]; // Chromosome.produceRandom();// population[index];
            }
            index++;
        }
        Arrays.sort(chromosomes, new Comparator<int[][]>() {
            public int compare(int[][] candidate1, int[][] candidate2) {
                return Integer.compare(calculateFitness(candidate1), calculateFitness(candidate2));
            }
        });
        population = chromosomes;
        System.out.println("Generation " + generation + " -- " + calculateFitness(population[0]) + " -- " + calculateFitness(population[1023]));
        generation++;
    }

    private int[][][] selectParents() {
        /*
         int matingRange = (int) (2048 * (1 / (1 + Configuration.instance.alphaScaling * Configuration.instance.random.nextDouble())));
         int parent1 = Configuration.instance.random.nextInt(matingRange);
         int parent2 = Configuration.instance.random.nextInt(matingRange);
         */
        int[][][] parents = new int[2][gridSize][gridSize];

        for (int i = 0; i < 2; i++) {
            parents[i] = population[Configuration.instance.random.nextInt(population.length)];
            for (int j = 0; j < 3; j++) {
                int index = Configuration.instance.random.nextInt(population.length);
                if (calculateFitness(population[index]) < calculateFitness(parents[i]))
                    parents[i] = population[index];
            }
        }

        return parents;
    }

    private int[][] produceRandom() {
        int[][] gene = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                gene[i][j] = (random.nextInt(gridSize) << bitOffset) + random.nextInt(gridSize);
            }
        }
        return gene;
    }

    /**
     * LOW fitness is desirable -> high fitness is bad.
     */
    private int calculateFitness(int[][] gene) {
        int fitness = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                fitness += tileFitness(gene, i, j);
            }
        }
        return fitness;
    }

    private int tileFitness(int[][] gene, int x, int y) {
        int c1, c2, r1, r2, d; // placeholders for possible rule violations
        c1 = c2 = r1 = r2 = d = 0;
        int symbol1 = gene[x][y] & symbol1Mask;
        int symbol2 = gene[x][y] & symbol2Mask;
        for (int i = 0; i < gridSize; i++) {
            if (i != x && (symbol1 == (gene[i][y] & symbol1Mask))) { // column symbol 1 error
                c1 += 1;
            }
            if (i != x && (symbol2 == (gene[i][y] & symbol2Mask))) { // column symbol 2 error
                c2 += 1;
            }
            if (i != y && (symbol1 == (gene[x][i] & symbol1Mask))) { // row symbol 1 error
                r1 += 1;
            }
            if (i != y && (symbol2 == (gene[x][i] & symbol2Mask))) { // row symbol 2 error
                r2 += 1;
            }
            for (int j = 0; j < gridSize; j++) { // duplicate error
                if (gene[x][y] == gene[i][j] && x != i && y != i) {
                    d += 1;
                    // break;
                }
            }
        }
        return c1 + c2 + r1 + r2 + d;
    }

    private int[][] mutate(int[][] candidate) {
        for (int i = 0; i < 1 + random.nextInt(gridSize * gridSize); i++) {
            int x = random.nextInt(gridSize);
            int y = random.nextInt(gridSize);
            if (random.nextBoolean()) {
                candidate[x][y] = (random.nextInt(gridSize) << bitOffset) + (candidate[x][y] & symbol2Mask);
            } else if (random.nextBoolean()) {
                candidate[x][y] = random.nextInt(gridSize) + (candidate[x][y] & symbol1Mask);
            } else {
                candidate[x][y] = (random.nextInt(gridSize) << 3) + random.nextInt(gridSize);
            }
        }
        return candidate;
        /*int flip = 1 << random.nextInt(2 * bitOffset);
        int x = random.nextInt(gridSize);
        int y = random.nextInt(gridSize);
        gene[x][y] ^= flip;*/
    }

    private int[][][] crossover(int[][] parent1, int[][] parent2) {
        int[][] child1 = new int[gridSize][gridSize];
        int[][] child2 = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                child1[i][j] = crossoverGene(parent1[i][j], parent2[i][j]);
                child2[i][j] = crossoverGene(parent2[i][j], parent1[i][j]);
            }
        }
        return new int[][][]{child1, child2};
    }

    private int crossoverGene(int gene1, int gene2) {
        if (random.nextBoolean())
            return (gene1 & symbol1Mask) + (gene2 & symbol2Mask);
        return (gene1 & symbol2Mask) + (gene2 & symbol1Mask);
    }
}

