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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.jhlabs.image.EdgeFilter;

/**
 * 
 * LAS2peer Service
 * 
 * This is a template for a very basic LAS2peer service
 * that uses the LAS2peer Web-Connector for RESTful access to it.
 *
 * Description and License information for the filter and processing library "Filters.jar" can be found here:
 * http://www.jhlabs.com/ip/filters/index.html
 *
 */
@Path("LAS2peerFosdemDemo")
@Version("0.1")
public class ImageProcessingService extends Service {

	private static final String DEFAULT_IMAGE_OUTPUT_TYPE = "png";
	private String imageOutputType = DEFAULT_IMAGE_OUTPUT_TYPE;

	/**
	 * This method is needed for every RESTful application in LAS2peer.
	 * 
	 * @return the mapping
	 */
	public String getRESTMapping()
	{
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
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
		String imgStr = null;
		try {
			JSONObject o = (JSONObject) JSONValue.parseWithException(content);
			imgStr = (String) o.get("image");
		} catch (ParseException e) {
			e.printStackTrace();
			HttpResponse response = new HttpResponse("Passed content was not readable!", 500);
			return response;
		}
		if (imgStr == null) {
			HttpResponse response = new HttpResponse("Passed content was null!", 500);
			return response;
		}
		// decode image
		BufferedImage imageIn = null;
		try {
			imageIn = decodeToImage(imgStr);
		} catch (IOException e) {
			HttpResponse response = new HttpResponse("Passed content could not be decoded!", 500);
			return response;
		}
		if (imageIn == null) {
			HttpResponse response = new HttpResponse("Passed image was null!", 500);
			return response;
		}
		// apply filter/manipulation
		EdgeFilter filter = new EdgeFilter();
		BufferedImage imageOut = filter.filter(imageIn, null);
		// encode image
		String resultStr = "";
		try {
			resultStr = encodeToString(imageOut);
		} catch (IOException e) {
			HttpResponse response = new HttpResponse("Processed image could not be encoded!", 500);
			return response;
		}
		HttpResponse response = new HttpResponse(resultStr, 200);
		return response;
	}

	/**
	 * Decodes a Base64 encoded string into a Java BufferedImage object
	 * 
	 * @param imageString
	 * @return
	 * @throws IOException 
	 */
	public BufferedImage decodeToImage(String imageString) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] imageByte = decoder.decodeBuffer(imageString);
		ByteArrayInputStream bais = new ByteArrayInputStream(imageByte);
		BufferedImage result = ImageIO.read(bais);
		bais.close();
		return result;
	}

	/**
	 * Returns the given BufferedImage object as Base64 encoded String
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public String encodeToString(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, imageOutputType, baos);
		byte[] imageBytes = baos.toByteArray();
		BASE64Encoder encoder = new BASE64Encoder();
		String result = encoder.encode(imageBytes);
		baos.close();
		return result;
	}

}
