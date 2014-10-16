package i5.las2peer.services.albumService;

import i5.las2peer.api.Service;
import i5.las2peer.execution.L2pServiceException;
import i5.las2peer.p2p.AgentNotKnownException;
import i5.las2peer.p2p.TimeoutException;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.*;
import i5.las2peer.security.AgentException;
import i5.las2peer.security.L2pSecurityException;
import i5.las2peer.security.Mediator;
import i5.las2peer.security.UserAgent;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONStyle;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * LAS2peer Album Service
 *
 */
@Path("LAS2peerFosdemDemo/images")
@Version("0.1")
public class AlbumService extends Service {


	Pattern htmlFileListPattern = Pattern.compile(">(\\w|\\s)+\\.\\w+");
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

	@GET
	@Path("")
	@Produces("application/json")
	public HttpResponse getImageList(@QueryParam(name="filter", defaultValue = "") String filter)
	{

		HttpResponse response=null;
		JSONArray json = new JSONArray();

		Boolean filterContent = filter.length()>0;
		String result= null;
		try{
			result = ((HttpResponse) this.invokeServiceMethod("i5.las2peer.services.fileService.DownloadService","getFile0")).getResult();
			//filter html response for file names
			Matcher matcher = htmlFileListPattern.matcher(result);
			while(matcher.find())
			{
				String imageId=matcher.group().substring(1);
				if(filterContent)
				{
					if(imageId.toLowerCase().contains(filter))
					{
						json.add(imageId);
					}
				}
				else
				{
					json.add(imageId);
				}
			}
			response=new HttpResponse(json.toString(),200);
		}catch (L2pServiceException e){
			e.printStackTrace();
		}catch (AgentNotKnownException e){
			e.printStackTrace();
		}catch (L2pSecurityException e){
			e.printStackTrace();
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}catch (InvocationTargetException e){
			e.printStackTrace();
		}catch (InterruptedException e){
			e.printStackTrace();
		}catch (TimeoutException e){
			e.printStackTrace();
		}


		if(response==null)
			response=new HttpResponse("Error retrieving the image list",500);

		return response;
	}

	@GET
	@Path("{imageId}")
	@Produces("image/jpeg")
	public HttpResponse getImage(@PathParam("imageId") String imageId)
	{
		HttpResponse response=null;
		String result;

		try{
			result = ((HttpResponse) this.invokeServiceMethod("i5.las2peer.services.fileService.DownloadService","getFile1",imageId)).getResult();

			response=new HttpResponse(result,200);

		}catch (L2pServiceException e){
			e.printStackTrace();
		}catch (AgentNotKnownException e){
			e.printStackTrace();
		}catch (L2pSecurityException e){
			e.printStackTrace();
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}catch (InvocationTargetException e){
			e.printStackTrace();
		}catch (InterruptedException e){
			e.printStackTrace();
		}catch (TimeoutException e){
			e.printStackTrace();
		}
		if(response==null)
			response=new HttpResponse("Error retrieving the image",500);

		return response;
	}



}
