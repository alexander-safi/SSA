package Simulation.Distributions;
import java.util.Random;

import Simulation.CDistribution;

public class Normal implements CDistribution {
	double mean;
	double std;

	/**
	*	Constructor
	*	@param mean	Mean for normal distribution
	*	@param std	Standard deviation for normal distribution
	*/
	public Normal(double mean, double std) {
		this.mean = mean;
		this.std = std;
	}

    public double draw() {
        Random random = new Random();
		return random.nextGaussian() * std + mean;
    }
}
