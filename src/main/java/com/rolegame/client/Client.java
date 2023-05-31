package com.rolegame.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.UUID;

import com.rolegame.data.Message;
import com.rolegame.server.ChatInterface;

public class Client extends UnicastRemoteObject implements ClientInterface {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";

	public static final String EXIT_CODE = "exit";

	public ChatInterface server;
	public String id;

	public Client() throws MalformedURLException, RemoteException, NotBoundException {
		super();
		server = (ChatInterface) Naming.lookup("//localhost/RmiServer");
		id = UUID.randomUUID().toString();
	}

	@Override
	public void receiveMessage(Message message) throws RemoteException {
		if (!message.getSenderId().equals(this.id)) {
			System.out.println(ANSI_BLUE + "[" + message.getContent() + "]" + ANSI_RESET);
		}
	}

	@Override
	public void receiveChatInformation(String information) throws RemoteException {
		System.out.println(ANSI_GREEN + "[" + information + "]" + ANSI_RESET);
	}

	@Override
	public String getId() throws RemoteException {
		return this.id;
	}

	public void startChatting() throws RemoteException {
		// tell server we want to participate in chat
		server.register(this);
		// run polling thread in background
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String out = String.format("Enter message to everyone (type \"%s\" to exit)", EXIT_CODE);
			System.out.println(out);
			String input = scanner.nextLine();
			if (input.equals(EXIT_CODE)) {
				break;
			}
			this.server.sendMessage(new Message(this.id, input));
		}
		// exit procedure
		scanner.close();
		server.unregister(this);
		// this line is necessary to terminate the process
		UnicastRemoteObject.unexportObject(this, true);
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
		chatClient.startChatting();
	}

}
