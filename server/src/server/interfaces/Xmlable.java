package server.interfaces;

public interface Xmlable {	
	/**
	 * Save path for the file with user data
	 */
	public static final String pathUsersData = "users.xml";
	public static final String pathCheckPointsData = "checkPointData.xml";
	
	/**
	 * Method for  unmarshal users data for application
	 * 
	 */
	public abstract void read();
	
	/**
	 * Method for marshal users data for application
	 * 
	 */
	public abstract void write();
}
