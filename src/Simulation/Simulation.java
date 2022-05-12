/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package Simulation;

import java.util.ArrayList;

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

		//  5 regular machines (/ cash registry)
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
