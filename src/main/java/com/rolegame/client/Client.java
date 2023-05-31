package com.rolegame.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.UUID;

import com.rolegame.server.ChatInterface;

public class Client extends UnicastRemoteObject implements ClientInterface {
	public static final String EXIT_CODE = "exit";

	public ChatInterface server;
	public String id;
	public Scanner scanner;

	public Client() throws MalformedURLException, RemoteException, NotBoundException {
		super();
		server = (ChatInterface) Naming.lookup("//localhost/rolegame");
		id = UUID.randomUUID().toString();
		server.register(this);
		scanner = new Scanner(System.in);
	}

	public void close() throws RemoteException {
		scanner.close();
		server.unregister(this);
		// this line is necessary to terminate the process
		UnicastRemoteObject.unexportObject(this, true);
	}

	@Override
	public void receiveChatInformation(String information) throws RemoteException {
		System.out.println("[" + information + "]");
	}

	@Override
	public String getId() throws RemoteException {
		return this.id;
	}

	// two clients are equal if their id is equal
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Client)) {
			return false;
		}
		Client other = (Client) obj;
		return this.id.equals(other.id);
	}

	public static void main(String args[]) throws Exception {
		Client chatClient = new Client();
	}

}
