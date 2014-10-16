package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import server.checkPointsData.CheckPoints;
import server.userData.UserWayList;
import server.userData.UsersList;

/**
 * Class server listening socket
 * initialize the checkPoints on init request
 * add request to the queue for further work
 * 
 * 
 * @author Admin
 *
 */
public class Server implements Runnable{
	private static final Logger log = Logger.getLogger(Server.class);
	

	private int port = 7890;//port id
	private ServerSocket serverSocket;//server socket for listening incom connection requests
	private Socket socket;//socket for updates
	private CheckPoints checkPoints;
	
	
	private InputStream is;
	private OutputStream os;
	
	private boolean[] checkPointsOnline;
	
	/**
	 * use for adding requests in queue on work 
	 */
	private ConcurrentLinkedQueue<HashMap<String, String>> requests = new ConcurrentLinkedQueue<HashMap<String, String>>();
	
	/**
	 * create server class instance with set port 
	 * @param port
	 * @param ul
	 * @param cp
	 * @param requests
	 */
	public Server(int port, CheckPoints cp, ConcurrentLinkedQueue<HashMap<String, String>> requests){
		this.port = port;
		this.checkPoints = cp;
		this.checkPointsOnline = new boolean[this.checkPoints.getCount()];
		this.requests = requests;
		for (int i=0;i<this.checkPoints.getCount();i++){
			this.checkPointsOnline[i]=false;//all check points aren`t registered on system
		}
		initialization();
	}
	
	/**
	 * create server class instance with default port
	 * @param ul
	 * @param cp
	 * @param requests
	 */
	public Server(CheckPoints cp, ConcurrentLinkedQueue<HashMap<String, String>> requests){
		this.checkPoints = cp;
		this.checkPointsOnline = new boolean[this.checkPoints.getCount()];
		this.requests = requests;
		for (int i=0;i<this.checkPoints.getCount();i++){
			this.checkPointsOnline[i]=false;//all check points aren`t registered on system
		}
		initialization();
	}

	private void initialization() {
		
	}
	
	/**
	 * 
	 * @param s
	 * @param parameters
	 */
	void parse(ObjectOutputStream stream, HashMap<String, String> parameters, ConcurrentLinkedQueue<HashMap<String, String>> requests) {
		HashMap<String, String> answerParameters = new HashMap<String, String>();
		String answer;
		if (parameters.size()<2){ //if we have no much parametrs in collections send back error
			try {
				answer = "Wrong count of args!";
				answerParameters.put("command", "error");
				answerParameters.put("message", answer);
				stream.writeObject(answerParameters);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		} else { //if enough parameters 
			switch (parameters.get("command")) {
			case "init":
				int id = Integer.valueOf(parameters.get("pointId"));
				if (id!=0) {
					if (onSystem(id)) {//already online
						answer = "Current Id = "+id+" alredy in system.";
						answerParameters.put("command", "error");
						answerParameters.put("message", answer);
						System.out.println(answer);
						try {
							stream.writeObject(answerParameters);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error(e);
						}
						
					} else {
						//add mark for check point as online registred
						this.checkPointsOnline[id-1] = true;
						System.out.println("Got new checkPoint");
						answer = "Current Id = "+id+" authorize.";
						answerParameters.put("command", "success");
						answerParameters.put("message", answer);
						System.out.println(answer);
						try {
							stream.writeObject(answerParameters);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error(e);
						}
					}
				} else if (id==0){ //if pointID=0 then send back error
					answer = parameters.get("pointId")+" is wrong Id";
					answerParameters.put("command", "error");
					answerParameters.put("message", answer);
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e);
					}
				}
				break;
				
			case "in":
				//get in command on message 
				
				requests.add(parameters);
				
				answer = parameters.get("command")+" with clientId" +parameters.get("clientId") + " is successful added";
				answerParameters.put("command", "success");
				answerParameters.put("message", answer);
				try {
					stream.writeObject(answerParameters);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e);
				}
				break;
				
			case "across":
				//get across command on message 
				requests.add(parameters);
				
				answer = parameters.get("command")+" with clientId" +parameters.get("clientId") + " is successful added";
				answerParameters.put("command", "success");
				answerParameters.put("message", answer);
				try {
					stream.writeObject(answerParameters);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e);
				}
				break;
				
			case "out":
				//get out command on message 
				requests.add(parameters);
				
				answer = parameters.get("command")+" with clientId" +parameters.get("clientId") + " is successful added";
				answerParameters.put("command", "success");
				answerParameters.put("message", answer);
				try {
					stream.writeObject(answerParameters);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e);
				}
				break;
				
			default:
				//uknown command feedback
				answer = parameters.get("command")+" is uknown command!";
				answerParameters.put("command", "error");
				answerParameters.put("message", answer);
				try {
					stream.writeObject(answerParameters);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e);
				}
				break;
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			System.out.println("Server Started (method run)");
			this.serverSocket = new ServerSocket(port);
			System.out.println("Sercer Socket Init on port " + this.port);
			
			while(true){
				System.out.println("W8 request");
				this.socket = serverSocket.accept();
				
				System.out.println("Got request");		
				
				this.is = this.socket.getInputStream();
				this.os = this.socket.getOutputStream();
				
				ObjectOutputStream oos = new ObjectOutputStream(this.os);
				ObjectInputStream ois = new ObjectInputStream(this.is);
				HashMap<String, String> parameters = new HashMap<String, String>();
				
				parameters = (HashMap<String, String>)ois.readObject();
				
				System.out.println("Got data!"); 
				
				//parse input data  ---- notifications
				parse(oos, parameters, this.requests);
				//this.socket.close();
				//serverSocket.close();
			}
		} catch (Exception e){
			log.error(e);
		}
		
	}
	
	private boolean onSystem(int id){
		return this.checkPointsOnline[id-1];
	}

}
