package i5.las2peer.services.imageCommentingService;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.PUT;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.imageCommentingService.database.MySQLDatabase;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * LAS2peer Image Commenting Service
 * 
 * This service is part of the LAS2peer Demo Application.
 * https://github.com/rwth-acis/LAS2peer-Demo-Application/
 * 
 * It connects to a MySQL database and offers an interface
 * to load and store image comments together with processing
 * parameters.
 * 
 */
@Path("LAS2peerFosdemDemo/images")
@Version("0.1")
public class ImageCommentingService extends Service {
	
	
	private String databaseName;
	private String databaseHost;
	private int databasePort;
	private String databaseUser;
	private String databasePassword;
	
	private MySQLDatabase database; //The database instance to write to.
	
	
	/**
	 * 
	 * This method is needed for every RESTful application in LAS2peer.
	 * 
	 * @return the mapping
	 * 
	 */
    public String getRESTMapping()
    {
        String result="";
        try {
            result= RESTMapper.getMethodsAsXML(this.getClass());
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }
    
    
    /**
     * 
     * The constructor reads the property file and tries to connect to the given database.
     * 
     */
	public ImageCommentingService(){
		this.setFieldValues(); //Sets the values of the configuration file
		this.database = new MySQLDatabase(this.databaseUser, this.databasePassword,
				this.databaseName, this.databaseHost, this.databasePort);
		try {
			this.database.connect();
			System.out.println("Image Commenting Service: Database connected!");
		} catch (Exception e) {
			System.out.println("Image Commenting Service: Could not connect to database!");
			e.printStackTrace();
		}
	}
    
    
    /**
     * 
     * Returns a collection of comments for the given imageId.
     * 
     */
    @SuppressWarnings("unchecked")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{imageId}/comments")
    public HttpResponse getCommentCollection(@PathParam("imageId") String imageId)
    {
    	JSONArray comments = new JSONArray();

    	try {
			ResultSet result = this.database.queryForCollection(imageId);
			while(result.next()){
		    	JSONObject comment = new JSONObject();
		    	
		    	int commentId = result.getInt(1);
				String author = result.getString(2);
				//the third would be the imageId
				String content = result.getString(4);
				String parameters = result.getString(5);
				comment.put("commentId", commentId);
				comment.put("author", author);
				comment.put("content", content);
				comment.put("parameters", parameters); //TODO
				
				comments.add(comment);
				
			}
		} catch (SQLException e) {
			System.out.println(e);
			HttpResponse response = new HttpResponse(e.toString(), 500);
	    	return response;
		}
    	
    	
		HttpResponse response = new HttpResponse(comments.toJSONString(), 200);
		response.setHeader("Content-Type", MediaType.APPLICATION_JSON);
    	return response;
    }
    
    
    /**
     * 
     * Returns a comment for the given imageId and commentId.
     * 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{imageId}/comments/{commentId}")
    public HttpResponse getComment(@PathParam("imageId") String imageId, @PathParam("commentId") String commentId)
    {
    	JSONObject comment = new JSONObject();
		HttpResponse response = new HttpResponse(comment.toJSONString());
		response.setHeader("Content-Type", MediaType.APPLICATION_JSON);
		response.setStatus(200);
    	return response;
    }
    
    
    /**
     * 
     * Stores a given comment to the database.
     * 
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{imageId}/comments")
    public HttpResponse HttpResponse (@PathParam("imageId") String imageId)
    {
		HttpResponse response = new HttpResponse("successfull");
		response.setStatus(201);
    	return response;
    }
    
    
	/**
	 * To be removed in final version..
	 */
    public boolean debugMapping()
	{
		String XML_LOCATION = "./restMapping.xml";
		String xml= getRESTMapping();

		try{
			RESTMapper.writeFile(XML_LOCATION,xml);
		}catch (IOException e){
			e.printStackTrace();
		}

		XMLCheck validator= new XMLCheck();
		ValidationResult result = validator.validate(xml);

		if(result.isValid())
			return true;
		return false;
	}
}
