package com.rolegame.client;

import java.rmi.Remote;

import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    
    public void receiveChatInformation(String information) throws RemoteException;
    public String getId() throws RemoteException;
}
