package Simulation;

/**
 *	Blueprint for distributions
 *	Classes that implement this interface can create distributions
 */
public interface CDistribution
{
	/**
	*	Method to draw a random number for the specific distribution
	*/
	public double draw();
}
