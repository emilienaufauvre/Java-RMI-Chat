package superchat;

import java.io.Serializable;

import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.rmi.*; 


/**
 * Connect the server and all the client together by saving
 * the list of usernames currently used, and the messages sent.
 */
public interface Connector extends Remote 
{
	/**
	 * Add the message from sender to the server history. The server will
	 * return the date/time the message was sent (thus all the message dates
	 * will be from the same source i.e. the server).
	 */
	public String addMessage(String sender, String message) throws RemoteException;

	/**
	 * Return true if the user was correctly created on the server side,
	 * Otherwise return false.
	 * Reasons why the operation could not be successfull:
	 * - An user with the same name already exists.
	 */
	public boolean connect(String name) throws RemoteException;

	/**
	 * Remove the client identified by name from the list of connected users.
	 */
	public void disconnect(String name) throws RemoteException;

	public ArrayList<String> getClientNames() throws RemoteException;

	public ArrayList<Message> getClientMessages() throws RemoteException;

	public void setClientMessages(ArrayList<Message> messages) throws RemoteException;


	public class BasicConnector implements Connector 
	{
		// Constants.
		private final String DATE_FORMAT = "HH:mm:ss";

		private ArrayList<String> mClientNames;
		private ArrayList<Message> mClientMessages;

		public BasicConnector()
		{
			mClientNames = new ArrayList<>();
			mClientMessages = new ArrayList<>();
		}

		@Override
		public String addMessage(String sender, String message) throws RemoteException
		{
			String time = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
			mClientMessages.add(new Message(time, sender, message));

			return time; 
		}

		@Override
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

		@Override
		public void disconnect(String name) throws RemoteException
		{
			System.out.println("Client exiting: " + name); 
			mClientNames.remove(name);
		}

		@Override
		public ArrayList<String> getClientNames() throws RemoteException
		{
			return mClientNames;
		}

		@Override
		public ArrayList<Message> getClientMessages() throws RemoteException
		{
			return mClientMessages;
		}

		@Override
		public void setClientMessages(ArrayList<Message> messages) throws RemoteException
		{
			mClientMessages = messages;
		}
	}


	@SuppressWarnings("serial")
	// Warnings:
	// - "serial": UID not necessary; only 1 version of this class.
	public class Message implements Serializable
	{
		private String mTime;
		private String mSender;
		private String mContent;

		private Message(String time, String sender, String content)
		{
			mTime = time;
			mSender = sender;
			mContent = content;
		}

		public String getTime()
		{
			return mTime;
		}

		public String getSender()
		{
			return mSender;
		}

		public String getContent()
		{
			return mContent;
		}
	}
}
