package server.workers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.checkPointsData.CheckPoints;
import server.userData.UserWayList;
import server.userData.UsersList;

/**
 * 
 * 
 * @author Admin
 *
 */
public class QueueRequestsWorker implements Runnable{
	public static final Logger log = LogManager.getLogger(QueueRequestsWorker.class);
	
	private UserWayList wayList;
	private UsersList ul = new UsersList();
	private CheckPoints cp = new CheckPoints();
	
	private ConcurrentLinkedQueue<HashMap<String, String>> requests = new ConcurrentLinkedQueue<HashMap<String,String>>();
	private ConcurrentLinkedQueue<HashMap<String, String>> emails = new ConcurrentLinkedQueue<HashMap<String,String>>();
	
	
	private QueueRequestsWorker(){}
	/**
	 * 
	 * @param ul create instance for get users info
	 * @param cp checkPoirns need for work of UserWayList
	 * @param requests get request for work frow queue witch create Server.class
	 * @param emails RequestWorker add requests for sending email with EmailSenderWorker-instance
	 */
	public QueueRequestsWorker(UsersList ul, CheckPoints cp, ConcurrentLinkedQueue<HashMap<String, String>> requests, 
			ConcurrentLinkedQueue<HashMap<String, String>> emails) {
		this.ul = ul;
		this.cp = cp;
		this.requests = requests;
		this.emails = emails;
		this.wayList = new UserWayList(cp);
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HashMap<String, String> temp = new HashMap<String, String>();
		String tempCommand = new String();
		boolean flag;
		
		while (true){
			if (!requests.isEmpty()){
				//code that compute 
				temp = requests.poll();
				tempCommand = temp.get("command");
				switch (tempCommand) {
				case "in":
					flag = this.wayList.clientOn(Integer.valueOf(temp.get("clientId")), Integer.valueOf(temp.get("checkPointId")));
					if (flag) {
						log.info("Client "+temp.get("clientId")+ " is \"on\"  on " 
								+ temp.get("checkPointId") +" checkPoint. was add to system");
					} else {
						log.info("Client "+temp.get("clientId")+ " is not \"on\"  on " 
								+ temp.get("checkPointId") +" checkPoint - he is already on system");
					}
					break;

				case "across":
					flag = this.wayList.clientAcross(Integer.valueOf(temp.get("clientId")), Integer.valueOf(temp.get("checkPointId")));
					if (flag){
						log.info("Client "+temp.get("clientId")+ " is \"acroos\" " 
								+ temp.get("checkPointId") +" checkPoint");
					} else {
						log.info("Client "+temp.get("clientId")+ " is not \"acroos\" " 
								+ temp.get("checkPointId") +" checkPoint. No avaliable way.");
					}
					break;
					
				case "out": 
					
					int price = this.wayList.clientOut(Integer.valueOf(temp.get("clientId")), Integer.valueOf(temp.get("checkPointId")));
					String way = this.wayList.wayToString(Integer.valueOf(temp.get("clientId")));
					this.wayList.removeUser(Integer.valueOf(temp.get("clientId")));
					log.info("Client "+temp.get("clientId")+ " is \"out\"  on " + temp.get("checkPointId") +" checkPoint");
					HashMap<String, String> tempInfo = new HashMap<String, String>();
					
					tempInfo.put("name", ul.getName(Integer.valueOf(temp.get("clientId"))));//put name of user
					
					tempInfo.put("email", ul.getEmail(Integer.valueOf(temp.get("clientId"))));
					
					tempInfo.put("price", String.valueOf(price));
					
					tempInfo.put("date", (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()).toString()));
					log.info("User "+temp.get("clientId")+" "+way);
					emails.add(tempInfo );
					break;
				
				default:
					log.trace(tempCommand + ": command not found!");
					break;
				}
				
			}
		}
	}

}
