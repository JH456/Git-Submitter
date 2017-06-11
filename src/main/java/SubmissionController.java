import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Handles the control flow for the submission tool. Panders between the
 * GiHubSubmitter and the UserInterface.
 *
 * @author Jim Harris
 * @version 1.0 1/29/17
 */
public class SubmissionController {
    private UserInterface cli;
    private GitHubSubmitter submitter;

    /**
     * Begins the submission process for the assignment. Will attempt to create
     * the repository, add collaborators, and submit files.
     *
     * @param properties the properties for the submission tool. Assumed that
     * all required properties have been checked and are nonnull.
     */
    public void start(Properties properties) {
        cli = new UserInterface(properties.getProperty("className"),
            properties.getProperty("helpEmails"));
        String username = cli.getUsername();
        String password = cli.getPassword();

        submitter = new GitHubSubmitter(
            properties.getProperty("hostURL"),
            properties.getProperty("prefix")
                + "-" + properties.getProperty("assignmentName")
                + "-" + username,
            properties.getProperty("headTA"),
            username,
            password,
            properties.getProperty("fileNames").split(" "));

        boolean success = false;
        success = createRepository() && addCollaborators() && submitFiles();

        if (success) {
            cli.printSuccessMessage(username, submitter.getRepositoryName());
        } else {
            cli.printFailureMessage();
        }
        cli.cleanup();
    }

    /**
     * Attempts to create the repository for the assignment. This stage of
     * the submission process succeeds as long as it has positive confirmation
     * that the repository exists.
     *
     * @return whether or not this stage of the process failed.
     */
    private boolean createRepository() {
        boolean requestGood = true;
        try {
            cli.printRepositoryMessage();
            submitter.createRepository();
            cli.printStageSuccessMessage();
        } catch (UnknownHostException e) {
            cli.printCouldNotConnectMessage();
            requestGood = false;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message.contains("422")) {
                cli.printRepositoryExistsMessage();
            } else if (message.contains("401")) {
                cli.printIncorrectCredentialsMessage();
                requestGood = false;
            } else {
                cli.printHelpMessage(e);
                requestGood = false;
            }
        }
        return requestGood;
    }

    /**
     * Attempts to add the collaborators to the repository. Assumes that the
     * repository exists. Will succeed as long as the necessary collaborators
     * are added to the repository.
     *
     * @return whether or not this stage of the process failed.
     */
    private boolean addCollaborators() {
        boolean requestGood = true;
        try {
            cli.printCollaboratorsMessage();
            submitter.addCollaborators();
            cli.printStageSuccessMessage();
        } catch (UnknownHostException e) {
            cli.printCouldNotConnectMessage();
            requestGood = false;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message.contains("401")) {
                cli.printIncorrectCredentialsMessage();
            } else {
                cli.printHelpMessage(e);
            }
            requestGood = false;
        }
        return requestGood;
    }

    /**
     * Attempts to submit the files to the repository. Succeeds in the event
     * that all files are added successfully.
     *
     * @return whether or not this stage of the process failed.
     */
    private boolean submitFiles() {
        boolean requestGood = true;
        try {
            cli.printFilesMessage();
            submitter.addFiles();
            cli.printStageSuccessMessage();
        } catch (FileNotFoundException e) {
            cli.printFileNotFoundMessage(e.getMessage());
            requestGood = false;
        } catch (UnknownHostException e) {
            cli.printCouldNotConnectMessage();
            requestGood = false;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message.contains("401")) {
                cli.printIncorrectCredentialsMessage();
            } else {
                cli.printHelpMessage(e);
            }
            requestGood = false;
        }
        return requestGood;
    }
}
