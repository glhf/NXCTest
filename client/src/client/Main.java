package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
	public static final Logger log = LogManager.getLogger(Main.class.getName());
	
	//arg 0 - id of the check point
	public static void main(String[] args) {
		 String host = "127.0.0.1";
		 Socket s;
		 InputStream is;
		 OutputStream os;
		 int pointId = 0;
		 int port = 7890;
		if (args.length==0){
			log.trace(args.length);
			log.trace(new IllegalArgumentException("Wrong count of argumets! No id of check point.").getMessage());
			return;
		} else if (args.length==1) {
			pointId=Integer.valueOf(args[0]);
		} else if (args.length==2) {
			pointId = Integer.valueOf(args[0]);
			port=Integer.valueOf(args[1]);
		}
		try { 
			log.trace("Waiting for conn...");
			s = new Socket(host, port);
			log.trace("Get conn...");
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			
			//initialization checkpoint with id
			//get processor thread on server
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("command", "init");
			parameters.put("checkPointId", String.valueOf(pointId));
			
			oos.writeObject(parameters);
			log.trace("Send initialiation data...");
			oos.flush();
					
			//get recent info if command = error 
			parameters.clear();
			log.trace("Recive answe...");
			parameters = (HashMap<String, String>) ois.readObject();
			
			if (parameters.get("command").equals("error")){
				log.trace(parameters.get("message"));
				log.trace("exit...... ");
				return;
			} else if(parameters.get("command").equals("success")){
				log.trace(parameters.get("message"));
			}
			s.close();
			
			while(true){
				parameters = new HashMap<String, String>();
				getCommand(pointId, parameters, s, host, port);
			}
			
		} catch (Exception ex) { 
			log.error(ex);
		}
	}
	
	//method for emulete UI in console
	//parse command and parameters
	static void getCommand(int pointId, HashMap<String, String> param, Socket s, String host, int port){ 
		System.out.println("---Enter Command---");
		System.out.println("--- parametrs: after command code enter id user from the system ---");
		System.out.println("--- 1 1 for example ---");
		System.out.println("1) Client entered ");
		System.out.println("2) Client across point");
		System.out.println("3) Client lefr ");
		System.out.println("0) exit");
		
		Scanner in = new Scanner(System.in);
		String cmd;
		cmd = in.nextLine();
		log.trace("Have "+cmd);
		String[] split = cmd.split(" ");
		int cmdInd = Integer.valueOf(split[0]);
		int userId = 0;
		if (cmdInd!=0) {
			userId = Integer.valueOf(split[1]);
		}
		
		switch (cmdInd) {
		case 1:
			param.put("command", "in");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPointId", String.valueOf(pointId));
			log.info("Client "+ String.valueOf(userId) + " in on check point " + String.valueOf(pointId));
			break;
		case 2:
			param.put("command", "across");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPointId", String.valueOf(pointId));
			log.info("Client "+ String.valueOf(userId) + " across check point " + String.valueOf(pointId));
			break;
		case 3:
			param.put("command", "out");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPointId", String.valueOf(pointId));
			log.info("Client "+ String.valueOf(userId) + " out on check point " + String.valueOf(pointId));
			break;
		case 0:
			param.put("command", "exit");
			param.put("checkPointId", String.valueOf(pointId));
			log.info("Check point " + String.valueOf(pointId) + " shutdown");
			break;
		default:
			break;
		}
		try {
			s = new Socket(host, port);
			sendData(new ObjectOutputStream(s.getOutputStream()), param);
			reciveAnswer(new ObjectInputStream(s.getInputStream()));
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		if (cmdInd==0) {
			System.exit(0);
		}
		
	}
	
	//send data method 
	static void sendData(ObjectOutputStream oos, HashMap<String, String> param){
		try {
			oos.writeObject(param);
			log.trace("Send clientId = "+param.get("clientId"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
	}
	
	//method for resive answer from server
	//print or dont the message
	static void reciveAnswer(ObjectInputStream ois){
		try {
			HashMap<String, String> parameters = (HashMap<String, String>)ois.readObject();
			if ( parameters.get("command")=="error") {
				log.trace(parameters.get("message"));
				log.info(parameters.get("message"));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
	}
}
