package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

import server.checkPointsData.CheckPoints;
import server.userData.UsersList;

public class Server implements Runnable{

	int port = 7890;
	ServerSocket serverSocket;
	Socket socket;
	UsersList userList;
	CheckPoints checkPoints;
	WayList wayList;
	
	
	InputStream is;
	OutputStream os;
	HashMap<Integer, ClientProcessor> clients = new HashMap<Integer, ClientProcessor>();

	public Server(){
		userList = new UsersList();
		checkPoints = new CheckPoints();
		wayList = new WayList(checkPoints);
		initialization();
	}
	
	public Server(int port){
		this.port=port;
		initialization();
	}

	private void initialization() {
		this.userList.read();
		
		this.checkPoints.read();
	}
	
	void parse(Socket s, HashMap<String, String> parameters, HashMap<Integer, ClientProcessor> clientsProcessors) {
		HashMap<String, String> answerParameters = new HashMap<String, String>();
		if (parameters.size()<2){ //if we have no much parametrs in collections send back error
			try {
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
				String answer = "Wrong count of args!";
				answerParameters.put("command", "error");
				answerParameters.put("message", answer);
				os.writeObject(answerParameters);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { //if enough parameters 
			switch (parameters.get("command")) {
			case "init":
				int id = Integer.valueOf(parameters.get("pointId"));
				if (Integer.valueOf(parameters.get("pointId"))!=0) {//
					if (clients.get(id)!=null) {
						String answer = "Current Id alredy in system.";
						answerParameters.put("command", "error");
						answerParameters.put("message", answer);
						try {
							ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
							os.writeObject(answerParameters);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else {//
						//add the checkpoint processor for implement functionality 
						ClientProcessor tempProcessor = new ClientProcessor(id, s, userList, checkPoints, wayList);
						clientsProcessors.put(id, tempProcessor);
						Thread t = new Thread(tempProcessor);
						t.setPriority(Thread.NORM_PRIORITY);
						t.start(); 
						System.out.println("Thread "+t.toString()+ " start for client if "+id);
						String answer = "Checkpoit is add";
						answerParameters.put("command", "succes");
						answerParameters.put("message", answer);
						try {
							ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
							os.writeObject(answerParameters);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				//
				} else if (Integer.valueOf(parameters.get("pointId"))==0){ //if pointID=0 then send back error
					String answer = parameters.get("pointId")+" is wrong Id";
					answerParameters.put("command", "error");
					answerParameters.put("message", answer);
					try {
						ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
						os.writeObject(answerParameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
				
			default:
				break;
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			System.out.println("Server Started (method run)");
			serverSocket = new ServerSocket(port);
			System.out.println("Sercer Socket Init on port " + this.port);
			
			while(true){
				System.out.println("W8 client");
				socket = serverSocket.accept();
				
				System.out.println("Got client");
							
				is = socket.getInputStream();
				os = socket.getOutputStream();
				
				HashMap<String, String> parameters = new HashMap<String, String>();
				
				ObjectInputStream oos = new ObjectInputStream(is);
				parameters = (HashMap<String, String>)oos.readObject();
				
				System.out.println("Got data");
				parse(socket, parameters, clients);
				//socket.close();
				//serverSocket.close();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
}
