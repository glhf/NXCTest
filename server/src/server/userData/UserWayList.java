package server.userData;

import java.util.ArrayList;
import java.util.HashMap;

import server.checkPointsData.CheckPoints;

/**
 * save information of moving clients 
 * @author goodvin
 * 
 */
public class UserWayList {
	
	private CheckPoints checkPoints;//for validadion pairs of points
	private HashMap<Integer, ArrayList<Integer>> list = new HashMap<Integer, ArrayList<Integer>>();
	
	private boolean isValidWay(int userId, int pointId){
		//check the way from prev poit to the point fith pointId
		boolean rez = true;
		//if have way from last enter point of current user 
		//to the current id (poitId)
		if(this.checkPoints.getPoints().get(this.list.get(userId).get(this.list.get(userId).size())).getPair().get(pointId)==null){
			rez=false;
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
			rez = false;
		} else {
			rez = true;
		}
		return rez;
	}
	
	/**
	 * Add user to the system, with set start checkPoint
	 * user come on the road
	 * @param userId user id 
	 * @param pointId start point id
	 */
	public void clientOn(int userId, int pointId){
		// add user to the system
		if (!this.onSystem(userId)) {
			this.list.put(userId, new ArrayList<Integer>());
			this.list.get(userId).add(pointId);
		} else { 
			System.out.println("User id="+userId+" already in system");
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
		for (int i=1; i<=temp.size(); i++){
			rez+=this.checkPoints.getCostRide(temp.get(i-1), temp.get(i));
		}
		return rez;
	}
	
	/**
	 * User come acrros the check point
	 * point add list
	 * @param userId
	 * @param pointId
	 */
	public void clientAcross(int userId, int pointId){
		//add poitId to list for userId
		if (isValidWay(userId, pointId)) {
			this.list.get(userId).add(pointId);
		} else {
			System.out.println("No way from "+this.list.get(userId).get(this.list.get(userId).size())+" to " + pointId);
		}
	}
	
}
