package Simulation.Distributions;
import java.util.Random;

import Simulation.CDistribution;

public class Poisson implements CDistribution {
	double rate;

	/**
	*	Constructor
	*	@param rate	Mean rate parameter for Poisson distribution
	*/
	public Poisson(double rate) {
		this.rate = rate;
	}

	/**
     * Method that generates Poisson-distributed random variables
     *
     * @return Poisson-distributed random variables
	 * https://stackoverflow.com/questions/9832919/generate-poisson-arrival-in-java
	 * https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
     */
    public double draw() {
        Random r = new Random();
		double L = Math.exp(-rate);
		int k = 0;
		double p = 1.0;
		do {
			p = p * r.nextDouble();
			k++;
		} while (p > L);
		return k - 1;
    }
}
