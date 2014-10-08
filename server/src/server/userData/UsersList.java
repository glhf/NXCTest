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
public class UsersList implements Xmlable{
	@XmlElement(name="users")
	private ArrayList<User> users = new ArrayList<>();
	
	private void initialization(){
		File f = new File(Xmlable.pathUsersData);
		if (f.exists()) {
			this.read();
		} else {
			//init walues
			this.addUser("nick3", "asdf@mail.ua");
			this.addUser("nick1", "asdf@mail.ua");
			this.addUser("nick2", "asdf@mail.ua");
			this.addUser("nick5", "asdf@mail.ua");
			this.write();
		}
		
	}
	
	public UsersList(){
		//initialization();
	}
	
	@Override
	public synchronized void read() {
		// unmarshal xml method
		try {
			JAXBContext jc = JAXBContext.newInstance(UsersList.class);
			Unmarshaller um = jc.createUnmarshaller();
			File f = new File(Xmlable.pathUsersData);
			UsersList temp = (UsersList)um.unmarshal(f);
			this.setUsers(temp.getUsers());
		} catch (JAXBException ex){
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		
	}

	@Override
	public void write() {
		// marshal xml method
		try {
			JAXBContext jc = JAXBContext.newInstance(UsersList.class);
			Marshaller m = jc.createMarshaller();
			FileOutputStream fs = new FileOutputStream(new File(Xmlable.pathUsersData));
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, fs);
			fs.close();
		} catch (IOException ex) {
			//insert sf4j log elements
			System.out.println(ex.toString());
		} catch (JAXBException ex){
			System.out.println(ex.toString());
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
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
