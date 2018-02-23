package main;

import main.Solver.GeneticSolver;


public class Application {
    public static void main (String... args){
        GeneticSolver solver = new GeneticSolver();
        while (true){
            solver.evolve();
            // solver.getPopulation()[0].print();
            // Arrays.stream(solver.getPopulation()).mapToInt(Chromosome::getFitness).reduce(0, (a, b) -> a + b);
        }
    }
}
