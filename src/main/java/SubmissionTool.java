import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Main entry point for the GitHub submission tool for CS1331. This is a tool
 * for submitting homework assignments to the Georgia Tech GitHub.
 *
 * @author Jim Harris
 * @version 1.0 1/29/17
 */
public class SubmissionTool {
    private static final String[] REQUIRED_PROPERTIES = {
        "hostURL",
        "prefix",
        "assignmentName",
        "headTA",
        "className",
        "helpEmails",
        "fileNames"
    };

    /**
     * Main entry point of the program. Will check the properties file and
     * then pass it along to the control logic if it is all good.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        InputStream input =
            new SubmissionTool().getClass().getResourceAsStream(
                "submissionTool.properties");
        if (input == null) {
            System.out.println("Properties file is missing!");
        } else {
            try {
                Properties properties = new Properties();
                properties.load(input);

                boolean allPropertiesNonNull = true;

                for (String property : REQUIRED_PROPERTIES) {
                    if (properties.getProperty(property) == null) {
                        allPropertiesNonNull = false;
                        System.out.println("Aborting. " + property
                            + "is missing from properties file!");
                    }
                }

                if (allPropertiesNonNull) {
                    new SubmissionController().start(properties);
                }
            } catch (IOException e) {
                System.out.println("Properties file is malformed!");
            }
        }
    }
}
