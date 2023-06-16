package com.rolegame.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.rolegame.client.ClientInterface;

public class Match extends UnicastRemoteObject implements MatchInterface {

    private int counter = 0;

    ClientInterface clientA;
    ClientInterface clientB;
    
    public Match() throws RemoteException {}

    @Override
    public void increase() throws RemoteException {
        counter++;
    }

    @Override
    public int getCounter() throws RemoteException {
        return counter;
    }

    @Override
    public boolean isReady() throws RemoteException {
        return (clientA != null && clientB != null);
    }

    @Override
    public boolean isStartingClient(String clientId) throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public void registerClient(ClientInterface client) throws RemoteException {
        if (clientA == null) {
            clientA = client;
        } else if (clientB == null) {
            clientB = client;
        }
    }

}
