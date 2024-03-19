package os.chat.client;


import os.chat.server.ChatServer;
import os.chat.server.ChatServerInterface;
import os.chat.server.ChatServerManagerInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Vector;

/**
 * This class implements a chat client that can be run locally or remotely to
 * communicate with a {@link ChatServer} using RMI.
 */
public class ChatClient implements CommandsFromWindow,CommandsFromServer {

	/**
	 * The name of the user of this client
	 */
	private String userName;
	
  /**
   * The graphical user interface, accessed through its interface. In return,
   * the GUI will use the CommandsFromWindow interface to call methods to the
   * ChatClient implementation.
   */
	private final CommandsToWindow window ;

	/** The registry that allows locating all remotely-callable interfaces at the server side */
	Registry registry;

	/**
	 * The name of the server we want to connect to
	 */
	String serverLookUpName = "ChatServerManager";

	ChatServerManagerInterface csm;
	
  /**
   * Constructor for the <code>ChatClient</code>. Must perform the connection to the
   * server. If the connection is not successful, it must exit with an error.
   * 
   * @param window reference to the GUI operating the chat client
   * @param userName the name of the user for this client
   * @since Q1
   */
	public ChatClient(CommandsToWindow window, String userName) {
		this.window = window;
		this.userName = userName;

		try {
			registry = LocateRegistry.getRegistry();
			csm = (ChatServerManagerInterface) registry.lookup(serverLookUpName);
		} catch (RemoteException e) {
			System.out.println("cannot locate registry");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Cannot look up for " + serverLookUpName);
		}

		try {
			UnicastRemoteObject.exportObject(this,0);
		} catch (RemoteException e){
			System.out.println("Cannot export self");
			e.printStackTrace();
		}



	}

	/*
	 * Implementation of the functions from the CommandsFromWindow interface.
	 * See methods description in the interface definition.
	 */

	/**
	 * Sends a new <code>message</code> to the server to propagate to all clients
	 * registered to the chat room <code>roomName</code>.
	 * @param roomName the chat room name
	 * @param message the message to send to the chat room on the server
	 */
	public void sendText(String roomName, String message) {
		try {
			((ChatServerInterface) registry.lookup(roomName)).publish(message, userName);
		} catch (RemoteException e) {
			System.out.println("Cannot connect to " + roomName);
		} catch (NotBoundException e) {
			System.out.println("ChatServer not connected to registry");
			e.printStackTrace();
        }
    }

	/**
	 * Retrieves the list of chat rooms from the server (as a {@link Vector}
	 * of {@link String}s)
	 * @return a list of available chat rooms or an empty Vector if there is
	 * none, or if the server is unavailable
	 * @see Vector
	 */
	public Vector<String> getChatRoomsList() {
		try {
			return csm.getRoomsList();
		} catch (RemoteException e) {
			System.out.println("cannot call ChatServerManager.getRoomList()");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Join the chat room. Does not leave previously joined chat rooms. To
	 * join a chat room we need to know only the chat room's name.
	 * @param roomName the name (unique identifier) of the chat room
	 * @return <code>true</code> if joining the chat room was successful,
	 * <code>false</code> otherwise
	 */
	public boolean joinChatRoom(String roomName) {
		try {
			((ChatServerInterface) registry.lookup(roomName)).register(this);
		} catch (RemoteException e) {
			System.out.println("Cannot connect to remote");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Cannot find " + roomName);
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Leaves the chat room with the specified name
	 * <code>roomName</code>. The operation has no effect if it has not
	 * previously joined the chat room.
	 * @param roomName the name (unique identifier) of the chat room
	 * @return <code>true</code> if leaving the chat room was successful,
	 * <code>false</code> otherwise
	 */	
	public boolean leaveChatRoom(String roomName) {
		try {
			((ChatServerInterface) registry.lookup(roomName)).unregister(this);
		} catch (RemoteException e) {
			System.out.println("Cannot connect to remote");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Cannot find " + roomName);
			e.printStackTrace();
		}

		return true;
	}

    /**
     * Creates a new room named <code>roomName</code> on the server.
     * @param roomName the chat room name
     * @return <code>true</code> if chat room was successfully created,
     * <code>false</code> otherwise.
     */
	public boolean createNewRoom(String roomName) {
		try {
			csm.createRoom(roomName);
			return true;
		} catch (RemoteException e) {
			System.out.println("Cannot connect to ChatServerManager");
			e.printStackTrace();
		}

		return false;
	}

	/*
	 * Implementation of the functions from the CommandsFromServer interface.
	 * See methods description in the interface definition.
	 */
	
	
	/**
	 * Publish a <code>message</code> in the chat room <code>roomName</code>
	 * of the GUI interface. This method acts as a proxy for the
	 * {@link CommandsToWindow#publish(String chatName, String message)}
	 * interface i.e., when the server calls this method, the {@link
	 * ChatClient} calls the 
	 * {@link CommandsToWindow#publish(String chatName, String message)} method 
	 * of it's window to display the message.
	 * @param roomName the name of the chat room
	 * @param message the message to display
	 */
	public void receiveMsg(String roomName, String message) {
		window.publish(roomName, message);
	}

	public String getUserName() {
		return userName;
	}
		
	// This class does not contain a main method. You should launch the whole program by launching ChatClientWindow's main method.
}
