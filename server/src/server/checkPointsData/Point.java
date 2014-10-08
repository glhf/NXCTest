package server.checkPointsData;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="point")
@XmlAccessorType(XmlAccessType.FIELD)
public class Point {
	@XmlElement(name="id")
	private int id;
	@XmlElement(name="pair")
	private HashMap<Integer,Integer> pair = new HashMap<Integer, Integer>();
	
	//consturcots
	public Point(){
		
	}
	
	public Point(int id, HashMap<Integer, Integer> pairs) {
		this.id=id;
		this.pair=pairs;
	}
	
	public Point(int id) {
		this.id=id;
	}
	
	//add pair for current point
	public void addPair(Integer where, Integer cost){
		this.getPair().put(where, cost);
	}
	
	//get set method
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public HashMap<Integer, Integer> getPair() {
		return pair;
	}
	public void setPair(HashMap<Integer, Integer> pair) {
		this.pair = pair;
	}
	
	
}
