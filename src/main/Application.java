package main;

import main.Solver.Chromosome;
import main.Solver.GeneticSolver;


public class Application {
    public static void main (String... args){
        GeneticSolver solver = new GeneticSolver();
        while (true){
            solver.evolve();
            // solver.getPopulation()[0].print();
            // Arrays.stream(solver.getPopulation()).mapToInt(Chromosome::getFitness).reduce(0, (a, b) -> a + b);
            /*solver.getPopulation()[0].print();
            for (Chromosome c : solver.getPopulation()){
                System.out.print(c.getFitness() + " ");
            }
            System.out.println();*/
        }
        /*System.out.println(1<<Configuration.instance.random.nextInt(2*Configuration.instance.offset));
        double rnd = Configuration.instance.random.nextDouble();
        for (int i = 0; i < 100; i++){
            // System.out.println(rnd & 1);
        }

        solver.getPopulation()[0].print();
        System.out.println(solver.getPopulation()[0].fitness);
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                System.out.print(solver.getPopulation()[0].gene[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println(solver.population[2047].fitness);
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                System.out.print(solver.population[2047].gene[i][j] + " ");
            }
            System.out.println();
        }*/
    }
}
