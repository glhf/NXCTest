package client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class Main {
	public static final Logger log = Logger.getLogger(Main.class);
	
	//arg 0 - id of the check point
	public static void main(String[] args) {
		 String host = "127.0.0.1";
		 Socket s;
		 InputStream is;
		 OutputStream os;
		 int pointId = 0;
		 int port = 7890;
		if (args.length==0){
			System.out.println(args.length);
			System.out.println(new IllegalArgumentException("Wrong count of argumets! No id of check point.").getMessage());
			return;
		} else if (args.length==1) {
			pointId=Integer.valueOf(args[0]);
		} else if (args.length==2) {
			pointId = Integer.valueOf(args[0]);
			port=Integer.valueOf(args[1]);
		}
		
		try { 
			System.out.println("Waiting for conn...");
			s = new Socket(host, port);
			System.out.println("Get conn...");
			
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			
			//initialization checkpoint with id
			//get processor thread on server
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("command", "init");
			parameters.put("pointId", String.valueOf(pointId));
			
			oos.writeObject(parameters);
			System.out.println("Send initialiation data...");
			oos.flush();
					
			//get recent info if command = error 
			parameters.clear();
			System.out.println("Recive answe...");
			parameters = (HashMap<String, String>) ois.readObject();
			
			if (parameters.get("command").equals("error")){
				System.out.println(parameters.get("message"));
				System.out.println("exit...... ");
				return;
			} else if(parameters.get("command").equals("success")){
				System.out.println(parameters.get("message"));
			}
			s.close();
			
			while(true){
				parameters = new HashMap<String, String>();
				getCommand(pointId, parameters, s, host, port);
			}
			
		} catch (Exception ex) { 
			log.error("Fail!", ex);
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
		System.out.println("Have "+cmd);
		String[] split = cmd.split(" ");
		int cmdInd = Integer.valueOf(split[0]);
		int userId = Integer.valueOf(split[1]);
		
		switch (cmdInd) {
		case 1:
			param.put("command", "in");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPointId", String.valueOf(pointId));
			break;
		case 2:
			param.put("command", "across");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPointId", String.valueOf(pointId));
			break;
		case 3:
			param.put("command", "out");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPointId", String.valueOf(pointId));
			break;
		case 0:
			return;
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
		
	}
	
	//send data method 
	static void sendData(ObjectOutputStream oos, HashMap<String, String> param){
		try {
			oos.writeObject(param);
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
				System.out.println(parameters.get("message"));
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
