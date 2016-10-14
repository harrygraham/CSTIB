package uk.ac.cam.hg402.fjava.tick1;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Thread;

public class StringChat {
	public static void main(String[] args) throws UnknownHostException, IOException {
		String server = null;
		int port = 0;

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
			server = serverName;
			String portName = args[1];
			
			try {
				int portNum = Integer.parseInt(portName);
				port = portNum;
			} catch (NumberFormatException e) {
				System.err.println("This application requires two arguments: <machine> <port>");
				return;
			} 
		}
		
		

		//s is declared as final as the socket connection wont be changed at any point in the program, therefore it is appropriate to mark this as final.
		try{
		final Socket s = new Socket(server, port);

		Thread output = new Thread() {
			@Override
			public void run() {

				InputStream in;
				try {
					in = s.getInputStream();
					byte[] buffer = new byte[1024];

					while(true){
						int len = in.read(buffer);
						String output = new String(buffer, 0, len);
						System.out.println(output);
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
				
			}
		};
		output.setDaemon(true); 
		output.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		while( true) {

			String data ="";
			try {
				data = r.readLine();
				byte[] inBuff = data.getBytes();

				OutputStream out = s.getOutputStream();
				out.write(inBuff);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} catch(IOException e){
		System.err.println("Cannot connect to " + server + " on port " + port);
		return;
	}
		}
}

