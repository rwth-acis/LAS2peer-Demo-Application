package i5.las2peer.services.imageCommentingService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import i5.las2peer.p2p.LocalNode;
import i5.las2peer.security.ServiceAgent;
import i5.las2peer.security.UserAgent;
import i5.las2peer.testing.MockAgentFactory;
import i5.las2peer.webConnector.WebConnector;
import i5.las2peer.webConnector.client.ClientResponse;
import i5.las2peer.webConnector.client.MiniClient;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 * LAS2peer Image Commenting Service - Tests
 * 
 * This service is part of the LAS2peer Demo Application.
 * https://github.com/rwth-acis/LAS2peer-Demo-Application/
 * 
 * These JUnit test cases test the three main functionalities of the
 * service, which are retrieval of a collection of all comments of an
 * image, retrieval of a specific comment and the persistence of a
 * comment.
 * 
 * NOTE: Test only work with a database setup with the sql-scripts located in
 * the "scripts" folder. Credentials have to be supplied in the "etc" folder.
 * 
 */
public class ServiceTest {
	
	private static final String HTTP_ADDRESS = "http://127.0.0.1";
	private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;
	
	private static LocalNode node;
	private static WebConnector connector;
	private static ByteArrayOutputStream logStream;
	
	private static UserAgent testAgent;
	private static final String testPass = "adamspass";
	
	private static final String testServiceClass = "i5.las2peer.services.imageCommentingService.ImageCommentingService";
	
	private static final String mainPath = "LAS2peerFosdemDemo/images";
	
	
	/**
	 * Called before the tests start.
	 * 
	 * Sets up the node and initializes connector and users that can be used throughout the tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void startServer() throws Exception {
		
		//start node
		node = LocalNode.newNode();
		node.storeAgent(MockAgentFactory.getAdam());
		node.launch();
		
		ServiceAgent testService = ServiceAgent.generateNewAgent(testServiceClass, "a pass");
		testService.unlockPrivateKey("a pass");
		
		node.registerReceiver(testService);
		
		//start connector
		logStream = new ByteArrayOutputStream ();
		
		connector = new WebConnector(true,HTTP_PORT,false,1000);
		connector.setSocketTimeout(10000);
		connector.setLogStream(new PrintStream (logStream));
		connector.start ( node );
        Thread.sleep(1000); //wait a second for the connector to become ready
		testAgent = MockAgentFactory.getAdam();
		
        connector.updateServiceList();
        //avoid timing errors: wait for the repository manager to get all services before continuing
        try
        {
            System.out.println("waiting..");
            Thread.sleep(10000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
		
	}
	
	
	/**
	 * Called after the tests have finished.
	 * Shuts down the server and prints out the connector log file for reference.
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void shutDownServer () throws Exception {
		
		connector.stop();
		node.shutDown();
		
        connector = null;
        node = null;
        
        LocalNode.reset();
		
		System.out.println("Connector-Log:");
		System.out.println("--------------");
		
		System.out.println(logStream.toString());
		
    }
	
	
	/**
	 * 
	 * Tries to retrieve a comment collection for a predefined image.
	 * Tests for type and size of result.
	 * 
	 */
	@Test
	public void testCommentCollectionRetrieval()
	{
		String imageId = "SomeImage.png";
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
		
		try
		{
			c.setLogin(Long.toString(testAgent.getId()), testPass);
			ClientResponse result = c.sendRequest("GET", mainPath + "/" + imageId + "/comments", "");
			assertEquals(200, result.getHttpCode());
			Object o = JSONValue.parseWithException(result.getResponse().trim());
			assertTrue(o instanceof JSONArray);
			JSONArray jsonArray = (JSONArray) o;
			assertTrue(jsonArray.size() == 3);
			
			System.out.println("Result of 'testCommentCollectionRetrieval': " + jsonArray.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail ( "Exception: " + e );
		}
		
    }
	
	
	/**
	 * 
	 * Tries to retrieve a specific comment for a specific image.
	 * Checks the type, size and content of the response for correctness.
	 * 
	 */
	@Test
	public void testCommentRetrieval()
	{
		String imageId = "SomeImage.png";
		String commentId = "1";
		String content = "Great Image";
		String parameters = "parametersLater";
		String author = "Thomas";
		
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
		
		try
		{
			c.setLogin(Long.toString(testAgent.getId()), testPass);
			ClientResponse result = c.sendRequest("GET", mainPath + "/" + imageId + "/comments/" + commentId, "");
			assertEquals(200, result.getHttpCode());
			Object o = JSONValue.parseWithException(result.getResponse().trim());
			assertTrue(o instanceof JSONObject);
			JSONObject jsonObject = (JSONObject) o;
			assertTrue(jsonObject.size() == 5);
			assertEquals(commentId, jsonObject.get("commentId"));
			assertEquals(author, jsonObject.get("author"));
			assertEquals(imageId, jsonObject.get("imageId"));
			assertEquals(content, jsonObject.get("content"));
			assertEquals(parameters, jsonObject.get("parameters"));
			
			System.out.println("Result of 'testCommentRetrieval': " + jsonObject.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail ( "Exception: " + e );
		}
		
    }
	
	
	/**
	 * 
	 * 
	 */
	@Test
	public void testCommentPersistence()
	{
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
		
		try
		{
			//Something
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail ( "Exception: " + e );
		}
		
    }
	
	
	/**
	 * Test the ServiceClass for valid rest mapping.
	 * Important for development, will be removed in final version.
	 */
	@Test
	public void testDebugMapping()
	{
		ImageCommentingService cl = new ImageCommentingService();
		assertTrue(cl.debugMapping());
	}
	
}
