package uk.ac.cam.hg402.fjava.tick5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import uk.ac.cam.cl.fjava.messages.Message;

public class ChatServer {

	public static <T> void main(String[] args) {
		if(args.length != 2){
			System.err.println("Usage: java ChatServer <port> <database path prefix>");
		}else{
			int port = 0;
			String pathPrefix = args[1];
			MultiQueue<Message> multiQueue = new MultiQueue<Message>();
			
			try{
				Database database = new Database(pathPrefix);
				port = Integer.valueOf(args[0]);
				ServerSocket socket = new ServerSocket(port);
				
				while(true){
					Socket newSock = socket.accept();
					
					ClientHandler handler = new ClientHandler(newSock, multiQueue, database);
					
				}
				
			
			}catch(NumberFormatException e1){
				System.err.println("Usage: java ChatServer <port>");
			} catch (IOException e) {
				System.err.println("Cannot use port number " + port);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
				
		}

	}

}
