package main.Solver;

import main.Configuration;
import main.util.MersenneTwister;

import java.util.Collections;


public class Chromosome implements Comparable<Chromosome> {
    private int[][] gene;
    private int fitness;

    // Configuration shortcuts
    private MersenneTwister random = Configuration.instance.random;
    private int gridSize = Configuration.instance.size;
    private int bitOffset = Configuration.instance.offset;
    private int symbol1Mask = Configuration.instance.symbol1Mask;
    private int symbol2Mask = Configuration.instance.symbol2Mask;


    private Chromosome(int[][] gene) {
        this.gene = gene;
        fitness = calculateFitness();
    }

    static Chromosome produceRandom(){
        Collections.shuffle(GeneticSolver.possibleGenes);
        int gridSize = Configuration.instance.size;
        int[][] gene = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                gene[i][j] = GeneticSolver.possibleGenes.get((i * gridSize) + j);
            }
        }
        return new Chromosome(gene);
    }

    Chromosome[] crossover(Chromosome mate){
        int[][] child1 = new int[gridSize][gridSize];
        int[][] child2 = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++){
            for (int j = 0; j < gridSize; j++){
                boolean selection = random.nextBoolean();
                child1[i][j] = selection ? gene[i][j] : mate.gene[i][j];
                child2[i][j] = selection ? mate.gene[i][j] : gene[i][j];
                // child1[i][j] = crossoverGene(gene[i][j], mate.gene[i][j]);
                // child2[i][j] = crossoverGene(mate.gene[i][j], gene[i][j]);
            }
        }
        return new Chromosome[] { new Chromosome(child1),
                new Chromosome(child2) };
    }

    /**
     * LOW fitness is desirable -> high fitness is bad.
     */
    private int calculateFitness() {
        int fitness = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                fitness += tileFitness(i, j);
            }
        }
        return fitness;
    }

    /**
     * TODO: Explore counting multiple row/column/duplicate rule violations -> tile fitness > 5 possible
     */
    private int tileFitness(int x, int y) {
        int c1, c2, r1, r2, d; // placeholders for possible rule violations
        c1 = c2 = r1 = r2 = d = 0;
        int symbol1 = gene[x][y] & symbol1Mask;
        int symbol2 = gene[x][y] & symbol2Mask;
        for (int i = 0; i < gridSize; i++) {
            if (i != x && (symbol1 == (gene[i][y] & symbol1Mask))) { // column symbol 1 error
                c1 = 1;
            }
            if (i != x && (symbol2 == (gene[i][y] & symbol2Mask))) { // column symbol 2 error
                c2 = 1;
            }
            if (i != y && (symbol1 == (gene[x][i] & symbol1Mask))) { // row symbol 1 error
                r1 = 1;
            }
            if (i != y && (symbol2 == (gene[x][i] & symbol2Mask))) { // row symbol 2 error
                r2 = 1;
            }
            for (int j = 0; j < gridSize; j++) { // duplicate error
                if (gene[x][y] == gene[i][j] && x != i && y != i) {
                    d = 1;
                    break;
                }
            }
        }
        return c1 + c2 + r1 + r2 + d;
    }

    public int compareTo(Chromosome chromosome) {
        return Integer.compare(fitness, chromosome.fitness);
    }

    public void print(){
        for (int i = 0; i < gridSize; i++){
            for (int j = 0; j < gridSize; j++){
                System.out.print(1 + (gene[i][j] & symbol2Mask) + Character.toString((char) (65 + (gene[i][j] >> 3))) + " ");
            }
            System.out.println();
        }
    }

    public int[][] getGene(){
        return gene;
    }

    public int getFitness() {
        return fitness;
    }
}
