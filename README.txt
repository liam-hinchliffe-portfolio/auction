
----- PREREQUISITES -----
Java 15 SDK
IntelliJ
JUnit 5 for unit tests
Mockito for data mocking

---------------------------

1 ) This system can be opened as an IntelliJ project or run through the command line.

2 ) There should be an instance of the Server class running either as an IntelliJ application configuration or as an instance in the command line

3 ) Then run an instance of the GUI class,  either as an IntelliJ application configuration or as an instance in the command line

4 ) Running the GUI class will create the GUI for the frontend which interacts with the server

5 ) Multiple instances of the GUI can be running, with multiple concurrent connections to the server

6 ) When you run the GUI, you will be presented with the login page where you can login with one of the following sets of credentials

	user: user
	password: password


	user: userTwo
	password: password

7a ) If you want to run the tests against the server, you will need to open the project in IntelliJ.
7b ) Make sure you have JUnit 5 and Mockito installed
7c) Run the ServerTest class an IntelliJ JUnit application configuration


----- NOTES ------
Once the server is running, the mocked auctions within the system will begin to expire.
The mocked auctions have different expiration dates, with longest being 3 minutes after the server has started.

---- WALKTRHROUGH VIDEO -----
https://drive.google.com/file/d/1kBAXa322rDQWFG8M0XjUJp81RhwbGAFP/view?usp=sharing
