# Github Submission Tool
This is a program used to submit homework assignments to private student
repositories on GitHub. An executable jar file should be distributed to
students. Note that this project is not currently under active development, and
has some bugs with it, which are detailed in the known bugs section. I may at
some points add additional features or fix things up, but development on this
will be sporatic at best. Feel free to fork this project though if you'd like
to add additional functionality :). This was originally developed and used for
the Spring 2017 Semester of Georgia Tech's CS1331 course, so some of the files
are a bit Georgia Techified. See the known bugs section. CS1331 is currently
not using this tool, and it is not in any way endorsed by Georgia Tech.
I'm currently just hosting it on GitHub as it is one of my personal projects.
The original repo for this on the Georgia tech GitHub had a very colorful
commit history, but I am redacting it as it contains contact information for
some people, and also many explitives.

## Dependencies

### Java
There are no dependencies on the student's end except for the most recent
version of Java. Testing has revealed that for the jar to work in its current
state, you need to be running Oracle JRE 1.8.0\_101+ on windows or Ubuntu, or
1.8.0\_91+ of the OpenJDK on Ubuntu. If you're Java is not up to date you will
get a really, really scary stacktrace. The executable jar should run cross
platform. The code works entirely through the GitHub web api, so students need
not even install git.

### gradle
gradle is used as the build tool for this project. The default task is to
generate an executable jar that can be distributed to students for purposes of
submitting their homework assignements.

## Features
* Creates a new, private repository if it does not exist.
* Adds a head TA as a collaborator so he/she can clone submissions to a
submissions repo at the due date.
* Supports resubmission so that students can push their files as much as they
please.
* Prints out error and help messages in the event that something goes wrong.
* Most of the files are documented fairly well, so shouldn't be terrible if you
would like to edit them.

## Known Bugs
* View open issues for known bugs that are in the process of being fixed.
* The method of getting the password from the user involves using some weird
System method, and this just so happens to not work when using terminal
emulators such as GitBash. So, if you're on Windows, you will need to run the
submission jar through cmd or PowerShell.
* The submitter does not recurse on directories, and will crash if it comes
across a subdirectory.
* Currently will just submit all files except class files in the specified
directories.
* Does not support filenames with spaces at the moment.
* Commit message for updating files on the repository is "Updating" which many
students have found confusing, and have come freaking out to us over in office
hours.
* Currently submits each file as its own commit, instead of bundling them
together and pushing once. This makes it difficult to identify versions of a
student's submission and roll back to earlier ones that worked if they
accidentally broke something.
* Some HTTP errors happen sporatically and need to be fixed with retries that
have not been implemented yet for some things.
* The properties file does not have too much effect on the messages the UI
prints out other than the help emails for the most part. Some more integration
should be done to make it so, but if you'd like to use this for your own
purposes, you'll need to change the UI file a bit.
* Does not deal with deleted files.

## Creating a homework submission tool

### 1. Edit gitHubSubmitter.properties
All modifications for new homework assignments should be possible by altering
the properties file, gitHubSubmitter.properties in src/main/resources. At the
time of authorship, the GitHub Web API only supports file transfers of up to
1MB. However, empirically we've had success submitting larger image files, so
YMMV.

* The properties file should be of this format:

```
+----------------------------+-----------------------------+
| submissionTool.properties  |                             |
+----------------------------------------------------------+
| #Sun Jan 29 12:25:37 EST 2017                            |
| prefix=hw                                                |
| className=CSXXXX                                         |
| assignmentName=test                                      |
| fileNames=Test.txt TestDir/                              |
| helpEmails=email@email.com email2@email.com              |
| headTA=headTAUsername                                    |
| hostURL=https\://api.github.com                          |
+----------------------------------------------------------+
```

* prefix is the prefix for the repository name (The repository will be named as
prefix-assignmentName-studentUsername).

* className is the name of the class that this tool is for.

* assignmentName is the name of the assignment.

* fileNames is a space delimited list of the files to be submitted to the
homework repository. Note that you can give a directory name, but the way it
is set up right now, it will not recurse on subdirectories, and it will flip
out if a subdirectory is actually there.

* helpEmails is a space delimited list of email addresses that will be
presented to students in the event that an unpredicted Exception is thrown
during the program.

* headTA is the username of the person who will be cloning these
repositores at the due date.

* hostURL is the url to the root directory of the github API. The : after https
needs to be escaped. If your organization has a private github enterprise
server, this is where you would set that. For example, for Georgia Tech, we
would put github.gatech.edu/api/v3 there.

### 2. Build
Run
```
gradle build
```

* This will produce github-submit.jar in the directory build/lib.
* The produced jar can be distributed to students so they may submit their
assignments.

## Using the jar
Simply place the jar in the directory where the homework files are located and
run

```
java -jar github-submit.jar
```

* This will prompt the user for credentials, and attempt to submit their
homework.
