package server.checkPointsData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import server.interfaces.Xmlable;

/**
 * 
 * @author goodvin
 * graph modeling class for counting cost of travel
 * 
 * information about cost and linked of point 
 * is available in file with text looks like
 * 
 * CheckPoints data file
 * N=
 * 0: [1,2], [4,6]
 * ...
 * n: [5,12], [2,5], [6,12]
 * 
 * there 0: (<n:>) is id of checkpoint
 * pairs [1,2] and [4,6] is info [<where can drive>,<cost of travel>]
 * 
 * 
 */
@XmlRootElement(name="pointsList")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckPoints{
	private static int lastAddIndex=0;
	@XmlElement(name="directed")
	private boolean directed=false; 
	@XmlElement(name="count")
	private int count=10;
	@XmlElement(name="points")
	private ArrayList<Point> points = new ArrayList<Point>();
	
	//init method for start values for road systems
	public void initialization(){
			for (int i=0; i<this.getCount()+1; i++){
				this.points.add(i, new Point(i));
			}			
			this.addPointWithTarget(1, 4, 4);
			this.addPairToLastPoint(5, 5);
			this.addPointWithTarget(2, 6, 8);
			this.addPointWithTarget(3, 6, 7);	
			this.addPointWithTarget(5, 6, 6);
			this.addPairToLastPoint(8, 7);
			this.addPairToLastPoint(10, 1);
			this.addPointWithTarget(10, 7, 4);
			this.addPairToLastPoint(9, 3);		
	}
	
	public CheckPoints(){
		
	}
	
	public CheckPoints(boolean directed){
		this.directed=directed;	
	}
	
	//method for get cost of ride frop A point to B point
	
	/**
	 * Retunr cost of the road from a to b
	 * @param a
	 * @param b
	 * @return int, cost
	 */
	public Integer getCostRide(int a, int b){
		if(this.getPoints().get(a).getPair().get(b)==null) throw new IllegalArgumentException("No way from "+a
																							 +" point to "+b+" point");
		int rez=0;
		//add exeption - path not found
		
		rez = this.getPoints().get(a).getPair().get(b);
		
		return rez;
	}
	
	
	//methods for adding points to the structures of road system
	
	/**
	 * Method add ways from porint a to set of points 
	 * whith set of costs
	 * if graph not directed add two pair at one moment:
	 * from a to b and b to a
	 * from id to where[] and from where to id
	 * @param id
	 * @param where
	 * @param cost
	 */
	public void addPointWithTargets(int id, int[] where, int[] cost){
		if (where.length!=cost.length){
			throw new IllegalArgumentException("Wrong size of input data: have "+where.length+" \"where\" and " + 
												+cost.length+" \"cost`s\" ");
		} else {
			if (!this.directed){
				for (int i=0; i<where.length; i++){
					this.getPoints().get(id).addPair(where[i], cost[i]);
					this.getPoints().get(where[i]).addPair(id, cost[i]);
				}
			} else {
				for (int i=0; i<where.length; i++){
					this.getPoints().get(id).addPair(where[i], cost[i]);
				}
			}
			CheckPoints.lastAddIndex=id;
		}
	}
	
	/**
	 * Method for adding cost of way beetwen two checkpoints
	 * if graph is gerected method add just one pair from a to b
	 * form id to where pair 
	 * if graph not directed add two pair at one moment:
	 * from a to b and b to a
	 * from id to where and from where to id
	 * @param id - ID of a point
	 * @param where - ID of b point
	 * @param cost - cost of way
	 */
	public void addPointWithTarget(int id, int where, int cost){
		if (!this.directed) {
			this.getPoints().get(id).addPair(where, cost);
			this.getPoints().get(where).addPair(id, cost);
			CheckPoints.lastAddIndex=id;
		} else {
			this.getPoints().get(id).addPair(where, cost);
			CheckPoints.lastAddIndex=id;
		}
	}
	
	/**
	 * Method for adding cost of way beetwen two checkpoints
	 * if graph is gerected method add just one pair from a to b
	 * form id to where pair 
	 * if graph not directed add two pair at one moment:
	 * from a to b and b to a
	 * from id to where and from where to id
	 * @param where
	 * @param cost
	 */
	public void addPairToLastPoint(int where, int cost){
		if (!this.directed) {
			this.getPoints().get(CheckPoints.lastAddIndex).addPair(where, cost);
			this.getPoints().get(where).addPair(CheckPoints.lastAddIndex, cost);
		} else {
			this.getPoints().get(CheckPoints.lastAddIndex).addPair(where, cost);
		}
	}
	
	//geter seter methods
	public ArrayList<Point> getPoints() {
		return this.points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	
	public boolean isDirected() {
		return this.directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}	
}
