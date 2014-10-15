package server.workers;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jws.soap.SOAPBinding.Use;

import org.apache.log4j.Logger;

import server.checkPointsData.CheckPoints;
import server.userData.User;
import server.userData.UsersList;

/**
 * 
 * 
 * @author Admin
 *
 */
public class QueueRequestsWorker implements Runnable{
	public static final Logger log = Logger.getLogger(QueueRequestsWorker.class);
	
	private UsersList ul = new UsersList();
	private CheckPoints cp = new CheckPoints();
	
	private ConcurrentLinkedQueue<HashMap<String, String>> requests = new ConcurrentLinkedQueue<HashMap<String,String>>();
	private ConcurrentLinkedQueue<HashMap<String, String>> emails = new ConcurrentLinkedQueue<HashMap<String,String>>();
	
	
	public QueueRequestsWorker(UsersList ul, CheckPoints cp, ConcurrentLinkedQueue<HashMap<String, String>> requests, 
			ConcurrentLinkedQueue<HashMap<String, String>> emails) {
		this.ul = ul;
		this.cp = cp;
		this.requests = requests;
		this.emails = emails;
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HashMap<String, String> temp = new HashMap<String, String>();
		String tempCommand = new String();
		while (true){
			if (!requests.isEmpty()){
				//code that compute 
				temp = requests.poll();
				tempCommand = temp.get("command");
				switch (tempCommand) {
				case "in":
					
					break;

				case "across":
					
					break;
					
				case "out": 
					
					break;
				
				default:
					break;
				}
				
			}
		}
	}

}
