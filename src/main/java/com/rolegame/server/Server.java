package com.rolegame.server;

import com.rolegame.data.Statistics;
import com.rolegame.remote.Match;
import com.rolegame.remote.MatchInterface;
import com.rolegame.client.ClientInterface;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;

	private List<ClientInterface> clientsList = new ArrayList<ClientInterface>();

	private MatchInterface lastMatch;

	public Server() throws RemoteException {
		super(0);
	}

	@Override
	public synchronized void register(ClientInterface client) throws RemoteException {
		String information = "New client joined with id " + client.getId();
		System.out.println(information);
		// make welcome message to new client, announce its present to other client
		client.receiveInformation("You joined the role game world with id " + client.getId());
		for (ClientInterface otherClient : clientsList) {
			otherClient.receiveInformation(information);
		}
		clientsList.add(client);
	}

	@Override
	public synchronized void unregister(ClientInterface client) throws RemoteException {
		String information = "Client left with id " + client.getId();
		System.out.println(information);
		client.receiveInformation("Goodbye!");
		// removal based on the client's id, see equal() method
		clientsList.remove(client);
		for (ClientInterface otherClient : clientsList) {
			otherClient.receiveInformation(information);
		}
	}

	public static void main(String args[]) throws Exception {
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
		}
		Server chatServer = new Server();
		Naming.rebind("//localhost/rolegame", chatServer);
		System.out.println("Server ready!");
	}

	@Override
	public Statistics getStatistics(String clientId, String clientName) throws RemoteException {
		final int maxLive = 10;
		final int maxEndurance = 10;
		final int protection = 5;
		final int power = 5;
		final int stamina = 5;
		final int speed = 5;
		Statistics stats = new Statistics(clientName, 1, maxLive, maxLive, maxEndurance, maxEndurance, protection,
				power, stamina, speed);
		return stats;
	}

	@Override
	public void startMatchAgainstServer(String clientId) throws RemoteException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'startMatchAgainstServer'");
	}

	@Override
	public MatchInterface startMatchAgainstPlayer(ClientInterface client) throws RemoteException {
		if (lastMatch == null || lastMatch.isReady()) {
			lastMatch = new Match();
		} 
		lastMatch.registerClient(client);
		return lastMatch;
	}

}
