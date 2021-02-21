package superchat;

import java.util.ArrayList; 

import java.io.File; 
import java.io.FileOutputStream; 
import java.io.FileInputStream; 
import java.io.ObjectOutputStream; 
import java.io.ObjectInputStream; 
import java.io.EOFException; 

import java.rmi.server.*; 
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;


/**
 * Create the "RMI register" and "Connector" used to communicate with clients, 
 * and load/save messages history on start/shut off.
 */
public class Server 
{
	public static void main(String[] args) 
	{
		new Server(parseArgs(args));
	}

	public static String parseArgs(String[] args)
	{
		if (args.length < 1) 
		{
			return "localhost";
		}

		return args[0];
	}
	
	
	// Path constants.
	private final String HOME_DIR_PATH = System.getProperty("user.home") 
		+ File.separator + ".superchat";
	private final String HISTORY_FILE_PATH = HOME_DIR_PATH + File.separator 
		+ "history"; 

	// Linker btw server and clients.
	private Connector.BasicConnector mConnector;

	public Server(String host)
	{
		mConnector = new Connector.BasicConnector();
		// Create/check existence of message history file. 
		createHomeDir();
		createHistoryFile();
		// And retrieve this history (if needed).
		retrieveMessageHistory();

		try 
		{
			// Avoid the "rmiregistry & / start rmiregistry" command if on local.
			if (host.equals("localhost"))
			{
				LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			}
			// Register the remoted object.
			Connector connector_stub = (Connector) 
				UnicastRemoteObject.exportObject(mConnector, 0);
			Registry registry = LocateRegistry.getRegistry(host);
			registry.rebind("rmi://server/ConnectService", connector_stub);
			// Save the messages when exiting.	
			Runtime.getRuntime().addShutdownHook(new Thread(() -> saveMessageHistory()));
		} 
		catch (Exception e) 
		{
			System.err.println("Error: " + e);
		}

		// Debug.
		System.out.println ("Server ready...");
	}

	public void retrieveMessageHistory()
	{
		try
		{
			// Open a stream to the file.
			ObjectInputStream stream = new ObjectInputStream(
					new FileInputStream(HISTORY_FILE_PATH));
			// Then read the messages.
			@SuppressWarnings("unchecked")
			ArrayList<Connector.Message> messages = (ArrayList<Connector.Message>) stream.readObject(); 

			if (messages != null)
			{
				mConnector.setClientMessages(messages);
			}

			stream.close();
		}
		catch (EOFException e) 
		{
			// If no EOFException then the history file is just empty => normal behavior.
		}
		catch (Exception e) 
		{
			// If no EOFException then the history file is empty.
			System.err.println("Error: cannot retrieve messages in the history file."); 
		}
	}	

	public void saveMessageHistory()
	{
		try
		{
			// Open a stream to the file.
			ObjectOutputStream stream = new ObjectOutputStream(
					new FileOutputStream(HISTORY_FILE_PATH));
			// Then write the messages. 
			stream.writeObject(mConnector.getClientMessages()); 

			stream.close();
		}
		catch (Exception e) 
		{
			System.err.println("Error: cannot save messages in the history file."); 
		}
	}

	private void createHomeDir()
	{
		File homeDir = new File(HOME_DIR_PATH);

		try
		{
			if (homeDir.exists() || homeDir.mkdirs()) 
			{
				return; 
			} 
			else 
			{
				throw new Exception();
			}
		}
		catch (Exception e)
		{
			System.err.println("Error: cannot create the Superchat home directory " + 
					HOME_DIR_PATH + "."); 
			System.exit(-1);
		}
	}

	private void createHistoryFile()
	{
		File file = new File(HISTORY_FILE_PATH);

		try
		{
			if (file.exists() || file.createNewFile())
			{
				return;
			}
			else
			{
				throw new Exception();
			}
		}
		catch (Exception e)
		{
			System.err.println("Error: cannot create the history file " + 
					HISTORY_FILE_PATH + "."); 
			System.exit(-1);
		}
	}
}
