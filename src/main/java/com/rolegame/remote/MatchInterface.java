package com.rolegame.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatchInterface extends Remote {
    
    public void increase() throws RemoteException;

    public int getCounter() throws RemoteException;
}
