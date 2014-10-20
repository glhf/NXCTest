package server.userData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import server.checkPointsData.CheckPoints;

/**
 * save information of moving clients 
 * @author goodvin
 * 
 */
public class UserWayList {
	private static Logger log = LogManager.getLogger(UserWayList.class.getName());
	
	private CheckPoints checkPoints;//for validadion pairs of points
	private HashMap<Integer, ArrayList<Integer>> list = new HashMap<Integer, ArrayList<Integer>>();
	
	/**
	 * identify way from a to b points
	 * 
	 * @param userId
	 * @param pointId
	 * @return
	 */
	private boolean isValidWay(int userId, int pointId){
		//check the way from prev poit to the point fith pointId
		boolean rez = true;
		//if have way from last enter point of current user 
		//to the current id (poitId)
		if (this.list.get(userId).size()!=0) {
			System.out.println(this.checkPoints.getPoints().get(this.list.get(userId).get(this.list.get(userId).size()-1)).getPair().get(pointId));
			if(this.checkPoints.getPoints().get(this.list.get(userId).get(this.list.get(userId).size()-1)).getPair().get(pointId)==null){
				rez=false;
			}
		} else {
			rez = false;
		}
		return rez;
	}
	
	public UserWayList(CheckPoints cp){
		this.checkPoints=cp;
	}
	
	/**
	 * Check for user in the system
	 * @param userId
	 * @return true - user is on , false - user is off
	 */
	public boolean onSystem(int userId){
		//check for user in system
		boolean rez=false;
		if (this.list.get(userId)!=null){
			rez = true;
		} else {
			rez = false;
		}
		return rez;
	}
	
	/**
	 * Add user to the system, with set start checkPoint
	 * user come on the road
	 * @param userId user id 
	 * @param pointId start point id
	 */
	public boolean clientOn(int userId, int pointId){
		// add user to the system
		if (!this.onSystem(userId)) {
			this.list.put(userId, new ArrayList<Integer>());
			this.list.get(userId).add(pointId);
			Iterator it = this.list.get(userId).iterator();
			String t = "user was ";
			while(it.hasNext()){
				t += it.next()+",";
			}
			log.trace(t);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Count price for client, when them out
	 * @param userId
	 * @param pointId
	 * @return cost of the user ride
	 */
	public Integer clientOut(int userId, int pointId){
		//count cost of way for userId
		//and delete user from list
		Integer rez = 0;
		this.clientAcross(userId, pointId);
		ArrayList<Integer> temp = this.list.get(userId);

		if (temp.size()>1){
			for (int i=1; i<temp.size(); i++){
				rez+=this.checkPoints.getCostRide(temp.get(i-1), temp.get(i));
			}
		} else if (temp.size()==1) {
			rez=0;
		}
		log.trace("Price for user = "+userId+" is "+rez);
		return rez;
	}
	
	/**
	 * User come acrros the check point
	 * point add list
	 * @param userId
	 * @param pointId
	 */
	public boolean clientAcross(int userId, int pointId){
		//add poitId to list for userId
		if (isValidWay(userId, pointId)) {
			this.list.get(userId).add(pointId);
			Iterator it = this.list.get(userId).iterator();
			String t = "user was ";
			while(it.hasNext()){
				t += it.next()+",";
			}
			log.trace(t);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * convert user`s way from array list sequence to string
	 * @param id
	 * @return
	 */
	public String wayToString(int id){
		String way = "way is: ";
		Iterator it = this.list.get(id).iterator();
		while(it.hasNext()){
			way+=it.next().toString()+", ";
		}
		way+=".";
		return way;
	}
	
	/**
	 * remove user from list when it out
	 * @param id user id
	 */
	public void removeUser(int id){
		this.list.remove(id);
	}
}
