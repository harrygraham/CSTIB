package uk.ac.cam.hg402.fjava.tick5;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.cl.fjava.messages.NewMessageType;
import uk.ac.cam.cl.fjava.messages.RelayMessage;

public class Database {


	private Connection connection;
	public Database(String databasePath) throws SQLException { 

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {

			e1.printStackTrace();
		}


		connection = DriverManager.getConnection("jdbc:hsqldb:file:"
				+ databasePath ,"SA","");
		Statement delayStmt = connection.createStatement();
		try {delayStmt.execute("SET WRITE_DELAY FALSE");}  //Always update data on disk
		finally {delayStmt.close();}



		connection.setAutoCommit(false);

		//create statistics table
		Statement sqlStmt = connection.createStatement();
		try {
			sqlStmt.execute("CREATE TABLE statistics(key VARCHAR(255),"+
					"value INT)");
		} catch (SQLException e) {
			System.out.println("Warning: Database table \"statistics\" already exists.");
		} finally {
			sqlStmt.close();
		}

		//insert default rows into statistics table
		String stmt = "INSERT INTO statistics(key,value) VALUES ('Total messages',0)";
		String stmt2 = "INSERT INTO statistics(key,value) VALUES ('Total logins',0)";
		
		PreparedStatement insertMessage = connection.prepareStatement(stmt);
		PreparedStatement insertMessage2 = connection.prepareStatement(stmt2);
		try {
			
			insertMessage.executeUpdate();
			insertMessage2.executeUpdate();
		} finally { //Notice use of finally clause here to finish statement
			insertMessage.close();
			insertMessage2.close();
		}

		//create messages table
		Statement sqlStmt2 = connection.createStatement();
		try {
			sqlStmt2.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
					"message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
		} catch (SQLException e) {
			System.out.println("Warning: Database table \"messages\" already exists.");
		} finally {
			sqlStmt2.close();
		}

		connection.commit();

	}
	public void close() throws SQLException { 
		connection.close();
	}
	public void incrementLogins() throws SQLException { 
		String stmt = "UPDATE statistics SET value = value+1 WHERE key='Total logins'";
		PreparedStatement insertMessage = connection.prepareStatement(stmt);
		try {

			insertMessage.executeUpdate();
		} finally { //Notice use of finally clause here to finish statement
			insertMessage.close();
		}

		connection.commit();
	}
	public void addMessage(RelayMessage m) throws SQLException {
		String stmt = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?) ";
		String stmt2 = "UPDATE statistics SET value = value+1 WHERE key='Total logins'";
						
		PreparedStatement insertMessage = connection.prepareStatement(stmt);
		PreparedStatement insertMessage2 = connection.prepareStatement(stmt2);
		try {
			insertMessage.setString(1, m.getFrom()); //set value of first "?" to "Alastair"
			insertMessage.setString(2, m.getMessage());
			
			insertMessage.setLong(3, m.getCreationTime().getTime());
			
			insertMessage.executeUpdate();
			insertMessage2.executeUpdate();
			
			connection.commit();
		} finally { //Notice use of finally clause here to finish statement
			insertMessage.close();
		}
		
		
	}
	public List<RelayMessage> getRecent() throws SQLException { 
		List<RelayMessage> list = new ArrayList<>();
		String stmt = "SELECT nick,message,timeposted FROM messages "+
				"ORDER BY timeposted DESC LIMIT 10";
		PreparedStatement recentMessages = connection.prepareStatement(stmt);
		try {
			ResultSet rs = recentMessages.executeQuery();
			try {
				while (rs.next()){
					long value = rs.getLong(3);
					java.util.Date newDate = new java.util.Date(value);
					RelayMessage NewMess = new RelayMessage(rs.getString(1), rs.getString(2), newDate);
					list.add(NewMess);
				}
				
			} finally {
				rs.close();
			}
		} finally {
			recentMessages.close();
		}
		
		return list;
		
	}


	public static void main(String[] args) throws SQLException {



		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {

			e1.printStackTrace();
		}
		//*** Code of path prefix input 
		String pathPrefix = "";
		if(args.length != 1){
			System.err.println("Usage: java uk.ac.cam.crsid.fjava.tick5.Database <database name>");
		}else{
			pathPrefix = args[0];
		}
		//***

		Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:"
				+ pathPrefix ,"SA","");
		Statement delayStmt = connection.createStatement();
		try {delayStmt.execute("SET WRITE_DELAY FALSE");}  //Always update data on disk
		finally {delayStmt.close();}



		connection.setAutoCommit(false);


		Statement sqlStmt = connection.createStatement();
		try {
			sqlStmt.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
					"message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
		} catch (SQLException e) {
			System.out.println("Warning: Database table \"messages\" already exists.");
		} finally {
			sqlStmt.close();
		}


		String stmt = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?)";
		PreparedStatement insertMessage = connection.prepareStatement(stmt);
		try {
			insertMessage.setString(1, "Alastair"); //set value of first "?" to "Alastair"
			insertMessage.setString(2, "Hello, Andy");
			insertMessage.setLong(3, System.currentTimeMillis());
			insertMessage.executeUpdate();
		} finally { //Notice use of finally clause here to finish statement
			insertMessage.close();
		}

		connection.commit();
		stmt = "SELECT nick,message,timeposted FROM messages "+
				"ORDER BY timeposted DESC LIMIT 10";
		PreparedStatement recentMessages = connection.prepareStatement(stmt);
		try {
			ResultSet rs = recentMessages.executeQuery();
			try {
				while (rs.next())
					System.out.println(rs.getString(1)+": "+rs.getString(2)+
							" ["+rs.getLong(3)+"]");
			} finally {
				rs.close();
			}
		} finally {
			recentMessages.close();
		}

	}
}
