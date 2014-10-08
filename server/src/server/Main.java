package server;

public class Main {

	/**
	 * @param args
	 * args 0 = port
	 */
	public static void main(String[] args) {
		Server s;
		if (args.length>0) {
			s = new Server(Integer.valueOf(args[0])); 
		} else {
			s = new Server();
		}
		Thread t = new Thread(s);
		t.setPriority(Thread.NORM_PRIORITY);
		t.start();
	}
}