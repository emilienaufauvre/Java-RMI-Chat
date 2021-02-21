all: set clean compile

set:
	
	# Setting...
	@mkdir -p lib/
	@mkdir -p classes/

clean:
	
	# Cleaning...
	@rm -f -r lib/*
	@rm -f -r classes/*

compile:
	
	# Compiling the classes...
	@javac -Xlint -d classes/ src/*.java
	# Creating client exec jar...
	@cd classes/ \
		&& jar cvfe ../lib/Application.jar superchat.Application \
		superchat/Application* superchat/Connector* superchat/Client* \
		../assets
	# Creating server exec jar...
	@cd classes/ \
		&& jar cvfe ../lib/Server.jar superchat.Server \
		superchat/Server* superchat/Connector* superchat/Client*
