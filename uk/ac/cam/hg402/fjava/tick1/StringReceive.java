package uk.ac.cam.hg402.fjava.tick1;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class StringReceive {

	public static void main(String[] args) throws IOException {
		if(args.length ==0){
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}
		else if(args.length != 2){
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}
		else{
			String serverName = args[0];
			String port = args[1];
			
			try {
				int portNum = Integer.parseInt(port);
				Socket sock = new Socket(serverName, portNum);
				InputStream in = sock.getInputStream();
				byte[] buffer = new byte[1024];
				
				while(true){
					int len = in.read(buffer);
					String output = new String(buffer, 0, len);
					System.out.println(output);
				}
				
				
			} catch (NumberFormatException e) {
				System.err.println("This application requires two arguments: <machine> <port>");
				return;
			} catch (IOException e2){
				System.err.println("Cannot connect to " + serverName + " on port " + port);
				return;
			}
		}
	}

}
