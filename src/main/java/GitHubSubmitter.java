import java.net.UnknownHostException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;
import java.util.Base64;

/**
 * GitHub submitter for CS1331 homework assignments. See the README.md for
 * information on tailoring the submission tool for specific classes, homeworks,
 * or quizzes.
 *
 * @author Jim Harris (jamesharris9456@gmail.com)
 * @version 1.0 1/29/17
 */
public class GitHubSubmitter {
    private HttpsService https;
    private String repositoryName;
    private String username;
    private String headTA;
    private String[] fileNames;
    private static final int RETRIES = 3;

    /**
     * Public constructor.
     *
     * @param propertiesFile the properties file.
     * @param hostURL the url to the github web API.
     * @param repositoryName what to name the repository.
     * @param headTA the GT ID of the headTA cloning the submissions.
     * @param username the username of the student.
     * @param password the password of the student.
     * @param fileNames the files for this assignment.
     */
    public GitHubSubmitter(String hostURL, String repositoryName,
        String headTA, String username, String password, String... fileNames) {

        this.https = new GitHubHttpsService(hostURL, username, password);
        this.repositoryName = repositoryName;
        this.username = username;
        this.headTA = headTA;
        this.fileNames = processFileNames(fileNames);
    }

    /**
     * Processes the fileNames.
     *
     * @param fileNames
     */
    private String[] processFileNames(String... fileNames) {
        ArrayList<String> fileNamesList = new ArrayList<String>();
        for (String fileName : fileNames) {
            File dir = new File(fileName);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.endsWith(".class");
                }
            });

            if (files != null) {
                for (File javaFile : files) {
                    fileNamesList.add(javaFile.toString());
                }
            } else {
                fileNamesList.add(fileName);
            }
        }
        String[] toReturn = new String[fileNamesList.size()];
        for (int i = 0; i < toReturn.length; i++) {
            String name = fileNamesList.get(i);
            if (name.indexOf("." + File.separator) == 0) {
                name = name.substring(2, name.length());
            }
            String separator = File.separator.replaceAll("\\\\", "\\\\\\\\");
            name = name.replaceAll(separator, "/");
            toReturn[i] = name;
        }
        return toReturn;
    }

    /**
     * @return the name of the repository.
     */
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * Attempts to create the user's repository for the homework assignment.
     * The repository will be made private. In the event that the repository
     * was created new, collaborators will be added.
     *
     * @return false in the event of authentication failure.
     * @throws IOException if there was a connection error, the repository
     * already exists, or there was an authentication error.
     */
    public boolean createRepository() throws IOException {
        https.post("/user/repos",
            new String[][]{
                {"name", this.repositoryName},
                {"private", "true"}
            });
        return true;
    }

    /**
     * Attempts to add a collaborator to the user's repository. Assumes the
     * repository already exists.
     *
     * @return false in the event of authentication failure or the repository
     * does not exist.
     * @throws IOException if there was a connection error or there was an
     * authentication error.
     */
    public boolean addCollaborators() throws IOException {
        https.put(String.format("/repos/%s/%s/collaborators/%s",
            this.username, this.repositoryName, this.headTA),
            new String[][]{
                {"permission", "push"},
            });
        return true;
    }

    /**
     * Attempts to create a new file in the Git repository.
     *
     * @param fileName the name of the file to create.
     * @param encodedContent the new file contents encoded in base 64.
     * @throws IOException if there was a connection issue, an authentication
     * issue, or the file exists.
     */
    public void createFile(String fileName, String encodedContent)
        throws IOException {
        https.put(String.format("/repos/%s/%s/contents/%s",
            this.username, this.repositoryName, fileName),
            new String[][]{
                {"path", fileName},
                {"message", "Initial add"},
                {"content", encodedContent}
            });
    }

    /**
     * Attempts to update an existing file in the Git repository.
     *
     * @param fileName the name of the file to update.
     * @param encodedContent the new file contents encoded in base 64.
     * @throws IOException if there was a connection issue, an authentication
     * issue, or the file does not exist.
     */
    public void updateFile(String fileName, String encodedContent)
        throws IOException {
        String response = https.get(String.format("/repos/%s/%s/contents/%s",
            this.username, this.repositoryName, fileName));
        int start = response.indexOf("\"sha\"") + 7;
        int end = response.indexOf("\"", start + 1);
        String sha = response.substring(start, end);
        https.put(String.format("/repos/%s/%s/contents/%s",
            this.username, this.repositoryName, fileName),
            new String[][]{
                {"path", fileName},
                {"message", "Updating"},
                {"content", encodedContent},
                {"sha", sha}
            });
    }

    /**
     * Attempts to create a file in the repository with several retries. Assumes
     * the repository already exsits.
     *
     * @param fileName the name of the file to update.
     * @param encodedContent the contents of the file encoded in Base64.
     * @param retries the number of retries if the connection fails with a 409.
     */
    private void createWithRetries(String fileName, String encodedContent,
        int retries) throws IOException {
        try {
            createFile(fileName, encodedContent);
        } catch (IOException e) {
            if ((e.getMessage().contains("409") || e.getMessage().contains("500")) && retries > 0) {
                createWithRetries(fileName, encodedContent, retries - 1);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to update a file to the repository with several retries. Assumes
     * the repository already exsits.
     *
     * @param fileName the name of the file to update.
     * @param encodedContent the contents of the file encoded in Base64.
     * @param retries the number of retries if the connection fails with a 409.
     */
    private void updateWithRetries(String fileName, String encodedContent,
        int retries) throws IOException {
        try {
            updateFile(fileName, encodedContent);
        } catch (IOException e) {
            if ((e.getMessage().contains("409") || e.getMessage().contains("500")) && retries > 0) {
                updateWithRetries(fileName, encodedContent, retries - 1);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to submit a file to the repository with several retries. Assumes
     * the repository already exsits.
     *
     * @param fileName the name of the file.
     * @param encodedContent the contents of the file encoded in Base64.
     * @param retries the number of retries if the connection fails with a 409.
     */
    private void pushChangesWithRetries(String fileName, String encodedContent,
        int retries) throws IOException {
        try {
            createWithRetries(fileName, encodedContent, RETRIES);
        } catch (IOException e) {
            if (e.getMessage().contains("422")) {
                updateWithRetries(fileName, encodedContent, RETRIES);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to submit files to the repository. Assumes the repository
     * already exists.
     *
     * @return false in the event of authentication failure or not all files
     * were able to be submitted.
     */
    public boolean addFiles() throws IOException {
        for (int i = 0; i < fileNames.length; i++) {
            FileInputStream file = new FileInputStream(new File(fileNames[i]));
            ArrayList<Byte> fileData = new ArrayList<>();
            while (file.available() > 0) {
                fileData.add((byte) file.read());
            }
            byte[] bytes = new byte[fileData.size()];
            for (int j = 0; j < fileData.size(); j++) {
                bytes[j] = fileData.get(j);
            }
            String encodedContent = new String(Base64.getEncoder()
                .encode(bytes));
            pushChangesWithRetries(fileNames[i], encodedContent, RETRIES);
        }
        return true;
    }
}
