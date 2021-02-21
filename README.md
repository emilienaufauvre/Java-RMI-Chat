# Online-Chat

An online chat for several users using Java RMI.

## Instructions

* First of all you need to compile the source code:

```console
user:~/Online-Chat/ $ make
```

* Next you need first to start the server. To do this you have
  to execute the following command:

	* If you are trying to launch the server on _localhost_:

```console
user:~/Online-Chat/ $ java -jar lib/Server.jar 
```

> Executing the _rmiregistry_ command is not necessary; since the server 
  is launched on _localhost_ everything is done programmatically. 

* Then to start a user connection:

```console
user:~/Online-Chat/ $ java -jar lib/Application.jar <host> 

```

> Where _host_ is the machine on which the server was launched. Its
  default value is _localhost_ if not specified.

## Troubleshooting

> Error: java.rmi.ConnectException

* You are not using _localhost_ as the server host, and you may be 
  starting _rmiregistry_ from the wrong folder.

> Exception in thread "main" java.awt.AWTError: Can't connect to <X server> 
  using <IP address:x.x> as the value of the DISPLAY variable.

* You may need to execute the `export DISPLAY=:0` command.

## Attributions

<div>Icon made by 
<a href="https://www.flaticon.com/authors/flat-icons" title="Flat Icons">Flat Icons
</a> from 
<a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com
</a>
</div>
