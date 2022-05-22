/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package Simulation;
import Simulation.Distributions.*;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;
	

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	// Create an eventlist
		CEventList l = new CEventList();

		// Define the service queue
		Queue[] serviceQueues = new Queue[1];
		serviceQueues[0] = new Queue(true, null);

		// Define all the queues
		Queue[] regularQueues = new Queue[6];
		regularQueues[0] = (new Queue(true, null));
		regularQueues[1] = (new Queue(true, null));
		regularQueues[2] = (new Queue(false, null));
		regularQueues[3] = (new Queue(false, null));
		regularQueues[4] = (new Queue(false, null));
		regularQueues[5] = (new Queue(true, serviceQueues[0])); // Part of the combined register
		
		Source regularSource = new Source(
			regularQueues, "regular", l,
			new Poisson(1),
			"Regular source"
		);

		Source serviceSource = new Source(
			serviceQueues, "service", l,
			new Poisson(5),
			"Service source"
		);

		// A sink
		Sink regularSink = new Sink("Regular sink");
		Sink serviceSink = new Sink("Service sink");
		
		// A machine
		Machine m1 = new Machine(regularQueues[0], regularSink, l, new Normal(2.6, 1.1), "Machine 1");
		Machine m2 = new Machine(regularQueues[1], regularSink, l, new Normal(2.6, 1.1), "Machine 2");
		Machine m3 = new Machine(regularQueues[2], regularSink, l, new Normal(2.6, 1.1), "Machine 3");
		Machine m4 = new Machine(regularQueues[3], regularSink, l, new Normal(2.6, 1.1), "Machine 4");
		Machine m5 = new Machine(regularQueues[4], regularSink, l, new Normal(2.6, 1.1), "Machine 5");

		Machine m6 = new Machine(regularQueues[5], serviceQueues[0], regularSink, serviceSink, l, new Normal(2.6, 1.1), new Normal(4.6, 1.1), "Machine 6");

		// start the eventlist
		l.start(500); // Maximum time

		// Print the output measures
		System.out.println();
		System.out.println("---------- OUTPUT MEASURES ----------");
		System.out.println("Mean delay of regular customers: " + regularSink.getMeanDelay());
		System.out.println("Mean delay of service customers: " + serviceSink.getMeanDelay());
		double meanDelay = (regularSink.getTotalDelay() + serviceSink.getTotalDelay()) / (regularSink.getNumberOfProducts() + serviceSink.getNumberOfProducts());
		System.out.println("Mean delay overall: " + meanDelay);
    }
}
