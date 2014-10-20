package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.checkPointsData.CheckPoints;
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
	private static final Logger log = LogManager.getLogger(Server.class);
	

	private int port = 7890;//port id
	private ServerSocket serverSocket;//server socket for listening incom connection requests
	private Socket socket;//socket for updates
	private CheckPoints checkPoints;
	
	
	private InputStream is;
	private OutputStream os;
	
	private boolean[] checkPointsOnline;
	private boolean[] usersOnline;
	
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
	public Server(int port, UsersList ul, CheckPoints cp, ConcurrentLinkedQueue<HashMap<String, String>> requests){
		this.port = port;
		this.checkPoints = cp;
		this.checkPointsOnline = new boolean[this.checkPoints.getCount()];
		this.usersOnline = new boolean[ul.getUsers().size()];
		this.requests = requests;
		for (int i=0;i<this.checkPoints.getCount();i++){
			this.checkPointsOnline[i]=false;//all check points aren`t registered on system
		}
		for (int i=0;i<ul.getUsers().size();i++){
			this.usersOnline[i]=false;//all users aren`t registered on system
		}
		initialization();
	}
	
	/**
	 * create server class instance with default port
	 * @param ul
	 * @param cp
	 * @param requests
	 */
	public Server(UsersList ul, CheckPoints cp, ConcurrentLinkedQueue<HashMap<String, String>> requests){
		this.checkPoints = cp;
		this.checkPointsOnline = new boolean[this.checkPoints.getCount()];
		this.usersOnline = new boolean[ul.getUsers().size()];
		this.requests = requests;
		for (int i=0;i<this.checkPoints.getCount();i++){
			this.checkPointsOnline[i]=false;//all check points aren`t registered on system
		}
		for (int i=0;i<ul.getUsers().size();i++){
			this.usersOnline[i]=false;//all users aren`t registered on system
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
		int userId = 0;
		if (parameters.size()<2){ //if we have no much parametrs in collections send back error
			try {
				answer = "Wrong count of args!";
				answerParameters.put("command", "error");
				answerParameters.put("message", answer);
				stream.writeObject(answerParameters);
				log.trace(answer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Fail ",e);
			}
		} else { //if enough parameters 
			
			int checkPointid = Integer.valueOf(parameters.get("checkPointId"));
			switch (parameters.get("command")) {
			case "init":
				if (checkPointid!=0) {
					if (checkPointOnSystem(checkPointid)) {//already online
						answer = "Check point "+checkPointid+" alredy in system.";
						answerParameters.put("command", "error");
						answerParameters.put("message", answer);
						log.trace(answer);
						try {
							stream.writeObject(answerParameters);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error("Fail ",e);
						}
						
					} else {
						//add mark for check point as online registred
						this.checkPointsOnline[checkPointid-1] = true;
						log.trace("Got new checkPoint");
						answer = "Check point "+checkPointid+" authorize.";
						answerParameters.put("command", "success");
						answerParameters.put("message", answer);
						log.trace(answer);
						try {
							stream.writeObject(answerParameters);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error("Fail ",e);
						}
					}
				} else if (checkPointid==0){ //if checkPointId=0 then send back error
					answer = parameters.get("")+" is wrong Id";
					answerParameters.put("checkPointId", "error");
					answerParameters.put("message", answer);
					log.trace(answer);
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}
				}
				break;
				
			case "exit":
				checkPointid = Integer.valueOf(parameters.get("checkPointId"));
				System.out.println(checkPointid+ " off point ");
				this.checkPointsOnline[checkPointid] = false;
				System.out.println("checkPoint id = "+checkPointid+ " is offline ");
				answer = "checkPoint id = "+checkPointid+ " is offline ";
				answerParameters.put("command", "success");
				answerParameters.put("message", answer);
				log.trace(answer);
				try {
					stream.writeObject(answerParameters);
					this.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Fail ",e);
				}
				break;
				
			case "in":
				userId = Integer.valueOf(parameters.get("clientId"));
				//get in command on message 
				if (!userOnSystem(userId)){
					setUserOn(userId);
					requests.add(parameters);
					answer = parameters.get("command")+" with clientId" +parameters.get("clientId") + " is successful added";
					answerParameters.put("command", "success");
					answerParameters.put("message", answer);
					log.trace("Add command "+parameters.get("command")+" from checkPoint="+parameters.get("checkPointId")
						+ ". Clinet id="+parameters.get("clientId"));
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}
				} else {
					answer = "ClientId " +parameters.get("clientId") + " is already online";
					answerParameters.put("command", "error");
					answerParameters.put("message", answer);
					log.trace(answer);
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}
				}			
				break;
				
				
			case "across":
				//get across command on message 
				userId = Integer.valueOf(parameters.get("clientId"));
				answerParameters = new HashMap<String, String>();
				if (userOnSystem(userId)){
					
					requests.add(parameters);					
					answer = parameters.get("command")+" with clientId" +parameters.get("clientId") + " is successful added";
					answerParameters.put("command", "success");
					answerParameters.put("message", answer);
					log.trace("User "+userId+" on system");
					log.trace("Add command "+parameters.get("command")+" from checkPoint="+parameters.get("checkPointId")
							+ ". Clinet id="+parameters.get("clientId"));
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}	
				} else {
					answer = "ClientId " +parameters.get("clientId") + " is off";
					answerParameters.put("command", "error");
					answerParameters.put("message", answer);
					log.trace("Clinet id="+parameters.get("clientId")+" is offline");
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}
				}
				break;
				
			case "out":
				//get out command on message 
				userId = Integer.valueOf(parameters.get("clientId"));
				if (userOnSystem(userId)){
					System.out.println("User "+userId+" on system");
					answer = parameters.get("command")+" with clientId" +parameters.get("clientId") + " is successful added";
					answerParameters.put("command", "success");
					answerParameters.put("message", answer);
					requests.add(parameters);
					log.trace("Add command "+parameters.get("command")+" from checkPoint="+parameters.get("checkPointId")
							+ ". Clinet id="+parameters.get("clientId")+". User off!");
					setUserOff(userId);
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}
				} else {
					answer = "ClientId " +parameters.get("clientId") + " is off";
					answerParameters.put("command", "error");
					answerParameters.put("message", answer);
					log.trace("Clinet id="+parameters.get("clientId")+" is offline already");
					try {
						stream.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error("Fail ",e);
					}
				}
				
				break;
				
			default:
				//uknown command feedback
				answer = parameters.get("command")+" is uknown command!";
				answerParameters.put("command", "error");
				answerParameters.put("message", answer);
				log.trace(answer);
				try {
					stream.writeObject(answerParameters);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Fail ",e);
				}
				break;
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			log.trace("Server Started (method run)");
			this.serverSocket = new ServerSocket(port);
			log.trace("Sercer Socket Init on port " + this.port);
			
			while(true){
				log.trace("W8 request");
				this.socket = serverSocket.accept();
				
				log.trace("Got request");		
				
				this.is = this.socket.getInputStream();
				this.os = this.socket.getOutputStream();
				
				ObjectOutputStream oos = new ObjectOutputStream(this.os);
				ObjectInputStream ois = new ObjectInputStream(this.is);
				HashMap<String, String> parameters = new HashMap<String, String>();
				
				parameters = (HashMap<String, String>)ois.readObject();
				
				log.trace("Got data!"); 
				
				//parse input data  ---- notifications
				parse(oos, parameters, this.requests);
				//this.socket.close();
				//serverSocket.close();
			}
		} catch (Exception e){
			log.error("Fail ",e);
		}
		
	}
	
	/**
	 * view check point with current id on system 
	 * @param id
	 * @return
	 */
	private boolean checkPointOnSystem(int id){
		return this.checkPointsOnline[id-1];
	}
	
	private void setCheckPointOn(int id){
		this.checkPointsOnline[id-1]=true;
	}
	
	private void setCheckPointOff(int id){
		this.checkPointsOnline[id-1]=false;
	}
	
	private boolean userOnSystem(int id){
		return this.usersOnline[id-1];
	}
	
	private void setUserOn (int id){
		this.usersOnline[id-1]=true;
	}
	private void setUserOff (int id){
		this.usersOnline[id-1]=false;
	}
}
