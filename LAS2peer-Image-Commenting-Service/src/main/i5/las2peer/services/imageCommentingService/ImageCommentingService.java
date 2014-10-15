package i5.las2peer.services.imageCommentingService;

import i5.las2peer.api.Service;
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

import java.io.IOException;

import org.json.JSONObject;


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
@Path("LAS2peerFosdemDemo")
@Version("0.1")
public class ImageCommentingService extends Service {


	/**
	 * This method is needed for every RESTful application in LAS2peer.
	 * 
	 * @return the mapping
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
     * 
     */
    @GET
    @Produces("application/json")
    @Path("/images/{imageId}/comments")
    public JSONObject getCommentCollection(@PathParam("imageId") String imageId)
    {
    	JSONObject commentCollection = null;
    	return commentCollection;
    }
    
    
    /**
     * 
     * 
     */
    @GET
    @Produces("application/json")
    @Path("/images/{imageId}/comments/{commentId}")
    public JSONObject getComment(@PathParam("imageId") String imageId, @PathParam("commentId") String commentId)
    {
    	JSONObject comment = null;
    	return comment;
    }
    
    
    /**
     * 
     * 
     */
    @PUT
    @Consumes("application/json")
    @Path("/images/{imageId}/comments")
    public void storeComment(@PathParam("imageId") String imageId)
    {
    	//do something
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
