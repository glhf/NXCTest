package server.workers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.userData.UsersList;

/**
 * runnable class than check queue of emails-requests and send emails for clients 
 * @author goodvin
 *
 */
public class EmailSendeWorker implements Runnable{
	private static String from = "goodvin4@gmail.com";
	private static String fromHost = "smtp.gmail.com";
	private static String fromPort = "587";
	private static String subject = "Leave system";
	
	
	public static final Logger log = LogManager.getLogger(EmailSendeWorker.class);
	private UsersList ul = new UsersList();
	private ConcurrentLinkedQueue<HashMap<String, String>> emails = new ConcurrentLinkedQueue<HashMap<String, String>>();
	
	
	public EmailSendeWorker(UsersList ul, ConcurrentLinkedQueue<HashMap<String, String>> emails){
		this.ul = ul;
		this.emails = emails;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			if (!emails.isEmpty()){
				//do itaration with send 
				
				//get parameters for email from hash map
				HashMap<String, String> temp = emails.poll();
				String tempName = temp.get("name");
				String tempEmail = temp.get("email");
				String to = tempEmail;
				Integer tempPrice = Integer.valueOf(temp.get("price"));
				String tempDate = temp.get("date");
				
				log.trace("EmailWorker get request");
				log.trace("name "+tempName);
				log.trace("email "+tempEmail);
				log.trace("price "+tempPrice);
				log.trace("date "+tempDate);
				
				//preapering email send properties
				Properties prop = System.getProperties();
				prop.setProperty("mail.smtp.host", fromHost);
				prop.put("mail.smtp.port", fromPort);
				prop.put("mail.smtp.auth", "false");
				prop.put("mail.smtp.starttls.enable", "true");

				
				//send email block
				Session session = Session.getDefaultInstance(prop,null);

				try{
				     MimeMessage message = new MimeMessage(session);
				     message.setFrom(new InternetAddress(from));
				     message.addRecipient(Message.RecipientType.TO,
				                              new InternetAddress(to));
				     message.setSubject(subject);
				     message.setText("You: "+tempName
				    		+ "whith email: "+tempEmail+"\n"
				     		+ "leave payable-way system at "+ tempDate+".\n"
				     		+ "You have trip with price " + tempPrice + "."
				    		);
				
				     // Send message
				     Transport.send(message);
				     log.trace("Sent message successfully....");
				 }catch (MessagingException ex) {
				     log.error("sending failed! ", ex);
				 }

			}
		}
	}
}
