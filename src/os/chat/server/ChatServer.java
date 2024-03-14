package os.chat.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import os.chat.client.CommandsFromServer;
import os.chat.client.CommandsFromWindow;

/**
 * Each instance of this class is a server for one room.
 * <p>
 * At first there is only one room server, and the names of the room available
 * is fixed.
 * <p>
 * Later you will have multiple room server, each managed by its own
 * <code>ChatServer</code>. A {@link ChatServerManager} will then be responsible
 * for creating and adding new rooms.
 */
public class ChatServer implements ChatServerInterface {
	
	private String roomName;
	private Vector<CommandsFromServer> registeredClients;

	Registry registry;
	
  /**
   * Constructs and initializes the chat room before registering it to the RMI
   * registry.
   * @param roomName the name of the chat room
   */
	public ChatServer(String roomName){
		this.roomName = roomName;
		registeredClients = new Vector<CommandsFromServer>();

		try {
			ChatServerInterface stub = (ChatServerInterface) UnicastRemoteObject.exportObject(this, 0);
			registry = LocateRegistry.getRegistry();
			registry.rebind(roomName, stub);
		} catch (RemoteException e) {
			System.out.println("Cannot locate registry");
			e.printStackTrace();
		}
		System.out.println("ChatServer was created");
	}

	/**
	 * Publishes to all subscribed clients (i.e. all clients registered to a
	 * chat room) a message send from a client.
	 * @param message the message to propagate
	 * @param publisher the client from which the message originates
	 */	
	public void publish(String message, String publisher) {
		
		System.err.println("TODO: publish is not implemented");
		
		/*
		 * TODO send the message to all registered clients
		 */
	}

	/**
	 * Registers a new client to the chat room.
	 * @param client the name of the client as registered with the RMI
	 * registry
	 */
	public void register(CommandsFromServer client) {
		
		System.err.println("TODO: register is not implemented");
		
		/*
		 * TODO register the client
		 */

		registry.rebind();
		registeredClients.add(client);
	}

	/**
	 * Unregisters a client from the chat room.
	 * @param client the name of the client as registered with the RMI
	 * registry
	 */
	public void unregister(CommandsFromServer client) {
		
		System.err.println("TODO: unregister is not implemented");
		
		/*
		 * TODO unregister the client
		 */
	}
	
}
