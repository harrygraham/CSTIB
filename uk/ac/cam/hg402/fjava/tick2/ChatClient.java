package uk.ac.cam.hg402.fjava.tick2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.reflect.generics.tree.FieldTypeSignature;
import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;
import uk.ac.cam.cl.fjava.messages.Execute;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.NewMessageType;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

@FurtherJavaPreamble(author = "Harry Graham", crsid = "hg402", date = "14th Oct 2016", summary = "Tick 2", ticker = FurtherJavaPreamble.Ticker.A )
public class ChatClient {

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

			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			Date now = new Date();
			String timeStr = time.format(now);
			//--------time string---------------
			System.out.println(timeStr + " [Client] " + "Connected to " + server + " on port " + port +".");

			Thread output = new Thread() {
				@Override
				public void run() {

					try{
						DynamicObjectInputStream in = new DynamicObjectInputStream(s.getInputStream());

						while(true){
							

							Object inputObj =  in.readObject();


							SimpleDateFormat time1 = new SimpleDateFormat("HH:mm:ss");
							Date now1 = new Date();
							String timeStr1 = time1.format(now1);
							//--------time string---------------
							
							if(inputObj instanceof StatusMessage){
								StatusMessage statusMessage = (StatusMessage)inputObj;
								System.out.println(timeStr1 + " [Server] " + statusMessage.getMessage());
							}

							else if(inputObj instanceof RelayMessage){
								RelayMessage relayMessage = (RelayMessage)inputObj;
								String from = relayMessage.getFrom();
								System.out.println(timeStr1 + " [" + from + "] " + relayMessage.getMessage());
							}
							else if(inputObj instanceof NewMessageType){
								NewMessageType newMessageType = (NewMessageType)inputObj;
								in.addClass(newMessageType.getName(), newMessageType.getClassData());
								System.out.println(timeStr1 + " [Client] New class " + newMessageType.getName() + " loaded.");
							}else{
								Class<?> someClass = inputObj.getClass();
								Field[] fields = someClass.getDeclaredFields();
								String fieldsStr ="";
								for(int i = 0; i < fields.length; i++){
									fields[i].setAccessible(true);
									String temp = (fields[i].getName() + "(" + fields[i].get(inputObj) + "), ");
									fieldsStr = fieldsStr + temp;
								}
								System.out.println(timeStr1 + " [Client] " + someClass.getSimpleName() + ": " + fieldsStr);
								
								Method[] methods = someClass.getDeclaredMethods();
								for(int i = 0 ; i < methods.length; i++){
									if(methods[i].getParameterCount() == 0 && methods[i].isAnnotationPresent(Execute.class)){
										try {
											methods[i].invoke(inputObj);
										} catch (IllegalAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalArgumentException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}

							
						}
					}catch(IOException | ClassNotFoundException e){
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
			output.setDaemon(true); 
			output.start();

			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

			while(true) {

				String data ="";

				SimpleDateFormat time2 = new SimpleDateFormat("HH:mm:ss");
				Date now2 = new Date();
				String timeStr2 = time2.format(now2);
				//--------time string---------------

				try {
					data = r.readLine();
					if(data.startsWith("\\")){
						if(data.startsWith( "\\nick")){
							String name = data.substring(6);
							ChangeNickMessage newNick = new ChangeNickMessage(name);
							out.writeObject(newNick);

						}
						else if(data.startsWith( "\\quit")){
							s.close();

							System.out.println(timeStr2 + " [Client] Connection terminated ");
							return;
						}else{
							String command = data.split(" ")[0].substring(1);
							System.out.println(timeStr2 + " [Client] Unknown command " +"\"" + command+ "\"");

						}

					}else{


						ChatMessage newMsg = new ChatMessage(data);


						out.writeObject(newMsg);

					}
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
