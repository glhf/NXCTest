package server.checkPointsData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import server.interfaces.Xmlable;

public class CheckPointsReader  implements Xmlable{
	private CheckPoints temp;

	//xmlable methods for serialize data with XML
	
	@Override
	public void read() throws JAXBException {
		// unmarshal xml method
		if ((new File(Xmlable.PATH_CHECK_POINTS_DATA)).exists()){
			JAXBContext jc = JAXBContext.newInstance(CheckPoints.class);
			Unmarshaller um = jc.createUnmarshaller();
			File f = new File(Xmlable.PATH_CHECK_POINTS_DATA);
			CheckPoints temp = (CheckPoints)um.unmarshal(f);
			this.temp = temp;
		} else {
			this.temp = new CheckPoints();
			this.temp.initialization();
		}
	}

	@Override
	public void write() throws JAXBException, FileNotFoundException, IOException{
		// marshal xml method
		JAXBContext jc = JAXBContext.newInstance(CheckPoints.class);
		Marshaller m = jc.createMarshaller();
		FileOutputStream fs = new FileOutputStream(new File(Xmlable.PATH_CHECK_POINTS_DATA));
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(this.temp, fs);
		fs.close();
	}

	public CheckPoints getTemp(){
		return this.temp;
	}
	
	public void setTemp(CheckPoints temp){
			this.temp=temp;
		}
}
