package com.rolegame.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rolegame.data.Statistics;
import com.rolegame.remote.MatchInterface;
import com.rolegame.client.ClientInterface;

public interface ServerInterface extends Remote {

    // TODO register and unregister only necessary if we want to use callbacks
    public void register(ClientInterface client) throws RemoteException;
	public void unregister(ClientInterface client) throws RemoteException;
	
    public Statistics getStatistics(String clientId, String clientName) throws RemoteException; 
    public void startMatchAgainstServer(String clientId) throws RemoteException;
    public MatchInterface startMatchAgainstPlayer(ClientInterface client) throws RemoteException;

}
