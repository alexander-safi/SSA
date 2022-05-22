package Simulation;
import java.util.ArrayList;
import java.util.Random;

/**
 *	A source of products
 *	This class implements CProcess so that it can execute events.
 *	By continuously creating new events, the source keeps busy.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Source implements CProcess
{
	/** Eventlist that will be requested to construct events */
	private CEventList list;
	/** Queue that buffers products for the machine */
	private ProductAcceptor queue;
	/** Queues that buffer products for the machine */
	private ProductAcceptor[] queues;
	/** Type of products to generate */
	private String type;
	/** Name of the source */
	private String name;
	/** Interarrival time distribution */
	private CDistribution interarrDistr;
	/** This is a variable to calcualte average queue length later on */
	private ArrayList<Integer> queueLengths;

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with mean 33
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param d1	Interarrival distribution
	*	@param d2	Service time distribution
	*	@param n	Name of object
	*/
	public Source(ProductAcceptor q, CEventList l, CDistribution d, String n)
	{
		list = l;
		queue = q;
		name = n;
		interarrDistr = d;
		queueLengths = new ArrayList<Integer>();
		//meanArrTime=33;
		// put first event in list for initialization
		//list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
		list.add(this,0,interarrDistr.draw()); //target,type,time
	}

	public Source(ProductAcceptor[] qs, String t, CEventList l, CDistribution d, String n)
	{
		list = l;
		queues = qs;
		name = n;
		interarrDistr = d;
		type = t;
		queueLengths = new ArrayList<Integer>();
		//meanArrTime=33;
		// put first event in list for initialization
		//list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
		list.add(this,0,interarrDistr.draw()); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with specified mean
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param m	Mean arrival time
	*/
	/*
	public Source(ProductAcceptor q,CEventList l,String n,double m)
	{
		list = l;
		queue = q;
		name = n;
		meanArrTime=m;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}
	*/

	/**
	*	Constructor, creates objects
	*        Interarrival times are prespecified
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param ia	interarrival times
	*/
	/*
	public Source(ProductAcceptor q,CEventList l,String n,double[] ia)
	{
		list = l;
		queue = q;
		name = n;
		meanArrTime=-1;
		interarrivalTimes=ia;
		interArrCnt=0;
		// put first event in list for initialization
		list.add(this,0,interarrivalTimes[0]); //target,type,time
	}
	*/
	
        @Override
	public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Arrival at time = " + tme + " (" + this.type + ")");
		// give arrived product to queue
		Product p = new Product(this.type);
		p.stamp(tme,"Creation",name);
		openOrCloseQueues();
		getShortestOpenQueue().giveProduct(p);
		// generate duration
		double duration = interarrDistr.draw();
		// Create a new event in the eventlist
		list.add(this,0,tme+duration); //target,type,time
	}

	private ProductAcceptor getShortestOpenQueue() {
		ProductAcceptor shortestQueue = null;

		for(int i=0; i<queues.length; i++) {
			if(((Queue)queues[i]).isOpen()) {
				// If it's the first open queue, select it as shortest for now
				if(shortestQueue == null) {
					shortestQueue = queues[i];
				}

				if(((Queue)queues[i]).getLength() < ((Queue)shortestQueue).getLength()) {
					shortestQueue = queues[i];
				}
			}
		}

		return shortestQueue;
	}

	private int howManyQueuesAreOpen() {
		int openQueues = 0;
		for(int i=0; i<queues.length; i++) {
			if(((Queue)queues[i]).isOpen() == true && !((Queue)queues[i]).isCombined()) {
				openQueues++;
			}
		}

		return openQueues;
	}

	private void openNewQueue() {
		for(int i=0; i<queues.length; i++) {
			if(((Queue)queues[i]).isOpen() == false) {
				((Queue)queues[i]).open();
				System.out.println("Queue " + (i+1) + " opened!");
				return;
			}
		}
	}

	private void openOrCloseQueues() {
		int nOpenQueues = howManyQueuesAreOpen();

		// Check whether to close a specific queue (at least one other queue is less than 4 and this one is 0)
		if(nOpenQueues > 2) {
			for(int i=0; i<queues.length; i++) {
				if(((Queue)queues[i]).isOpen() && ((Queue)queues[i]).getLength() == 0) {
					boolean allOpenedQueuesFullBesidesI = true;
					for(int j=0; j<queues.length; j++) {
						if(i == j)
							continue;
						else {
							if(((Queue)queues[j]).isOpen() && ((Queue)queues[j]).getLength() < 4) {
								allOpenedQueuesFullBesidesI = false;
							}
						}
					}

					if(!((Queue)queues[i]).isCombined() && !allOpenedQueuesFullBesidesI) {
						((Queue)queues[i]).close();
						System.out.println("Queue " + (i+1) + " closed!");
					}
				}
			}
		}

		// Check whether to open a new queue (all opened queues are of length at least 4)
		if(nOpenQueues < queues.length) {
			boolean allOpenedQueuesFull = true;
			for(int i=0; i<queues.length; i++) {
				if(((Queue)queues[i]).isOpen() && ((Queue)queues[i]).getLength() < 4) {
					allOpenedQueuesFull = false;
				}
			}
			if(allOpenedQueuesFull)
				openNewQueue();
		}
		
		// DEBUG: Print state of the queues (and therefore registers)
		if(queues.length > 1) {
			for(int i=0; i<queues.length; i++) {
				boolean isOpen = ((Queue)queues[i]).isOpen();
				int customers = ((Queue)queues[i]).getLength();

				System.out.print("Q" + (i+1) + ": " + isOpen + "(" + customers + "), ");
			}
			System.out.println();
		}
	}

	public void saveQueueLengths() {
		for(int i=0; i<queues.length; i++) {
			boolean isOpen = ((Queue)queues[i]).isOpen();

			if(isOpen) {
				int qLength = ((Queue)queues[i]).getLength();
				queueLengths.add(qLength);
			}
		}
	}

	public int getTotalQueueLength() {
		int totalQueueLength = 0;
		for(int i=0; i<queueLengths.size(); i++) {
			totalQueueLength += queueLengths.get(i);
		}
		return totalQueueLength;
	}
	
	/*
	public static double drawRandomExponential(double mean)
	{
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean 33
		double res = -mean*Math.log(u);
		return res;
	}
	*/
}