package i5.las2peer.services.imageCommentingService.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 
 * Stores the database credentials and provides query execution methods.
 * 
 */
public class MySQLDatabase{
	
	private Connection connection = null;
	private boolean isConnected = false;
	
	private String username = null;
	private String password = null;
	private String database = null;
	private String host = null;
	private int port = -1;
	
	
	/**
	 * 
	 * Constructor for a database instance.
	 * 
	 * @param username
	 * @param password
	 * @param database
	 * @param host
	 * @param port
	 * 
	 */
	public MySQLDatabase(String username, String password, String database, String host, int port){
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.database = database;
	}
	
	
	/**
	 * 
	 * Connects to the database.
	 * 
	 * @return true, if connected
	 * 
	 * @throws ClassNotFoundException if the driver was not found
	 * @throws SQLException if connecting did not work
	 * 
	 */
	public boolean connect() throws Exception{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String urlPrefix = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
			this.connection = DriverManager.getConnection(urlPrefix, this.username, this.password);
			
			if(!this.connection.isClosed()){
				this.isConnected = true;
				return true;
			}
			else{
				return false;
			}
		} 
		catch (ClassNotFoundException e){
			throw new ClassNotFoundException("JDBC-Driver for MySQL database type not found! Make sure the library is placed in the library folder!", e);
		}
		catch (SQLException e){
			throw e;
		}
	}
	
	
	/**
	 * 
	 * Disconnects from the database.
	 * 
	 * @return true, if correctly disconnected
	 * 
	 */
	public boolean disconnect(){
		try{
			this.connection.close();
			this.isConnected = false;
			this.connection = null;
			
			return true;
		} 
		catch (SQLException e){
			e.printStackTrace();
			this.isConnected = false;
			this.connection = null;
		}
		return false;
	}
	
	
	/**
	 * 
	 * Checks, if this database instance is currently connected.
	 * 
	 * @return true, if connected
	 * 
	 */
	public boolean isConnected(){
		try{
			return (this.isConnected && this.connection != null && !this.connection.isClosed());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 
	 * Executes a SQL statement to insert an entry into the database.
	 * 
	 * @param SQLStatment
	 * 
	 * @return true, if correctly inserted
	 * 
	 * @throws SQLException problems inserting
	 * 
	 */
	public void store(String SQLStatment) throws SQLException{
		// make sure one is connected to a database
		if(!isConnected())
			throw new SQLException("Not connected!");
		
		Statement statement = connection.createStatement();
		statement.executeUpdate(SQLStatment);
		
	}
	
	
	/**
	 *
	 * Executes a given query on the database.
	 *
	 * @param SQLStatment
	 *
	 * @return a ResultSet
	 *
	 * @throws SQLException problems inserting or not connected
	 *
	 */
	public ResultSet query(String SQLStatment) throws SQLException{
		// make sure one is connected to a database
		if(!isConnected())
			throw new SQLException("Not connected!");
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(SQLStatment);
		return resultSet;
		
	}
	
	
	public String getUser(){
		return this.username;
	}
	
	
	public String getPassword(){
		return this.password;
	}
	
	
	public String getDatabase(){
		return this.database;
	}
	
	
	public String getHost(){
		return this.host;
	}
	
	
	public int getPort(){
		return this.port;
	}
	
	
}
