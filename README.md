[![Generic badge](https://img.shields.io/badge/license-Unlicense-green.svg)](https://shields.io/)

<div align="center">
	<br>
	<br>
	<img src="assets/launcher.png" width="200" height="200">
	<h1>Superchat</h1>
	<p>
	<b>An online chat for several users using the Java RMI interface, and Swing.</b>
	</p>
	<br>
	<br>
	<br>
</div>

## Demonstration

<p align="center">
	<img src="assets/presentation_1.gif" width="100%">
	<br>
</p>

## Description

Based on Java RMI default implementation, the chat consists in one 
server application, and another for clients (i.e. the server application 
should be launched on a host, before the clients can launch their one).
Based on a P2P traffic model, our chat is centralized. 

### Features:

* Each client can connect/disconnect whenever they want without leaving 
  the application.
	
* A real-time connected user list is proposed.

* On connection, the client will receive all the messages not received, 
  and will be able to see the connected users.
	
* Server logs indicate connections/disconnections.
	
* The server will load messages from last sessions on launch, and will save 
  them all on exit (by using the file `$HOME/.superchat/history` on the host).

### Implementation:

* The `Linker` class/interface:

	* Created in the `Server`, and added in the _RMI register_ once.
	
	* Identifies the clients; each pseudo is unique (among the connected users) 
      and is stored here to ensure this uniqueness.
		
	* Every message sent is stored here. This allows to have the same source
	  for the date/time (which is associated to the messages) for every client
	  (a client could use the chat in Colombia while another in Japan, this 
	  would result in a synchronicity problem at the time level if the time source
	  was not the same).
	  Also, the messages are stored here from the `history` file on server start,
	  and retrieved to be saved by the server when exiting.
		
* The `Client` class/interface:

	* Created in the `Application`, and added in the _RMI register_ for every user.

	* Saves state (pseudo and messages) on the server side by using the `Linker`. 

	* Communicates with other clients by saving itself in the _RMI register_ (P2P). 
      Thus, each `Client` will send a message/notify connection/notify disconnection 
      to others by fetching all the clients in the register, and sending them 
      the message. To do all of this, clients are registered in the _RMI register_ 
	  with a URI containing their pseudo (which is unique). 
	
* The `Application` class:

	* The _GUI_ bound with a `Client`; the one launched by a user.

	* Informs the `Client` of the user's inputs so that it can act on them.
	
* The `Server` class:

	* Creates the _RMI register_ and `Linker`; needs to be launched only once,
	  before executing any `Application`.
	
	* Retrieves/Saves the message history on launch/exit, in the 
	  `$HOME/.superchat/history` file on the host.

### Possible improvements

* For security issues, the URI associated to a Client could be improved,
  and each pseudo associated to a password. 

* For backup issues, the message history file should not be stored on the
  host, but on the client side. We could also save the message history 
  every _X_ minutes or every _Y_ messages to avoid loosing them on a power cut 
  for example.

* For performance issues, limitations should be added (maximum number of clients,
  maximum length of messages, etc.), and recovery algorithms should be optimized.

* For ethical issues, the chat itself should not be used this way, because
  it implies a centralization of user data (names, messages, and metadata).
  Since we have already implemented a P2P traffic model, we could make this chat
  completly decentralized.

## Instructions

1. First you need to compile the source code:

	```console
	user:~/Java-RMI-Chat/ $ make
	```

2. Next you need first to start the server. To do this you have
  to execute the following command:

	* If you are trying to launch the server on `localhost`:

	```console
	user:~/Java-RMI-Chat/ $ java -jar lib/Server.jar 
	```

	> Executing the `rmiregistry &`/`start rmiregistry` command is not necessary; 
	since the server is launched on `localhost` everything is done programmatically. 

3. Then to start a user connection:

	```console
	user:~/Java-RMI-Chat/ $ java -jar lib/Application.jar <host> 

	```

	> Where `host` is the machine on which the server was launched. Its
	default value is `localhost` if not specified.

## Troubleshooting

```console
Error: java.rmi.ConnectException
```

* You are not using `localhost` as the server host, and you may be 
  starting `rmiregistry` from the wrong folder.

```console
Exception in thread "main" java.awt.AWTError: Can't connect to \<X server\> 
using \<IP address:x.x\> as the value of the DISPLAY variable.
```

* You may need to execute the `export DISPLAY=:0` command.

## Attributions

<div>
	Icon made by 
	<a href="https://www.flaticon.com/authors/flat-icons" title="Flat Icons">Flat Icons</a> 
	from 
	<a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
	.
</div>
