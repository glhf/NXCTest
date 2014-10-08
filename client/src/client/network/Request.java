package client.network;
import java.util.HashMap;
/**
 * Class for transfer requests data via socket 
 * example:
 * key="commandCode" value = "useIn"
 * key="userId" value = "1";
 * etc
 * and serialization for socket transfer
 * @author goodvin
 *
 */
public class Request {
	private HashMap<String, String> parametrs;
	
	public String getParametrByKey(String key){
		return this.parametrs.get(key);
	}
	
}
