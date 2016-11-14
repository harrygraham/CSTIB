package uk.ac.cam.hg402.fjava.tick4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import uk.ac.cam.cl.fjava.messages.Message;

public class ChatServer {

	public static <T> void main(String[] args) {
		if(args.length != 1){
			System.err.println("Usage: java ChatServer <port>");
		}else{
			int port = 0;
			MultiQueue<Message> multiQueue = new MultiQueue<Message>();
			try{
				port = Integer.valueOf(args[0]);
				ServerSocket socket = new ServerSocket(port);
				
				while(true){
					Socket newSock = socket.accept();
					ClientHandler handler = new ClientHandler(newSock, multiQueue);
					
				}
				
			
			}catch(NumberFormatException e1){
				System.err.println("Usage: java ChatServer <port>");
			} catch (IOException e) {
				System.err.println("Cannot use port number " + port);
			}
			
			
				
		}

	}

}
