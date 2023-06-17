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

	// private List<ClientInterface> clientsList = new ArrayList<ClientInterface>();
	// private Map<ClientInterface, Statistics> clientStatisticsMap = new HashMap<>();

	private Map<String, ClientInterface> clientsMap = new HashMap<>();
	private Map<String, Statistics> statisticsMap = new HashMap<>();

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
		clientsMap.put(client.getId(), client);
	}

	@Override
	public synchronized void unregister(ClientInterface client) throws RemoteException {
		String information = "Client left with id " + client.getId();
		System.out.println(information);
		client.receiveInformation("Goodbye!");
		clientsMap.remove(client.getId());
		statisticsMap.remove(client.getId());
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
		// update statistics map
		statisticsMap.put(clientId, stats);
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
		if (lastMatch == null || lastMatch.isStarted()) {
			lastMatch = new Match();
		} 
		// if client cannot be properly registered on the match, there is something broken with it and we need to create a new match
		try {
			lastMatch.registerClient(client);
		} catch (Exception e) {
			// register the client on an empty match
			lastMatch = new Match();
			lastMatch.registerClient(client);
		}
		return lastMatch;
	}

	@Override
	public Statistics getClientStatistics(String clientId) throws RemoteException {
		return statisticsMap.get(clientId);
	}

	@Override
	public void setClientStatistics(String clientId, Statistics statistics) throws RemoteException {
		statisticsMap.put(clientId, statistics);
	}

	@Override
	public Statistics resetClientsActiveLiveAndEndurance(String clientId) throws RemoteException {
		Statistics stats = statisticsMap.get(clientId);
		stats.setActiveEndurance(stats.getMaxEndurance());
		stats.setActiveLive(stats.getMaxLive());
		return stats;
	}

	

}
