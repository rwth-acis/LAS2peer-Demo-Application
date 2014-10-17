package i5.las2peer.services.imageProcessingService;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.Version;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

/**
 * 
 * LAS2peer Service
 * 
 * This is a template for a very basic LAS2peer service
 * that uses the LAS2peer Web-Connector for RESTful access to it.
 * 
 * 
 *
 */
@Path("LAS2peerFosdemDemo")
@Version("0.1")
public class ImageProcessingService extends Service {



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
    @POST
    @Produces(MediaType.IMAGE_JPEG)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("processImage")
    public HttpResponse processImage(@ContentParam String content)
    {
    	String image = "";
		JSONObject o;
		try {
			o = (JSONObject) JSONValue.parseWithException(content);
			image = (String) o.get("image");
			//TODO
		} catch (ParseException e) {
			e.printStackTrace();
			HttpResponse response = new HttpResponse("Passed content was not readable!", 500);
	    	return response;
		}
		
		HttpResponse response = new HttpResponse(image, 200);
    	return response;
    }
}
