package main.Solver;

import main.Configuration;

import java.util.Arrays;

public class GeneticSolver {

    private Chromosome[] population;
    private int generation = 0;

    // Configuration shortcuts
    private int gridSize = Configuration.instance.size;
    private double mutationRatio = Configuration.instance.mutationRatio;
    private double crossoverRatio = Configuration.instance.crossoverRatio;
    private double elitismRatio = Configuration.instance.elitismRatio;

    public GeneticSolver() {
        population = new Chromosome[Configuration.instance.populationSize];
        for (int i = 0; i < population.length; i++) {
            population[i] = Chromosome.produceRandom();
        }
        Arrays.sort(population);
    }

    public void evolve() {
        Chromosome[] chromosomes = new Chromosome[population.length];
        int index = (int) (population.length * elitismRatio);
        System.arraycopy(population, 0, chromosomes, 0, index);

        while (index < chromosomes.length) {
            if (Configuration.instance.random.nextFloat() <= crossoverRatio) {
                Chromosome[] parents = selectParents();
                Chromosome[] children = parents[0].crossover(parents[1]);

                if (Configuration.instance.random.nextFloat() <= mutationRatio)
                    chromosomes[(index++)] = children[0].mutate();
                else
                    chromosomes[(index++)] = children[0];

                if (index < chromosomes.length)
                    if (Configuration.instance.random.nextFloat() <= mutationRatio)
                        chromosomes[index] = children[1].mutate();
                    else
                        chromosomes[index] = children[1];
            } else if (Configuration.instance.random.nextFloat() <= mutationRatio) {
                chromosomes[index] = population[index].mutate();
            } else {
                chromosomes[index] = population[index];
            }
            index++;
        }

        Arrays.sort(population);
        population = chromosomes;
        System.out.println("Generation " + generation + " -- " + population[0].getFitness());
        generation++;
    }

    private Chromosome[] selectParents() {
        /**
         *                 int matingRange = (int) (2048 * (1 / (1 + Configuration.instance.alphaScaling * Configuration.instance.random.nextDouble())));
         int parent1 = Configuration.instance.random.nextInt(matingRange);
         int parent2 = Configuration.instance.random.nextInt(matingRange);
         */
        Chromosome[] parents = new Chromosome[2];

        for (int i = 0; i < 2; i++) {
            parents[i] = population[Configuration.instance.random.nextInt(population.length)];
            for (int j = 0; j < 3; j++) {
                int index = Configuration.instance.random.nextInt(population.length);
                if (population[index].compareTo(parents[i]) < 0)
                    parents[i] = population[index];
            }
        }

        return parents;
    }

    public Chromosome[] getPopulation() {
        return population;
    }
}

