package com.rolegame.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Match extends UnicastRemoteObject implements MatchInterface {

    private int counter = 0;
    
    public Match() throws RemoteException {}

    @Override
    public void increase() throws RemoteException {
        counter++;
    }

    @Override
    public int getCounter() throws RemoteException {
        return counter;
    }

}
