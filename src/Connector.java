import java.util.ArrayList;

import java.rmi.*;


public interface Connector extends Remote 
{
	/**
	 * Return true if the user was correctly created on the server side.
	 * Otherwise return false.
	 * Reasons why the operation could not be successfull:
	 * - An user with the same name already exists.
	 */
	public boolean connect(String name) throws RemoteException;

	public void deconnect(String name) throws RemoteException;

	public ArrayList<String> getClientNames() throws RemoteException;


	public class BasicConnector implements Connector 
	{
		private ArrayList<String> mClientNames;

		public BasicConnector()
		{
			mClientNames = new ArrayList<>();
		}

		public boolean connect(String name) throws RemoteException 
		{
			if (mClientNames.contains(name))
			{
				return false; 
			}

			System.out.println("Client joining: " + name); 
			mClientNames.add(name);
			return true;
		}

		public void deconnect(String name) throws RemoteException
		{
			System.out.println("Client exiting: " + name); 
			mClientNames.remove(name);
		}

		public ArrayList<String> getClientNames() throws RemoteException
		{
			return mClientNames;
		}
	}
}
