package com.rolegame.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rolegame.client.ClientInterface;

public interface MatchInterface extends Remote {
    
    public void increase() throws RemoteException;

    public int getCounter() throws RemoteException;

    public boolean isReady() throws RemoteException;

    public boolean isStartingClient(String clientId) throws RemoteException;

    public void registerClient(ClientInterface client) throws RemoteException;
}
