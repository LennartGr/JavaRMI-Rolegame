package com.rolegame.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.UUID;

import com.rolegame.data.Statistics;
import com.rolegame.remote.MatchInterface;
import com.rolegame.server.ServerInterface;

public class Client extends UnicastRemoteObject implements ClientInterface {
	private static final String EXIT_CODE = "exit";
	private static final String CMD_STATS = "stats";
	private static final String CMD_NEW = "new";
	private static final String CMD_FIGHT_PLAYER = "fightplayer";
	private static final String CMD_FIGHT_SERVER = "fightserver";

	private ServerInterface server;
	private String id;
	private Scanner scanner;
	private Statistics statistics;

	public Client() throws MalformedURLException, RemoteException, NotBoundException {
		super();
		server = (ServerInterface) Naming.lookup("//localhost/rolegame");
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

	public void createCharacter() throws RemoteException {
		String name;
		JansiHelper.print("Enter your name:");
		while (true) {
			name = scanner.nextLine();
			if (name.length() < 3 || name.length() > 10) {
				JansiHelper.printError("Player names must have between 3 and 10 characters.");
			} else if (!name.matches("[a-zA-Z]+")) {
				JansiHelper.printError("Player names must only consist of letters.");
			} else {
				// valid name
				break;
			}
		}
		statistics = server.getStatistics(id, name);
		JansiHelper.print("You have the following statistics:");
		JansiHelper.print(statistics.toString());
	}

	private void displayMenuInfo() {
		JansiHelper.print("Type " + JansiHelper.alert(CMD_STATS) + " to see your stats.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_NEW) + " to create a new character.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_FIGHT_PLAYER) + " to challenge a player.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_FIGHT_SERVER) + " to challenge the server.");
		JansiHelper.print("Type " + JansiHelper.colorize(EXIT_CODE, "red") + " to leave.");
	}

	public void run() throws RemoteException {
		String input = "";
		while (! input.equals(EXIT_CODE)) {
			displayMenuInfo();
			input = scanner.nextLine();
			switch (input) {
				case CMD_STATS:
					JansiHelper.print(statistics.toString());
					break;
				case CMD_NEW:
					createCharacter();
					break;
				case CMD_FIGHT_PLAYER:
					fightPlayer();
					break;
				case CMD_FIGHT_SERVER:
					fightServer();
					break;
				case EXIT_CODE:
					return;
				default: {
					JansiHelper.printError("Unrecognized command, try again.");
				}
			}
		}
	}

	public void fightPlayer() throws RemoteException {
		MatchInterface match = joinMatch();
		if (match == null)
			return;
		while (!match.isReady()){}
		JansiHelper.print("Match now ready");
		while (true) {
			String tmp = scanner.nextLine();
			match.increase();
			JansiHelper.print("" + match.getCounter());
		}
	}

	public void fightServer() {

	}

	public MatchInterface joinMatch() throws RemoteException {
		MatchInterface match  = server.startMatchAgainstPlayer(this);
		return match;
	}

	@Override
	public void receiveInformation(String information) throws RemoteException {
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

	@Override
	public Statistics getStatistics() throws RemoteException {
		return statistics;
	}

	@Override
	public void setStatistics(Statistics statistics) throws RemoteException {
		this.statistics = statistics;
	}

	public static void main(String args[]) throws Exception {
		Client chatClient = new Client();
		chatClient.createCharacter();
		chatClient.run();
		chatClient.close();
	}

}
