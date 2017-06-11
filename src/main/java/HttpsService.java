import java.io.IOException;

/**
 * Represents some service for processing various HTTPS requests. Supports
 * GET, POST, and PUT requests.
 *
 * @author Jim Harris
 * @version 1.0 1/30/17
 */
public interface HttpsService {

    /**
     * Sends an HTTPS GET request.
     *
     * @param verb the kind of request (e.g GET, POST, PUT, DELETE, etc.).
     * @param url The url to send the request to.
     * @return a String containing the response from the server.
     * @throws IllegalArgumentException when properties is malformed.
     * @throws IOException when something goes wrong connecting to the server.
     */
    String get(String url) throws IllegalArgumentException, IOException;

    /**
     * Sends an HTTPS POST request.
     *
     * @param verb the kind of request (e.g GET, POST, PUT, DELETE, etc.).
     * @param url The url to send the request to.
     * @param properties an array of length 2 String arrays that represents the
     * properties for the post request. Each length 2 String[] is in the format
     * {property, value}.
     * @return a String containing the response from the server.
     * @throws IllegalArgumentException when properties is malformed.
     * @throws IOException when something goes wrong connecting to the server.
     */
    String post(String url, String[][] properties)
        throws IllegalArgumentException, IOException;

    /**
     * Sends an HTTPS PUT request.
     *
     * @param verb the kind of request (e.g GET, POST, PUT, DELETE, etc.).
     * @param url The url to send the request to.
     * @param properties an array of length 2 String arrays that represents the
     * properties for the post request. Each length 2 String[] is in the format
     * {property, value}.
     * @return a String containing the response from the server.
     * @throws IllegalArgumentException when properties is malformed.
     * @throws IOException when something goes wrong connecting to the server.
     */
    String put(String url, String[][] properties)
        throws IllegalArgumentException, IOException;

}
