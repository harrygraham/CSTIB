package uk.ac.cam.hg402.fjava.tick5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

public class ClientHandler {
	private Socket socket;
	private MultiQueue<Message> multiQueue;
	private String nickname;
	private MessageQueue<Message> clientMessages;
	private boolean closureFlag =false;
	private Database database;
	public ClientHandler(Socket s, MultiQueue<Message> q, Database db) {

		database = db;
		socket = s;
		multiQueue = q;
		SafeMessageQueue<Message> safeMessageQueue = new SafeMessageQueue<>();
		clientMessages = safeMessageQueue;
		multiQueue.register(safeMessageQueue);
		int randomNum = 10000 + (int)(Math.random() * 99999);
		nickname = "Anonymous" + randomNum;
		
		//add 10 recent messages to multiqueue straight away
				try {
					List<RelayMessage> list;
					list = db.getRecent();
					for(int i = list.size() -1; i >=0; i--){
						multiQueue.put(list.get(i));
					}

					db.incrementLogins();
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				
		String hostname = socket.getInetAddress().getHostName();
		StatusMessage statusMessage = new StatusMessage(nickname + " connected from " + hostname + ".");
		multiQueue.put(statusMessage);

		


		Thread incoming = new Thread(){
			@Override
			public void run() {
				try{
					ObjectInputStream in = new ObjectInputStream(s.getInputStream());

					while(true){
						Object inputObj = in.readObject();

						if(inputObj instanceof ChangeNickMessage){
							ChangeNickMessage newMessage = (ChangeNickMessage)inputObj;

							StatusMessage statusMess = new StatusMessage(nickname + " is now known as " + newMessage.name);
							nickname = newMessage.name;
							multiQueue.put(statusMess);


						}
						if(inputObj instanceof ChatMessage){
							RelayMessage newMessage = new RelayMessage(nickname, (ChatMessage)inputObj);
							multiQueue.put(newMessage);
							db.addMessage(newMessage);
						}
					}
				}catch(IOException e){
					handleClosure(s);
					stop();

				} catch (ClassNotFoundException e) {

					e.printStackTrace();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		};
		incoming.start();


		Thread outgoing = new Thread(){
			@Override
			public void run() {
				try{
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

					while(true){
						out.writeObject(clientMessages.take());
					}
				}catch(IOException e){
					handleClosure(s);
					stop();
				}
			}
		};
		outgoing.start();


	}

	private void handleClosure(Socket s){
		if(closureFlag == false){
			multiQueue.deregister(clientMessages);
			StatusMessage statusMessage = new StatusMessage(nickname + " has disconnected.");
			try {
				s.close();
				closureFlag = true;
			} catch (IOException e) {

				e.printStackTrace();
			}
			closureFlag = true;
		}

	}

}

