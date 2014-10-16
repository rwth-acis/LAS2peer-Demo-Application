package i5.las2peer.services.imageCommentingService.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
	 * Returns a collection of comments for the given imageId
	 * from the database.
	 *
	 * @param imageId
	 *
	 * @return a ResultSet
	 *
	 * @throws SQLException problems with the database or not connected
	 *
	 */
	public ResultSet queryForCollection(String imageId) throws SQLException{
		// make sure one is connected to a database
		if(!isConnected())
			throw new SQLException("Not connected!");
		
		PreparedStatement collectionQuery = this.connection.prepareStatement(
				  "SELECT ID FROM COMMENT WHERE IMAGEID = ?");
		collectionQuery.setString(1, imageId);
		
		ResultSet resultSet = collectionQuery.executeQuery();
		return resultSet;
	}
	
	
	/**
	 *
	 * Returns the comment with the given commentId
	 * from the database.
	 *
	 * @param commentId
	 *
	 * @return a ResultSet
	 *
	 * @throws SQLException problems with the database or not connected
	 *
	 */
	public ResultSet queryForComment(String commentId) throws SQLException{
		// make sure one is connected to a database
		if(!isConnected())
			throw new SQLException("Not connected!");
		
		PreparedStatement commentQuery = this.connection.prepareStatement(
				  "SELECT * FROM COMMENT WHERE ID = ?");
		commentQuery.setString(1, commentId);
		
		ResultSet resultSet = commentQuery.executeQuery();
		return resultSet;
	}
	
	
	/**
	 *
	 * Stores a comment with the given parameters to the database.
	 *
	 * @param author
	 * @param imageId
	 * @param content
	 * @param parameters
	 *
	 * @throws SQLException problems inserting or not connected
	 *
	 */
	public void insertComment(String author, String imageId, String content, String parameters) throws SQLException{
		// make sure one is connected to a database
		if(!isConnected())
			throw new SQLException("Not connected!");
		
		PreparedStatement insertCommentQuery = this.connection.prepareStatement(
				  "INSERT INTO COMMENT (AUTHOR,IMAGEID,COMMENT_CONTENT,PROCESSING_PARAMETERS)"
				  + "VALUES(?,?,?,?)");
		insertCommentQuery.setString(1, author);
		insertCommentQuery.setString(2, imageId);
		insertCommentQuery.setString(3, content);
		insertCommentQuery.setString(4, parameters);
		
		insertCommentQuery.executeUpdate();
	}
	
	
}
