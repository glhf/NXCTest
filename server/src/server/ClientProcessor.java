package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import server.checkPointsData.CheckPoints;
import server.userData.UsersList;

public class ClientProcessor implements Runnable{
	int pointId;
	Socket socket;
	UsersList userList;
	CheckPoints checkPionts;
	WayList wayList;
	
	InputStream is;
	OutputStream os;
	
	
	public ClientProcessor(int id, Socket socket, UsersList userList,
			CheckPoints checkPionts, WayList waylist) {
		super();
		this.pointId = id;
		this.socket = socket;
		this.userList = userList;
		this.checkPionts = checkPionts;
		this.wayList = waylist;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		//read command from client
		ObjectInputStream oos;
		try {
			oos = new ObjectInputStream(is);
			parameters = (HashMap<String, String>)oos.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch (parameters.get("command")) {
		case "clientOn":
			this.wayList.clientOn(Integer.valueOf(parameters.get("clientId")), Integer.valueOf(parameters.get("checkPoint")));
			break;
		case "clientAcross":
			this.wayList.clientAcross(Integer.valueOf(parameters.get("clientId")), Integer.valueOf(parameters.get("checkPoint")));
			break;
		case "clientOut":
			this.wayList.clientOut(Integer.valueOf(parameters.get("clientId")), Integer.valueOf(parameters.get("checkPoint")));
			break;
		default:
			break;
		}
		
	}

	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public UsersList getUserList() {
		return userList;
	}
	public void setUserList(UsersList userList) {
		this.userList = userList;
	}
	public CheckPoints getCheckPionts() {
		return checkPionts;
	}
	public void setCheckPionts(CheckPoints checkPionts) {
		this.checkPionts = checkPionts;
	}
	public WayList getWayList() {
		return wayList;
	}
	public void setWayList(WayList wayList) {
		this.wayList = wayList;
	}
}
