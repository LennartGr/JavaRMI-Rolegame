package com.rolegame.server;

import com.rolegame.data.Message;
import com.rolegame.client.ClientInterface;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ChatInterface {
	private static final long serialVersionUID = 1L;

	List<ClientInterface> clientsList = new ArrayList<ClientInterface>();

	public Server() throws RemoteException {
		super(0);
	}

	@Override
	public synchronized void register(ClientInterface client) throws RemoteException {
		String information  = "New client joined with id " + client.getId();
		System.out.println(information);
		// make welcome message to new client, announce its present to other client
		client.receiveChatInformation("You joined the chat with id " + client.getId());
		for (ClientInterface otherClient : clientsList) {
			otherClient.receiveChatInformation(information);
		}
		clientsList.add(client);
	}

	@Override
	public synchronized void unregister(ClientInterface client) throws RemoteException {
		String information  = "Client left with id " + client.getId();
		System.out.println(information);
		client.receiveChatInformation("Goodbye!");
		// removal based on the client's id, see equal() method
		clientsList.remove(client);
		for (ClientInterface otherClient : clientsList) {
			otherClient.receiveChatInformation(information);
		}
	}

	@Override
	public synchronized void sendMessage(Message messsage) throws RemoteException {
		// broadcast message to everyone
		for (ClientInterface client : clientsList) {
			client.receiveMessage(messsage);
		}
	}

	public static void main(String args[]) throws Exception {
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
		}
		Server chatServer = new Server();
		Naming.rebind("//localhost/RmiServer", chatServer);
		System.out.println("Server ready!");
	}

}
