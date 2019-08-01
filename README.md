# Server-Client application
This java Application consists of a server that opens a socket and restricts input to at most 5 concurrent
clients. Clients will connect to the Application and write any number of 9 digit
numbers, and then close the connection. The Application must write a de-duplicated
list of these numbers to a log file in no particular order.
## Requirements
- Java 8+ : [JDK 8]
- Maven 3.5.4+ : [Maven]
## Build
In the project folder, run the following command:

`mvn package`

It will generate the application.jar file in the target folder.

## Run Server
To start the app.server using maven run the following command:

`mvn exec:java -Dexec.mainClass="app.Application"`

Or you can also open a terminal where the jar is located and run the following command:

`java -jar applicationserver.jar`

## Run clients to test
There are a few clients (in the jar) to test the app.server.
To execute the different types of app.client run one of the following commands in a new terminal. If the app.server or host is not running they will not work.
- MultiClient: Executes 5 threads to connect to the app.server and send data for a given number of seconds

`mvn exec:java -Dexec.mainClass="app.client.MultiClient" -Dexec.args="<host> <port> <seconds>"`

- SingleClient: Sends one valid data to the app.client and closes.

`mvn exec:java -Dexec.mainClass="app.client.SingleClient" -Dexec.args="<host> <port>"`

- NonNumericClient: Sends invalid data to the app.client and closes.

`mvn exec:java -Dexec.mainClass="app.client.NonNumeriClient" -Dexec.args="<host> <port>"`

- TerminateClient: Sends the "terminate" command to shutdown the app.server.

`mvn exec:java -Dexec.mainClass="app.client.TerminateClient" -Dexec.args="<host> <port>"`

Or you can also open a terminal where the jar is located and run the following command:

`java -cp applicationserver.jar app.client.<Client> <host> <port>`

### Things to take in consideration
- Every time the app.server starts it creates a logs folder with a *number.app.server.log* file which will contain all the numbers processed.
The logs folder is created in the same directory where the console is running.
- If the app.server is executed from the maven command and a terminal signal is received, it shutdowns but an exception is thrown.

[JDK 8]: https://jdk.java.net/8/
[Maven]: https://maven.apache.org/install.html
