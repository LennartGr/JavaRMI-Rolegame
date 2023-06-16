package com.rolegame.client;

import java.rmi.Remote;

import java.rmi.RemoteException;

import com.rolegame.data.Statistics;

public interface ClientInterface extends Remote {
    
    public void receiveInformation(String information) throws RemoteException;
    public String getId() throws RemoteException;
    public Statistics getStatistics() throws RemoteException;
    public void setStatistics(Statistics statistics) throws RemoteException;
}
