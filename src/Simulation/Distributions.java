package Simulation;
import java.util.Random;

public class Distributions {

    public static double poisson(double rate) {
        Random random = new Random();
        int r = 0;
        double a = random.nextDouble();
        double p = Math.exp(-rate);
        while (a > p) {
            r++;
            a = a - p;
            p = p * rate / r;
        }
        return r;
    }
    public static double normal(double mean, double std){
        double x = Math.random();
        double num = 1 / (Math.sqrt(std) * Math.sqrt(2*Math.PI));
        num *= Math.exp(-(1/2) * (Math.pow((x-mean)/Math.sqrt(std), 2)));

        return num;
    }

    // https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/distribution/NormalDistribution.html
}
