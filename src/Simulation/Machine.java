package Simulation;



/**
 *	Machine in a factory
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Machine implements CProcess,ProductAcceptor
{
	/** Product that is being handled  */
	private Product product;
	/** Eventlist that will manage events */
	private final CEventList eventlist;
	/** Queue from which the machine has to take products */
	private Queue regularQueue;
	private Queue serviceQueue;
	/** Sink to dump products */
	private ProductAcceptor sink;
	private ProductAcceptor serviceSink;
	/** Status of the machine (b=busy, i=idle) */
	private char status;
	/** Machine name */
	private final String name;
	/** Mean processing time */
	private double meanProcTime;
	/** Processing times (in case pre-specified) */
	private double[] processingTimes;
	/** Processing time iterator */
	private int procCnt;
	/** Service time distribution */
	private CDistribution serviceDistr;
	private CDistribution serviceDistr2;
	

	/**
	*	Constructor
	*        Service times are exponentially distributed with mean 30
	*	@param q	Queue from which the machine has to take products
	*	@param s	Where to send the completed products
	*	@param e	Eventlist that will manage events
	*	@param n	The name of the machine
	*/
	public Machine(Queue q, ProductAcceptor s, CEventList e, CDistribution d, String n)
	{
		status='i';
		regularQueue=q;
		sink=s;
		eventlist=e;
		name=n;
		meanProcTime=30;
		serviceDistr = d;
		regularQueue.askProduct(this);
	}
	public Machine(Queue regularQ, Queue serviceQ, ProductAcceptor s, ProductAcceptor s2, CEventList e, CDistribution d, CDistribution d2, String n)
	{
		status='i';
		regularQueue=regularQ;
		serviceQueue=serviceQ;
		sink=s;
		serviceSink=s2;
		eventlist=e;
		name=n;
		meanProcTime=30;
		serviceDistr = d;
		serviceDistr2 = d2;

		if(!serviceQueue.askProduct(this))
			regularQueue.askProduct(this);
	}

	/**
	*	Method to have this object execute an event
	*	@param type	The type of the event that has to be executed
	*	@param tme	The current time
	*/
	public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Product finished at time = " + tme);
		// Remove product from system
		product.stamp(tme,"Production complete",name);
		if(product.getType() == "regular")
			sink.giveProduct(product);
		else
			serviceSink.giveProduct(product);
		product=null;
		// set machine status to idle
		status='i';
		// Ask the queue for products
		if(serviceQueue != null) {
			if(!serviceQueue.askProduct(this))
				regularQueue.askProduct(this);
		} else {
			regularQueue.askProduct(this);
		}
	}
	
	/**
	*	Let the machine accept a product and let it start handling it
	*	@param p	The product that is offered
	*	@return	true if the product is accepted and started, false in all other cases
	*/
        @Override
	public boolean giveProduct(Product p)
	{
		// Only accept something if the machine is idle
		if(status=='i')
		{
			// accept the product
			product=p;
			// mark starting time
			product.stamp(eventlist.getTime(),"Production started",name);
			// start production
			startProduction(product.getType());
			// Flag that the product has arrived
			return true;
		}
		// Flag that the product has been rejected
		else return false;
	}
	
	/**
	*	Starting routine for the production
	*	Start the handling of the current product with an exponentionally distributed processingtime with average 30
	*	This time is placed in the eventlist
	*/
	private void startProduction(String type)
	{
		// generate duration
		if(meanProcTime>0)
		{
			double duration;
			duration = serviceDistr.draw();
			if(type == "regular")
				duration = serviceDistr.draw();
			else {
				duration = serviceDistr2.draw();
				// Handle the case when duration is 0
				while(duration == 0)
					duration = serviceDistr2.draw();
			}
			// Create a new event in the eventlist
			double tme = eventlist.getTime();
			eventlist.add(this,0,tme+duration); //target,type,time
			// set status to busy
			status='b';
		}
		else
		{
			if(processingTimes.length>procCnt)
			{
				eventlist.add(this,0,eventlist.getTime()+processingTimes[procCnt]); //target,type,time
				// set status to busy
				status='b';
				procCnt++;
			}
			else
			{
				eventlist.stop();
			}
		}
	}
}