package superchat;

import javax.swing.JTextArea;
import javax.swing.DefaultListModel;

import java.io.Serializable;

import java.rmi.*;
import java.rmi.server.*; 
import java.rmi.registry.*;


public interface Client extends Remote
{
	public String getName() throws RemoteException;

	/**
	 * Display the message sended by "sender". 
	 */
	public void writeMessage(String sender, String message) throws RemoteException;

	/**
	 * Notify that the user "name" is disconnected.
	 */
	public void notifyDisconnected(String name) throws RemoteException;

	/**
	 * Notify that the user "name" is connected.
	 */
	public void notifyConnected(String name) throws RemoteException;


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
		// To print messages and connected users.
		private Application mApp; 

		public BasicClient(String host)
		{
			mIsConnected = false;
			// Get server objects.
			getRemotedObjects(host);
		}

		/**
		 * Bind this client with the current running gui, to print the messages 
		 * and connected users.
		 */
		public void bindWithGUI(Application app)
		{
			mApp = app;
		}

		/**
		 * Connect the user to the server, and return true if successfull.
		 */
		public boolean connect(String name)
		{
			try 
			{
				// Try to create the user with the pseudo on the server side.
				if (! mConnector.connect(name))
				{
					mApp.addToChat("[Server]: Error, this pseudo is not available.");
					return false;
				}
			} 
			catch (Exception e)  
			{
				mApp.addToChat("[Server]: Error with the server, try again or " + 
						"relaunch the app.");
				return false;
			} 

			try
			{
				// Add this client to the registry.
				Client this_stub = (Client) 
					UnicastRemoteObject.exportObject((Client) this, 0);
				mRegistry.rebind("rmi://client/" + name, this_stub); 

				mName = name;
				mIsConnected = true;
			}
			catch (Exception e)
			{
				mApp.addToChat("[Server]: Error with the server, try again or " +
						"relaunch the app.");
				return false;
			}

			try 
			{
				// And spread this connection to every other client (and herself/himself).
				// And populate the connected users list.
				mConnector.getClientNames().stream().forEach(
						s -> 
						{
							try 
							{
								// Notify.
								Client client = (Client) mRegistry.lookup("rmi://client/" + s);
								client.notifyConnected(mName);
								// Populate.
								if (! s.equals(mName))
								{
									mApp.addToUsersList(s);
								}
							} 
							catch (Exception e)  
							{
								mApp.addToChat("[Server]: Error, cannot notify your connection " +
										"to \"" + s + "\"."); 
							}
						}
				) ;
			} 
			catch (Exception e)  
			{
				mApp.addToChat("[Server]: Error, cannot notify your connection."); 
			}

			mApp.addToChat("[Server]: You are connected as \"" + mName + "\"."); 
			return true;
		}

		/**
		 * Disconnect the user of the server by releasing her/his pseudo.
		 */
		public void disconnect()
		{
			mApp.addToChat("[Server]: Initiating your disconnection..."); 

			try
			{
				// Try to unbind the user on the server side.
				mRegistry.unbind("rmi://client/" + mName);
				mConnector.disconnect(mName);
				mIsConnected = false;
			}
			catch (Exception e)
			{
				mApp.addToChat("[Server]: Error, cannot completely disconnect you. " + 
						"Your username may be unavailable until the server restarts."); 
			}

			try 
			{
				// And spread this disconnection to every other client (and herself/himself).
				mConnector.getClientNames().stream().forEach(
						s -> 
						{
							try 
							{
								Client client = (Client) mRegistry.lookup("rmi://client/" + s);
								client.notifyDisconnected(mName);
							} 
							catch (Exception e)  
							{
								mApp.addToChat("[Server]: Error, cannot notify your disconnection " +
										"to \"" + s + "\"."); 
							}
						}
				) ;

				notifyDisconnected(mName);
			} 
			catch (Exception e)  
			{
				mApp.addToChat("[Server]: Error, cannot notify your disconnection."); 
			}
			// Remove the connected users.
			mApp.clearUsersList();	

			mApp.addToChat("[Server]: Disconnection finished."); 
		}

		/**
		 * Send the user message.
		 */
		public void sendMessage(String message)
		{
			try 
			{
				// Print the message for this client.
				mApp.addToChat("* " + mName + " > " + message);
				// And spread this message to every other client.
				mConnector.getClientNames().stream().filter(s -> ! s.equals(mName)).forEach(
						s -> 
						{
							try 
							{
								Client client = (Client) mRegistry.lookup("rmi://client/" + s);
								client.writeMessage(mName, message);
							} 
							catch (Exception e)  
							{
								mApp.addToChat("[Server]: Error, cannot distribute this message " +
										"to \"" + s + "\"."); 
							}
						}
				) ;
			} 
			catch (Exception e)  
			{
				mApp.addToChat("[Server]: Error, cannot distribute this message."); 
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
				mApp.addToChat("[Server]: Error with the server, please " + 
						"relaunch the app.");
				// Can't continue without them.
				System.exit(-1);
			}
		}

		@Override
		public void writeMessage(String sender, String message) throws RemoteException
		{
			mApp.addToChat(sender + " > " + message);
		}

		@Override
		public void notifyDisconnected(String name) throws RemoteException
		{
			mApp.removeFromUsersList(name);

			if (! name.equals(mName))
			{
				mApp.addToChat(name + " is disconnected.");
			}
		}

		@Override
		public void notifyConnected(String name) throws RemoteException
		{
			if (! name.equals(mName))
			{
				mApp.addToChat(name + " is connected.");
			}

			mApp.addToUsersList(name);
		}

		@Override
		public String getName() throws RemoteException
		{
			return mName;
		}

		public boolean isConnected()
		{
			return mIsConnected;		
		}
	}
}
