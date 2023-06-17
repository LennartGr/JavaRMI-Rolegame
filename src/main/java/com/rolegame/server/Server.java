package com.rolegame.server;

import com.rolegame.data.Statistics;
import com.rolegame.remote.Match;
import com.rolegame.remote.MatchInterface;
import com.rolegame.remote.ServerMatch;
import com.rolegame.client.ClientInterface;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;

	private List<ClientInterface> clientsList = new ArrayList<ClientInterface>();
	private Map<ClientInterface, Statistics> clientStatisticsMap = new HashMap<>();

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
		clientsList.add(client);
	}

	@Override
	public synchronized void unregister(ClientInterface client) throws RemoteException {
		String information = "Client left with id " + client.getId();
		System.out.println(information);
		client.receiveInformation("Goodbye!");
		// removal based on the client's id, see equal() method
		clientsList.remove(client);
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
	public Statistics createNewStatistics(String clientId, String clientName) throws RemoteException {
		final int maxLive = 10;
		final int maxEndurance = 10;
		final int protection = 3;
		final int power = 5;
		final int stamina = 5;
		final int speed = 5;
		Statistics stats = new Statistics(clientName, 1, maxLive, maxLive, maxEndurance, maxEndurance, protection,
				power, stamina, speed);
		return stats;
	}

	@Override
	public MatchInterface startMatchAgainstServer(ClientInterface client) throws RemoteException {
		MatchInterface serverMatch = new ServerMatch();
		serverMatch.registerClient(client);
		return serverMatch;
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
