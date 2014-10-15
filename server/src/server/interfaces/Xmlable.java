package server.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

public interface Xmlable {	
	/**
	 * Save path for the file with user data
	 */
	public static final String PATH_USER_DATA = "users.xml";
	public static final String PATH_CHECK_POINTS_DATA = "checkPointData.xml";
	
	/**
	 * Method for  unmarshal users data for application
	 * @throws JAXBException 
	 * 
	 */
	public abstract void read() throws JAXBException;
	
	/**
	 * Method for marshal users data for application
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * 
	 */
	public abstract void write() throws JAXBException, FileNotFoundException, IOException;
}
