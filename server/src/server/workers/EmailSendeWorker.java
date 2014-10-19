package server.workers;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import server.userData.UsersList;

/**
 * runnable class than check queue of emails-requests and send emails for clients 
 * @author goodvin
 *
 */
public class EmailSendeWorker implements Runnable{
	public static final Logger log = LogManager.getLogger(EmailSendeWorker.class);
	private UsersList ul = new UsersList();
	private ConcurrentLinkedQueue<HashMap<String, String>> emails = new ConcurrentLinkedQueue<HashMap<String, String>>();
	
	
	public EmailSendeWorker(UsersList ul, ConcurrentLinkedQueue<HashMap<String, String>> emails){
		this.ul = ul;
		this.emails = emails;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			if (!emails.isEmpty()){
				//do itaration with send 
				HashMap<String, String> temp = emails.poll();
				int userId = Integer.valueOf(temp.get("userId"));
				int checkPointId = Integer.valueOf(temp.get("checkPointId"));
				//input methods of patrsing data and send emals
			}
		}
	}

}
