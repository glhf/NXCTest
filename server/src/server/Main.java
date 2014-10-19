package server;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.checkPointsData.CheckPoints;
import server.checkPointsData.CheckPointsReader;
import server.userData.UserDataReader;
import server.userData.UserWayList;
import server.userData.UsersList;
import server.workers.EmailSendeWorker;
import server.workers.QueueRequestsWorker;
public class Main {
	private static final Logger log = LogManager.getLogger(Main.class);
	/**
	 * Include preparing of data for open server
	 * if data is validate open server
	 * @param args
	 * args 0 = port
	 */
	public static void main(String[] args) {
		//init data for server start
		UsersList ul = new UsersList();
		CheckPoints cp = new CheckPoints();
		UserWayList wayList;
		
		//initialization vars
		try {
			UserDataReader ur = new UserDataReader();
			ur.read();
			ul=ur.getTemp();
			System.out.println("Init users done!");
			CheckPointsReader cpr = new CheckPointsReader();
			cpr.read();
			cp=cpr.getTemp();
			System.out.println("Init check points done!");
		} catch (JAXBException e) {
			// TODO: handle exception
			log.error("User data read faild ", e);
		}
		
		//if there are`t data files
		ifXmlFilesDontExists(cp, ul);
		
		ConcurrentLinkedQueue<HashMap<String, String>> emails = new ConcurrentLinkedQueue<HashMap<String, String>>();
		System.out.println("Init emails queue done!");
		ConcurrentLinkedQueue<HashMap<String, String>> requests = new ConcurrentLinkedQueue<HashMap<String, String>>();
		System.out.println("Init requests queue done!");
				
		//start server
		Server serv;
		if (args.length==0){
			serv = new Server(cp, requests);
		} else {
			serv = new Server(Integer.valueOf(args[0]), cp, requests);
		}
		Thread server = new Thread(serv);
		server.setPriority(Thread.NORM_PRIORITY);
		server.start();
		
		QueueRequestsWorker rw = new QueueRequestsWorker(ul, cp, requests, emails);
		Thread requestWorker = new Thread(rw);
		requestWorker.setPriority(Thread.NORM_PRIORITY);
		requestWorker.start();
		log.info("Start requestWorker! ");
		
		EmailSendeWorker ew = new EmailSendeWorker(ul, emails);
		Thread emailWorker = new Thread(ew);
		emailWorker.setPriority(Thread.NORM_PRIORITY);
		emailWorker.start();
		log.info("Start emailWorker!");
	}
	
	/**
	 * write new xml data files if they aren`t exist
	 * @param cp write this checkPoints to file
	 * @param ul write this userList to file
	 */
	private static void ifXmlFilesDontExists(CheckPoints cp, UsersList ul){
		if (!(new java.io.File(server.interfaces.Xmlable.PATH_CHECK_POINTS_DATA)).exists()){
			try {
				CheckPointsReader cpr = new CheckPointsReader();
				cpr.setTemp(cp);
				cpr.write();
				System.out.println("Write check points done!");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				log.error("Can`t reslove path ", e);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				log.error("Marshal error ", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("IO Exeption ",e);
			}
		}
		if (!(new java.io.File(server.interfaces.Xmlable.PATH_USER_DATA)).exists()){
			try {
				UserDataReader udr = new UserDataReader();
				udr.setTemp(ul);
				udr.write();
				System.out.println("Write user list done!");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				log.error("Can`t reslove path ", e);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				log.error("Marshal error ", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("IO Exeption ",e);
			}
		}
	}
	
}