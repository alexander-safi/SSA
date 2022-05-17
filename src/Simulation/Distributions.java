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

    // https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/distribution/NormalDistribution.html
}
