/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package Simulation;

import java.util.ArrayList;
import java.util.Comparator;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;


	/**
	 * Method that opens a new machine every time a machine is filled with 4 customers.
	 * @param machines, the list of machines being observed.
	 * @return true or false, depending on whether the customer number exceeds 4.
	 */
	private boolean mustOpenNewMachine(ArrayList<Machine> machines){
    	boolean allHave4customers = true;
		for(Machine m : machines){
			if(m.isOpen()){
				if(m.getQueue().getSize()<4){
					allHave4customers = false;
					break;
				}
			}
		}
		return allHave4customers;
	}

	private boolean allOpen(ArrayList<Machine> machines){
    	for(Machine m : machines){
    		if(!m.isOpen()){
    			return false;
			}
		}
    	return true;
	}

	private int nrOfOpenMachines(ArrayList<Machine> machines){
		// minimum no. of machines open is 3; 2 regular and the service machine must be always open.
		int nr = 3;
		for(Machine m: machines){
			// max number of open machines is 6 in total
			if(m.isOpen() && nr < 7 ){
				nr++;
			}
		}
		return nr;
	}



	//getLastCustomer returns a list of
	/*private ArrayList<Product> getLastCustomers(ArrayList<Machine> machines){
		ArrayList<Product> newCustomers = new ArrayList<Product>();
		for(Machine m : machines){
			int size = m.getQueue().getSize();
			Product customer = m.getQueue().getRow().get(size-1);
			newCustomers.add(customer);
		}
    	return newCustomers;
	}*/

	private ArrayList<Product> getAllCustomers(ArrayList<Machine> machines){
		ArrayList<Product> customers = new ArrayList<Product>();
		for(Machine m : machines){
			ArrayList<Product> c = m.getQueue().getRow();
			customers.addAll(c);
		}
		customers.sort(Comparator.comparing(Product::getArrivalTime));
		return customers;
	}

	//assumes there are n open machines with queues size>=4 and one empty queue
	//takes the last customer of each of the full queues and puts them in the new machine
	private ArrayList<Machine> rearrangeQueues(ArrayList<Machine> machines){
		ArrayList<Product> normalCustomers = getAllCustomers(machines);
		int nrOfOpenMachines = nrOfOpenMachines(machines);
		int newQueueLength = normalCustomers.size()/nrOfOpenMachines;
		int i = 0;
		for(Machine m : machines){
			if(m.isOpen()){

				ArrayList<Product> newRow = new ArrayList<Product>();
				if(i+newQueueLength<=normalCustomers.size()){
					newRow = new ArrayList(normalCustomers.subList(i,i+newQueueLength-1));
				}
				else{
					newRow = new ArrayList(normalCustomers.subList(i,normalCustomers.size()-1));
				}
				m.setQueue(newRow);
				i+=newQueueLength;
			}
			if(i>=normalCustomers.size()){
				break;
			}
		}
		return machines;
	}

	/*
	* updates the state of the machines (opens/closes them)
	* if a machine is opened, the queues are rearranged
	*/

	private ArrayList<Machine> updateMachines(ArrayList<Machine> machines){
		int openMachines = nrOfOpenMachines(machines);
		if(openMachines > 3){
			for(Machine m : machines){
				m.closeIfCan();
				openMachines--;
				if(openMachines<=3){
					break;
				}
			}
		}
		if(mustOpenNewMachine(machines) && !allOpen(machines)){
			for(Machine m: machines){
				if(!m.isOpen()){
					m.open();
					rearrangeQueues(machines);
					break;
				}
			}
		}
		return machines;
	}

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	// Create an eventlist
		CEventList l = new CEventList();

		// A queue for the machine
		ArrayList<Queue> queues = new ArrayList<>();

		//Create 5 regular queues
		for(int i=0; i<5;i++){
			Queue q = new Queue(true);
			queues.add(q);
		}

		//Create 1 more regular queue for the service registry
		Queue qr = new Queue(true);
		//Create 1 service queue
		Queue qs = new Queue(false);

		// A source
		Source source = new Source(queues,qr,qs,l,"Source");
		queues.add(qr);
		queues.add(qs);

		// A sink
		Sink sr = new Sink("Sink Regular");
		Sink ss = new Sink("Sink Service");

		//  Machines generation (/ cash registry)
		ArrayList<Machine> machines = new ArrayList<>();
		for(int i=0; i<6;i++){
			Machine m = new Machine(queues.get(i),sr,l,"Machine "+(i+1));
			machines.add(m);
		}
		Machine m = new Machine(queues.get(5), queues.get(6),sr,ss,l,"Machine Service");




		// start the eventlist
		l.start(2000); // 2000 is maximum time
    }

}
