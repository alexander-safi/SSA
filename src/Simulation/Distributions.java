package Simulation;

import java.util.Random;

public class Distributions {

    /**
     * Method that generates Poisson-distributed random variables
     *
     * @param rate Happening rate of the specified event in a minute
     * @return Poisson-distributed random variables
     * https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
     */
    public static double poisson(double rate) {
        Random random = new Random();
        double l = Math.exp(-rate);
        int k = 0;
        double p = 1;
        do {
            p = p * random.nextDouble();
            k++;
        }
        while (p > l);


        return k - 1;
    }

    public static double normal(double mean, double std) {
        double x = Math.random();
        double num = 1 / (Math.sqrt(std) * Math.sqrt(2 * Math.PI));
        num *= Math.exp(-(1 / 2) * (Math.pow((x - mean) / Math.sqrt(std), 2)));

        return num;
    }

    // https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/distribution/NormalDistribution.html
}
