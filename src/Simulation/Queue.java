package Simulation;

import java.util.ArrayList;

/**
 *	Queue that stores products until they can be handled on a machine machine
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Queue implements ProductAcceptor
{
	/** List in which the products are kept */
	private ArrayList<Product> row;
	/** Requests from machine that will be handling the products */
	private ArrayList<Machine> requests;
	/** Variable to indicate whether a queue is open */
	private boolean isOpen;
	/** Reference to the correlated queue (if not null, then the queue is part of hybrid queue) */
	private Queue correlatedServiceQueue;
	
	/**
	*	Initializes the queue and introduces a dummy machine
	*	the machine has to be specified later
	*/
	public Queue(boolean isOpen, Queue correlatedServiceQueue)
	{
		row = new ArrayList<>();
		requests = new ArrayList<>();
		this.isOpen = isOpen;
		this.correlatedServiceQueue = correlatedServiceQueue;
	}
	
	/**
	*	Asks a queue to give a product to a machine
	*	True is returned if a product could be delivered; false if the request is queued
	*/
	public boolean askProduct(Machine machine)
	{
		// This is only possible with a non-empty queue
		if(row.size()>0)
		{
			// If the machine accepts the product
			if(machine.giveProduct(row.get(0)))
			{
				row.remove(0);// Remove it from the queue
				return true;
			}
			else
				return false; // Machine rejected; don't queue request
		}
		else
		{
			requests.add(machine);
			return false; // queue request
		}
	}
	
	/**
	*	Offer a product to the queue
	*	It is investigated whether a machine wants the product, otherwise it is stored
	*/
	public boolean giveProduct(Product p)
	{
		// Check if the machine accepts it
		if(requests.size()<1)
			row.add(p); // Otherwise store it
		else
		{
			boolean delivered = false;
			while(!delivered & (requests.size()>0))
			{
				delivered=requests.get(0).giveProduct(p);
				// remove the request regardless of whether or not the product has been accepted
				requests.remove(0);
			}
			if(!delivered)
				row.add(p); // Otherwise store it
		}
		return true;
	}

	public void open() {
		isOpen = true;
	}

	public void close() {
		isOpen = false;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public int getLength() {
		if(isCombined())
			return row.size() + correlatedServiceQueue.getLength();
		else
			return row.size();
	}

	public boolean isCombined() {
		if(correlatedServiceQueue != null)
			return true;
		else
			return false;
	}
}