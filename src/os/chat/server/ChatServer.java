package os.chat.server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import os.chat.client.ChatClient;
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

	/**
	 * The name of the room
	 */
	private String roomName;

	/**
	 * Vector of the clients that are connected to the room
	 */
	private Vector<CommandsFromServer> registeredClients;

	/**
	 * Vector of the clients names
	 */
	private Vector<String> clientNames;

	/**
	 * The ChatServerManager instance bound to the registry
	 */
	private ChatServerManagerInterface csm;

	/**
	 * the rmi registry
	 */
	Registry registry;

  /**
   * Constructs and initializes the chat room before registering it to the RMI
   * registry.
   * @param roomName the name of the chat room
   */
	public ChatServer(String roomName){
		this.roomName = "room_" + roomName;
		registeredClients = new Vector<CommandsFromServer>();
		clientNames = new Vector<>();

		try {
			ChatServerInterface stub = (ChatServerInterface) UnicastRemoteObject.exportObject(this, 0);
			registry = LocateRegistry.getRegistry();
			registry.rebind(this.roomName, stub);
			csm = (ChatServerManagerInterface) registry.lookup("ChatServerManager");
		} catch (RemoteException e) {
			System.out.println("Cannot locate registry");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Cannot find ChatServerManager");
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
		String messageWiPub = publisher + " : " + message;
		for(int i = 0; i < registeredClients.size(); i++) {
			try {
				registeredClients.get(i).receiveMsg(roomName, messageWiPub);
			} catch (RemoteException e) {
				System.out.println("Cannot connect to client " + clientNames.get(i));
					registeredClients.remove(i);
					clientNames.remove(i);
			}
		}
	}

	/**
	 * Registers a new client to the chat room.
	 * @param client the name of the client as registered with the RMI
	 * registry
	 */
	public void register(CommandsFromServer client) {
		try {
			registeredClients.add(client);
			clientNames.add(client.getUserName());
		} catch (RemoteException e) {
			System.out.println("Cannot connect to client");
			e.printStackTrace();
		}
    }

	/**
	 * Unregisters a client from the chat room.
	 * @param client the name of the client as registered with the RMI
	 * registry
	 */
	public void unregister(CommandsFromServer client) {
		try {
			registeredClients.remove(client);
			clientNames.remove(client.getUserName());
		} catch (RemoteException e) {
			System.out.println("Cannot unregister client properly");
			e.printStackTrace();
		}
    }
	
}
