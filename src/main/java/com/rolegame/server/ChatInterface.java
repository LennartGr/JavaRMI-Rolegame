package com.rolegame.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rolegame.data.Message;
import com.rolegame.client.ClientInterface;

public interface ChatInterface extends Remote {

	public void register(ClientInterface client) throws RemoteException;
	public void unregister(ClientInterface client) throws RemoteException;
	public void sendMessage(Message message) throws RemoteException;
}
