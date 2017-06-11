import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;

/**
 * Represents a service for processing various HTTPS requests to a GitHub web
 * API. Supports GET, POST, and PUT requests.
 *
 * @author Jim Harris
 * @version 1.0 1/30/17
 */
public class GitHubHttpsService implements HttpsService {
    private String hostURL;
    private String username;
    private String password;

    /**
     * Public constructor.
     *
     * @param hostURL the url to the github web API.
     * @param username the username needed to authenticate with GitHub.
     * @param password the password needed to authenticate with GitHub.
     */
    public GitHubHttpsService(String hostURL, String username,
        String password) {
        this.hostURL = hostURL;
        this.username = username;
        this.password = password;
    }

    /**
     * @param urlExt An extension to this.hostURL.
     */
    @Override
    public String get(String urlExt) throws IllegalArgumentException,
        IOException {
        return https("GET", urlExt, new String[][]{});
    }

    /**
     * @param urlExt An extension to this.hostURL.
     */
    @Override
    public String post(String urlExt, String[][] properties)
        throws IllegalArgumentException, IOException {
        return https("POST", urlExt, properties);
    }

    /**
     * @param urlExt An extension to this.hostURL.
     */
    @Override
    public String put(String urlExt, String[][] properties)
        throws IllegalArgumentException, IOException {
        return https("PUT", urlExt, properties);
    }

    /**
     * Formats the properties string into JSON format.
     *
     * @param properties an array of length 2 String arrays that represents the
     * properties for the post request. Each length 2 String[] is in the format
     * {property, value}.
     * @return properties formatted into JSON.
     */
    private String jsonifyPropertyString(String[][] properties) {
        String propertyString = "{";
        for (int i = 0; i < properties.length - 1; i++) {
            propertyString += "\"" + properties[i][0] + "\":\""
                + properties[i][1] + "\",";
        }
        if (properties.length > 0) {
            propertyString += "\"" + properties[properties.length - 1][0]
                + "\":\"" + properties[properties.length - 1][1] + "\"";
        }
        propertyString += "}";
        return propertyString;
    }

    /**
     * Verifies that the properties array is not malformed.
     *
     * @param properties the properties to pass into the request.
     * @throws IllegalArgumentException in the event that properties is
     * malformed.
     */
    private void verifyProperties(String[][] properties)
        throws IllegalArgumentException {
        if (properties == null) {
            throw new IllegalArgumentException("properties must be non-null.");
        } else {
            for (int i = 0; i < properties.length; i++) {
                String[] property = properties[i];
                if (property == null) {
                    throw new IllegalArgumentException("Element " + i
                        + " in properties is null, and must be non-null.");
                }
                if (property.length != 2) {
                    throw new IllegalArgumentException("Element " + i
                        + " in properties is of length " + property.length
                        + " and must be of length 2.");
                }
            }
        }
    }

    /**
     * Sends an https request. Site should support HTTPS. The return value of
     * this method is the server's response. I'll include some comments in the
     * code for clarity.
     *
     * @param verb the kind of request (e.g GET, POST, PUT, DELETE, etc.).
     * @param urlExt An extension to this.hostURL.
     * @param properties an array of length 2 String arrays that represents the
     * properties for the post request. Each length 2 String[] is in the format
     * {property, value}.
     * @return a String containing the response from the server.
     * @throws IllegalArgumentException when properties is malformed.
     * @throws IOException when something goes wrong connecting to the server.
     */
    private String https(String verb, String urlExt, String[][] properties)
        throws IllegalArgumentException, IOException {
        verifyProperties(properties);

        // Creates the connection.
        URL obj = new URL(this.hostURL + urlExt);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod(verb.toUpperCase());

        // Uses basic authentication with the server.
        String encoded = new String(Base64.getEncoder().encode(
                (this.username + ":" + this.password).getBytes()));
        con.setRequestProperty("Authorization", "Basic " + encoded);

        // Formats the properties as a JSON object.
        String propertyString = jsonifyPropertyString(properties);

        // Sending properties murders everything with GET requests.
        if (!verb.toUpperCase().equals("GET")) {
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(propertyString);
            out.flush();
            out.close();
        }

        // Gets the server's response.
        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
