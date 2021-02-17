all: set clean compile

set:
	
	# Setting...
	@mkdir -p lib/
	@mkdir -p classes/

clean:
	
	# Cleaning...
	@rm -f lib/*
	@rm -f classes/*

compile:
	
	# Creating lib... 
	@javac -d classes -cp .:classes src/Connector.java
	@jar cvf lib/Connector.jar classes/Connector.class 
	
	# Compiling server...
	@javac -d classes\
		-cp .:classes:lib/Connector.jar\
	   	src/Server.java 
	
	# Compiling client...
	@javac -d classes\
		-cp .:classes:lib/Connector.jar\
	   	src/Client.java 
