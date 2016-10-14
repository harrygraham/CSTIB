package uk.ac.cam.hg402.fjava.tick2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.sun.org.apache.bcel.internal.generic.RETURN;

//TODO: import required classes

class TestMessageReadWrite {
 
 static boolean writeMessage(String message, String filename) throws IOException {
  //TODO: Create an instance of "TestMessage" with "text" set
  //      to "message" and serialise it into a file called "filename".
  //      Return "true" if write was successful; "false" otherwise.
	 
	 TestMessage testMessage = new TestMessage();
	 testMessage.setMessage(message);
	 try{
		 FileOutputStream fos = new FileOutputStream(filename);
		 ObjectOutputStream out = new ObjectOutputStream(fos);
		 out.writeObject(testMessage);
		 out.close();
		 return true;
	 }catch(IOException e){
		 return false;
	 }
	 
 }

 static String readMessage(String location) throws MalformedURLException, ClassNotFoundException {
  //TODO:
  // If "location" begins with "http://" then attempt to download
  // and deserialise an instance of TestMessage; you should use
  // the java.net.URL and java.net.URLConnection classes.
  // If "location" does not begin with "http://" attempt to 
  // deserialise an instance of TestMessage by assuming that 
  // "location" is the name of a file in the filesystem.
  //
	 
	
	String textField;
	TestMessage testMessage;
	
	Boolean isURL = location.startsWith("http://");
	URL url = new URL(location);
	
	if(isURL){
		//System.out.println("isURL");
		try {
			URLConnection connection = url.openConnection();
			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
			testMessage = (TestMessage) in.readObject();
			textField=testMessage.getMessage();

		} catch (IOException e1) {
			textField = "null";
			//System.out.println(e1.getMessage());
		}

	}else{

		try{
			FileInputStream fis = new FileInputStream(location);
			ObjectInputStream in = new ObjectInputStream(fis);
			testMessage = (TestMessage) in.readObject();
			in.close();
			textField = testMessage.getMessage();
		}catch(IOException e){
			textField =  "null";
		}

		
		// If deserialisation is successful, return a reference to the 
		// field "text" in the deserialised object. In case of error, 
		// return "null".
	}
	
	return textField;
 }

 public static void main(String args[]) throws MalformedURLException, ClassNotFoundException {
  //TODO: Implement suitable code to help you test your implementation
  //      of "readMessage" and "writeMessage".
	 
	 String location = "http://www.cl.cam.ac.uk/teaching/current/FJava/testmessage-hg402.jobj";
	 String result = readMessage(location);
	 System.out.println(result);
	 
 }
}