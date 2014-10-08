package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Main {
	
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
			is=s.getInputStream();
			os=s.getOutputStream();
			
			ObjectOutputStream oos = new ObjectOutputStream(os);
			//initialization checkpoint with id
			//get processor thread on server
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("command", "init");
			parameters.put("pointId", String.valueOf(pointId));
			System.out.println("Send initialiation data...");
			oos.writeObject(parameters);
			//get recent info if command = error 
			System.out.println("Recive answe...");
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			parameters = (HashMap<String, String>)ois.readObject();
			
			
			if (parameters.get("command")=="error"){
				System.out.println(parameters.get("message"));
				System.out.println("exit...... ");
				return;
			} else {
				System.out.println("Sucessfull initialization as checkpoint "+pointId);
				while(true){
					parameters = new HashMap<String, String>();
					getCommand(pointId, parameters);
					sendData(oos, parameters);
					reciveAnswer(ois);
				}
			}
			
			
		} catch (Exception ex) { 
			System.out.println(ex.toString());
		}
	}
	
	//method for emulete UI in console
	//parse command and parameters
	static void getCommand(int pointId, HashMap<String, String> param){ 
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
			param.put("command", "clientOn");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPoint", String.valueOf(pointId));
			break;
		case 2:
			param.put("command", "clientAcross");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPoint", String.valueOf(pointId));
			break;
		case 3:
			param.put("command", "clientOut");
			param.put("clientId", String.valueOf(userId));
			param.put("checkPoint", String.valueOf(pointId));
			break;
		case 0:
			return;
		default:
			break;
		}
		
	}
	
	//send data method 
	static void sendData(ObjectOutputStream oos, HashMap<String, String> param){
		try {
			oos.writeObject(param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}