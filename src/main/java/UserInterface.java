import java.util.Scanner;

/**
 * Command line user interface for this program. Handles I/O with the user.
 *
 * @author Jim Harris
 * @version 1.0 1/29/17
 */
public class UserInterface {
    private Scanner input;
    private String className;
    private String helpEmails;

    /**
     * Public constructor.
     *
     * @param className the name of the class this tool is for.
     * @param helpEmails space delimited list of tech support emails.
     */
    public UserInterface(String className, String helpEmails) {
        this.input = new Scanner(System.in);
        this.helpEmails = helpEmails;
        this.className = className;
    }

    /**
     * Outputs a prompt for the user to input their GT username.
     *
     * @return the username of the user.
     */
    public String getUsername() {
        System.out.print("=======================================\n");
        System.out.println(className + " Assignment Submission Tool");
        System.out.print("=======================================\n");
        System.out.print("Enter your GT username! (e.g gburdell3)\n> ");
        String username = input.nextLine();
        return username;
    }

    /**
     * Outputs a prompt for the user to input their GT password. The prompt
     * requires that the user be running some actual terminal, rather than
     * some emulator. As a result, GitBash will not work, but CMD, or PowerShell
     * or actual Bash will work just fine.
     *
     * @return the password of the user.
     */
    public String getPassword() {
        System.out.print("\nEnter your GT password!\n> ");
        String password = new String(System.console().readPassword());
        System.out.print("=======================================\n");
        return password;
    }

    /**
     * Prints a message for when creating the repository.
     */
    public void printRepositoryMessage() {
        System.out.println("\nCreating repository...");
    }

    /**
     * Prints a message for when adding the collaborators.
     */
    public void printCollaboratorsMessage() {
        System.out.println("\nAdding collaborators...");
    }

    /**
     * Prints a message for when submitting the files.
     */
    public void printFilesMessage() {
        System.out.println("\nSubmitting files...");
    }

    /**
     * Prints a message for when a file is missing.
     *
     * @param message the message from the Exception.
     */
    public void printFileNotFoundMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints a message for when the repository already exists.
     */
    public void printRepositoryExistsMessage() {
        System.out.println("Repository already exists! Continuing.");
    }

    /**
     * Prints a messsage when a particular stage is successful.
     */
    public void printStageSuccessMessage() {
        System.out.println("Success!");
    }

    /**
     * Prints a help message for when something goes wrong.
     *
     * @param e the Exception to print the stack trace of.
     */
    public void printHelpMessage(Exception e) {
        System.out.println("Something has gone wrong! Please let us "
            + "know, and send us this stack trace and any details about "
            + "your usage of this program:\n");
        e.printStackTrace();
        System.out.println("\nEmail these people for help: " + helpEmails);
    }

    /**
     * Prints a message when the server could not be connected to.
     */
    public void printCouldNotConnectMessage() {
        System.out.println("Could not connect to server! Check your "
            + "internet connection!");
    }

    /**
     * Prints a message when authentication failed with the server.
     */
    public void printIncorrectCredentialsMessage() {
        System.out.println("Incorrect login credentials! Aborting.");
    }

    /**
     * Prints a message for when submission was successful.
     */
    public void printSuccessMessage(String username, String homeworkName) {
        System.out.println("=======================================");
        System.out.println("\nHomework submitted! Go to "
            + String.format("https://github.gatech.edu/%s/%s", username,
            homeworkName)
            + " to make sure all of your files are there and up to date!"
            + "\n\nIf you click on the button on that page to Clone or "
            + "download, you'll see a button to download a zip of your "
            + "submission. Download this and make sure your code compiles "
            + "and runs properly. Non-compiling solutions will receive a 0,"
            + " and claiming that Git messed up is not a valid excuse. If "
            + "this tool is not working properly, contact your head TA "
            + "to arrange the timely submission of your homework through "
            + "other reasonable means."
            + "\n\nYou shouldn't modify the Git repository through the web"
            + " interface or by pushing manually, but feel free to "
            + "resubmit with this tool as much as you want! Do not change "
            + "The repository's privacy settings. Doing so during or after "
            + "the semester constitutes a violation of Georgia Tech's "
            + "honor code. Also, do not change the collaborator settings, "
            + "or we may not be able to pull your submission. Also do not "
            + "add additional students as collaborators, again with the "
            + "honor code.");
    }

    /**
     * Prints a message for when the submission fails.
     */
    public void printFailureMessage() {
        System.out.println("\nSomething went wrong!");
    }

    /**
     * Closes resources.
     */
    public void cleanup() {
        input.close();
    }
}
