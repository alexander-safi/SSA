package Simulation;

import java.util.ArrayList;

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
	private ArrayList<ProductAcceptor> queues;
	private ProductAcceptor regularQueue;
	private ProductAcceptor serviceQueue;
	/** Name of the source */
	private String name;
	/** Mean interarrival time */
	private double meanArrTime;
	/** Interarrival times (in case pre-specified) */
	private double[] interarrivalTimes;
	/** Interarrival time iterator */
	private int interArrCnt;

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with mean 33
	*	@param regularQueues Queues of regular registries
	*	@param regulardQueue regular queue of special registry
	*	@param serviceQueue service queue of special registry 
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*/
	public Source(ArrayList<ProductAcceptor> regularQueues, Queue regularQueue, Queue serviceQueue, CEventList l, String n)
	{
		list = l;
		queues = regularQueues;
		this.regularQueue = regularQueue;
		this.serviceQueue = serviceQueue;
		name = n;
		meanArrTime=33;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with specified mean
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param m	Mean arrival time
	*/
	public Source(ArrayList<ProductAcceptor> q,CEventList l,String n,double m)
	{
		list = l;
		queues = q;
		name = n;
		meanArrTime=m;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are prespecified
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param ia	interarrival times
	*/
	public Source(ArrayList<ProductAcceptor> q,CEventList l,String n,double[] ia)
	{
		list = l;
		queues = q;
		name = n;
		meanArrTime=-1;
		interarrivalTimes=ia;
		interArrCnt=0;
		// put first event in list for initialization
		list.add(this,0,interarrivalTimes[0]); //target,type,time
	}
	
        @Override
	public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Arrival at time = " + tme);
		// give arrived product to queue
		boolean regularCustomer = Math.random() > 0.5;
		
		Product p = new Product(regularCustomer);
		p.stamp(tme,"Creation",name);

		chooseQueue(p);
		// generate duration
		if(meanArrTime>0)
		{
			double duration = drawRandomExponential(meanArrTime);
			// Create a new event in the eventlist
			list.add(this,0,tme+duration); //target,type,time
		}
		else
		{
			interArrCnt++;
			if(interarrivalTimes.length>interArrCnt)
			{
				list.add(this,0,tme+interarrivalTimes[interArrCnt]); //target,type,time
			}
			else
			{
				list.stop();
			}
		}
	}
	public void chooseQueue(Product p){
		ArrayList<Queue> openQueues = new ArrayList<>();
		//Define open queues
		for(Queue q: queues){
			if(q.isOpen()){
				openQueues.add(q);
			}
		}
		if(!p.isRegular()){
			serviceQueue.giveProduct(p);
		} else{
			//Find shortest queue
			Queue shortestQueue = new Queue(true);
			int size = Integer.MAX_VALUE;
			for(Queue q: openQueues){
				if(q.getSize()  < size){
					shortestQueue = q;
					size = g.getSize;
				}
			}
			//Also look at the combined queue of the service registry
			if(regularQueue.size() + serviceQueue.size() < size){
				shortestQueue = regularQueue;
				size = regularQueue.size() + serviceQueue.size();
			}
			//Give product to shortest queue
			shortestQueue.giveProduct(p);
		}
	}
	
	public static double drawRandomExponential(double mean)
	{
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean 33
		double res = -mean*Math.log(u);
		return res;
	}
}