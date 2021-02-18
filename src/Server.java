import java.rmi.*; 
import java.rmi.server.*; 
import java.rmi.registry.*;


public class Server 
{
	public static void main(String[] args) 
	{
		try 
		{
			// Create the remoted objects.
			Connector.BasicConnector connector = new Connector.BasicConnector();
			Connector connector_stub = (Connector) 
				UnicastRemoteObject.exportObject(connector, 0);
			// Register the remoted objects.
			Registry registry = LocateRegistry.getRegistry(parseArgs(args)); 
			registry.rebind("rmi://server/ConnectService", connector_stub);
			// Debug.
			System.out.println ("Server ready...");
		} 
		catch (Exception e) 
		{
			System.err.println("Error: " + e);
		}
	}

	public static String parseArgs(String[] args)
	{
		if (args.length < 1) 
		{
			return "localhost";
		}

		return args[0];
	}
}
