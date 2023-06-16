package com.rolegame.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rolegame.client.ClientInterface;
import com.rolegame.data.RolegameException;
import com.rolegame.data.Statistics;

public interface MatchInterface extends Remote {
    
    // TODO del
    public void increase() throws RemoteException;

    // TODO del
    public int getCounter() throws RemoteException;

    public boolean isReady() throws RemoteException;

    public boolean isActiveClient(String clientId) throws RemoteException;

    public void registerClient(ClientInterface client) throws RemoteException;

    public void makeAttack(String clientId, boolean heavy) throws RemoteException, RolegameException;

    public String getWinningClient() throws RemoteException;

    public Statistics getOpponenStatistics(String clientId) throws RemoteException;

}
