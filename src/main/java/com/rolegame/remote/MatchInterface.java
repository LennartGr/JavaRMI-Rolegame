package com.rolegame.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rolegame.client.ClientInterface;
import com.rolegame.data.RolegameException;
import com.rolegame.data.Statistics;
import com.rolegame.data.TooSlowException;

public interface MatchInterface extends Remote {

    public boolean isStarted() throws RemoteException;

    public boolean isActiveClient(String clientId) throws RemoteException;

    public void registerClient(ClientInterface client) throws RemoteException;

    public void makeAttack(String clientId, boolean heavy) throws RemoteException, RolegameException, TooSlowException;

    public String getWinningClient() throws RemoteException;

    public Statistics getOpponenStatistics(String clientId) throws RemoteException;

    public boolean usesAttackTimer() throws RemoteException;

    public double getAttackerTimeLeft() throws RemoteException;

}
