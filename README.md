# Online-Chat

An online chat for several users using Java RMI.

<p align="center">
<img src="assets/presentation_1.gif" width="550">
</p>

## Description

Based on Java RMI default implementation, the chat consists in one 
server application, and another for clients (i.e the server application 
should be launched on a host, before the clients can launch their one).

### Features:

* Each client can connect/disconnect whenever they want without leaving 
  the application.
	
* A real-time connected user list is proposed.

* On connection, the client will receive all the messages not received, 
  and will be able to see the connected users.
	
* Server logs indicate connections/disconnections.
	
* The server will load messages from last sessions on launch, and will save 
  them all on exit (by using a file in `$HOME/.superchat/`).

### Implementation:

* The `Linker` class:
	
	* Identify the clients each pseudo is unique (among the connected users) 
      and is stored here.
		
	* Every message sent is stored here. This allows to have the same date/
      time source for every client, and the server to retrieve every message 
      when exiting.
		
* The `Client` class:

	* Save states (pseudo and messages) on the server with the `Connector`. 

	* Communicate with other clients by saving itself in the _RMI register_ 
      (each `Client` will send a message/notify connection/notify disconnection 
      to others by fetching all the clients in the register, and sending them 
      the message). 
	
* The `Application` class:

	* The _GUI_ binded with a `Client`; the one launched by an user.
	
* The `Server` class:

	* Create the _RMI register_ and _Connector_.
	
	* Retrieve/Save the message history on launch/exit.

## Instructions

* First of all you need to compile the source code:

```console
user:~/Online-Chat/ $ make
```

* Next you need first to start the server. To do this you have
  to execute the following command:

	* If you are trying to launch the server on `localhost`:

```console
user:~/Online-Chat/ $ java -jar lib/Server.jar 
```

> Executing the `rmiregistry` command is not necessary; since the server 
  is launched on `localhost` everything is done programmatically. 

* Then to start a user connection:

```console
user:~/Online-Chat/ $ java -jar lib/Application.jar <host> 

```

> Where `host` is the machine on which the server was launched. Its
  default value is `localhost` if not specified.

## Troubleshooting

> Error: java.rmi.ConnectException

* You are not using `localhost` as the server host, and you may be 
  starting `rmiregistry` from the wrong folder.

> Exception in thread "main" java.awt.AWTError: Can't connect to \<X server\> 
  using \<IP address:x.x\> as the value of the DISPLAY variable.

* You may need to execute the `export DISPLAY=:0` command.

## Attributions

<div>Icon made by 
<a href="https://www.flaticon.com/authors/flat-icons" title="Flat Icons">Flat Icons
</a> from 
<a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com
</a>
</div>
