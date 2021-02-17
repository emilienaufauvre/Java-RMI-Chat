import java.util.Scanner;

import java.io.Serializable;

import java.rmi.*;
import java.rmi.server.*; 
import java.rmi.registry.*;


public interface Client extends Remote
{
	public String getName() throws RemoteException;

	public void writeMessage(String sender, String message) throws RemoteException;


	public static void main(String[] args) throws RemoteException 
	{	
		if (args.length < 1) 
		{
			System.out.println("Usage: java Client <rmiregistry host>");
			return;
		}

		new BasicClient(args[0]).run();
	}


	@SuppressWarnings("serial")
	// Warnings:
	// - "serial": UID not necessary; only 1 version of this class.
	public class BasicClient implements Client, Serializable
	{
		// Current user state.
		private boolean mIsConnected;
		private String mName;
		// Remoted objects.
		private Registry mRegistry;
		private Connector mConnector;
		// To read the user inputs.
		private transient Scanner mScanner;

		private BasicClient(String host)
		{
			mIsConnected = false;
			mScanner = new Scanner(System.in);
			// Get server objects.
			getRemotedObjects(host);
		}

		/**
		 * The only method to directy call; offer commands to communicate with the server to the
		 * current user after connecting her/him.
		 */
		private void run()
		{
			// Deconnect the user when exiting.
			Runtime.getRuntime().addShutdownHook(new Thread()
					{
						@Override
						public void run()
						{
							deconnect();
						}
					}
			);
			
			connect();

			while (true)
			{
				sendMessage();
			}
		}

		/**
		 * Connect the user to the server by asking a pseudo.
		 */
		private void connect()
		{
			while (! mIsConnected)
			{
				mName = readInput("Please enter your pseudo:\n");

				try 
				{
					// Try to create the user with the pseudo on the server side.
					if (! (mIsConnected = mConnector.connect(mName)))
					{
						System.out.println("Error: pseudo not available.");
					}
				} 
				catch (Exception e)  
				{
					System.err.println("Error: pseudo not sended.");
				} 
			}

			try
			{
				// Add this client to the registry.
				Client this_stub = (Client) 
					UnicastRemoteObject.exportObject((Client) this, 0);
				mRegistry.rebind("rmi://client/" + mName, this_stub); 
			}
			catch (Exception e)
			{
				System.err.println("Error: can't register client.");
			}
		}

		/**
		 * Deconnect the user of the server by releasing her/his pseudo.
		 */
		private void deconnect()
		{
			if (mIsConnected)
			{
				try
				{
					mRegistry.unbind("rmi://client/" + mName);
					mConnector.deconnect(mName);
					mIsConnected = false;
				}
				catch (Exception e)
				{
					System.err.println("Error: can't unbind client.");
				}
			}
		}

		/**
		 * Ask and send the user message.
		 */
		private void sendMessage()
		{
			// Read/Ask for the message.
			String input = readInput("> ");

			try 
			{
				// Spread this message to every other client.
				mConnector.getClientNames().stream().filter(s -> ! s.equals(mName)).forEach(
						s -> 
						{
							try 
							{
								Client client = (Client) 
									mRegistry.lookup("rmi://client/" + s);
								client.writeMessage(mName, input);
							} 
							catch (Exception e)  
							{
								System.err.println("Error: when sending message/looking up to " 
										+ s + ".");
							}
						}
				) ;
			} 
			catch (Exception e)  
			{
				System.err.println("Error: when sending message, can't get clients." + e); 
			}
		}

		/**
		 * Load every remoted object reference from the server into memory.
		 */
		private void getRemotedObjects(String host)
		{
			try 
			{
				mRegistry = LocateRegistry.getRegistry(host); 
				mConnector = (Connector) mRegistry.lookup("rmi://server/ConnectService");
			} 
			catch (Exception e)  
			{
				System.err.println("Error: " + e);
				// Can't continue without them.
				System.exit(-1);
			}
		}

		/**
		 * Ask the user input with a dedicated message and return it.
		 */
		private String readInput(String message)
		{
			System.out.print(message);
			return mScanner.nextLine();
		}

		public void writeMessage(String sender, String message) throws RemoteException
		{
			System.out.println(sender + "> " + message);
		}

		public String getName() throws RemoteException
		{
			return mName;
		}
	}
}
