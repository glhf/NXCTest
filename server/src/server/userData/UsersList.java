package server.userData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import server.interfaces.*;

/**
 * 
 * @author goodvin
 * class implements user list serialization function
 * with jaxb lib
 */
@XmlRootElement(name="usersList")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersList {
	@XmlElement(name="users")
	private ArrayList<User> users = new ArrayList<>();
	
	public void initialization(){
		//init walues
		this.addUser("nick3", "asdf@mail.ua");
		this.addUser("nick1", "asdf@mail.ua");
		this.addUser("nick2", "asdf@mail.ua");
		this.addUser("nick5", "asdf@mail.ua");
	}
	
	public UsersList(){
		
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}
	
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	public void addUser(String name, String email){
		this.users.add(new User(this.users.size() , name, email));
	}
}
