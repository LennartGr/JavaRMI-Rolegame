package com.rolegame.client;

import java.rmi.Remote;

import java.rmi.RemoteException;

import com.rolegame.data.Message;

public interface ClientInterface extends Remote {
    public void receiveMessage(Message message) throws RemoteException;
    public void receiveChatInformation(String information) throws RemoteException;
    public String getId() throws RemoteException;
}
