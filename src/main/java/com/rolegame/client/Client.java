package com.rolegame.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.UUID;

import com.rolegame.data.RolegameException;
import com.rolegame.data.Statistics;
import com.rolegame.data.TooSlowException;
import com.rolegame.remote.MatchInterface;
import com.rolegame.server.ServerInterface;

public class Client extends UnicastRemoteObject implements ClientInterface {
	private static final String EXIT_CODE = "exit";
	private static final String CMD_STATS = "stats";
	private static final String CMD_NEW = "new";
	private static final String CMD_FIGHT_PLAYER = "fightplayer";
	private static final String CMD_FIGHT_SERVER = "fightserver";

	// commands match
	private static final String CMD_OTHER_STATS = "statsother";
	private static final String CMD_LIGHT_ATTACK = "light";
	private static final String CMD_HEAVY_ATTACK = "heavy";

	private static final String MSG_WIN = "You won!";
	private static final String MSG_LOSE = "You lost!";

	private static final String ERR_UNKNOWN_CMD = "Unrecognized command, try again.";
	private static final String ERR_SERVER_DISCONNECT = "No connection to server available.";
	private static final String ERR_CONNECTION_MATCH = "Connection to server lost or disconnect of opponent. Match ended unexpectedly.";

	private ServerInterface server;
	private String id;
	private Scanner scanner;
	private Statistics statistics;

	// used to control timer thread
	private boolean timerThreadActive = false;

	public Client() throws MalformedURLException, RemoteException, NotBoundException {
		super();
		server = (ServerInterface) Naming.lookup("//localhost/rolegame");
		id = UUID.randomUUID().toString();
		server.register(this);
		scanner = new Scanner(System.in);
	}

	private void close() throws RemoteException {
		scanner.close();
		server.unregister(this);
		// this line is necessary to terminate the process
		UnicastRemoteObject.unexportObject(this, true);
	}

	private void createCharacter() {
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
		try {
			statistics = server.createNewStatistics(id, name);
			JansiHelper.print("You have the following statistics:");
			JansiHelper.print(statistics.toString());
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_SERVER_DISCONNECT);
		}
	}

	private void displayMenuInfo() {
		JansiHelper.print("Type " + JansiHelper.alert(CMD_STATS) + " to see your stats.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_NEW) + " to create a new character.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_FIGHT_PLAYER) + " to challenge a player.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_FIGHT_SERVER) + " to challenge the server.");
		JansiHelper.print("Type " + JansiHelper.colorize(EXIT_CODE, "red") + " to leave.");
	}

	private void displayMatchMenuInfo() {
		JansiHelper.print("Type " + JansiHelper.alert(CMD_STATS) + " to see your stats.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_OTHER_STATS) + " to see your opponents stats.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_LIGHT_ATTACK) + " to make a light attack.");
		JansiHelper.print("Type " + JansiHelper.alert(CMD_HEAVY_ATTACK) + " to make a heavy attack.");
	}

	public void run() {
		String input = "";
		while (!input.equals(EXIT_CODE)) {
			displayMenuInfo();
			input = scanner.nextLine();
			switch (input) {
				case CMD_STATS:
					showStatistics();
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
					JansiHelper.printError(ERR_UNKNOWN_CMD);
				}
			}
		}
	}

	private void showStatistics() {
		try {
			// attempt fetch from server
			this.getStatistics();
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_SERVER_DISCONNECT);
			JansiHelper.print("Your last knows statistics are:");
		}
		JansiHelper.print(statistics.toString());
	}

	// fight another player
	private void fightPlayer() {
		try {
			MatchInterface match = server.startMatchAgainstPlayer(this);
			fightGeneral(match);
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_SERVER_DISCONNECT);
		}

	}

	private void fightServer() {
		try {
			MatchInterface match = server.startMatchAgainstServer(this);
			fightGeneral(match);
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_SERVER_DISCONNECT);
		}
	}

	// fight a match in general, no matter whether against server or client
	private void fightGeneral(MatchInterface match) {
		try {
			JansiHelper.print("Waiting for the match to start...");
			if (match == null)
				return;
			while (!match.isStarted()) {
			}
			JansiHelper.print(JansiHelper.alert("Match now ready"));

			// to ensure console is not flooded with wainting messages
			boolean waitingDisplayed = false;
			while (true) {
				String winningClient = match.getWinningClient();
				// TODO display winning because of timeout?
				if (winningClient != null && winningClient.equals(id)) {
					JansiHelper.print(JansiHelper.alert(MSG_WIN));
					break;
				} else if (winningClient != null) {
					JansiHelper.print(JansiHelper.alert(MSG_LOSE));
					break;
				}
				if (match.isActiveClient(this.id)) {
					JansiHelper.print("It is your turn!");
					// start the timer thread if necessary
					timerThreadActive = true;
					if (match.usesAttackTimer()) {
						TimerThread timer = new TimerThread(match);
						// note to self: call start NOT run
						timer.start();
					}
					makeMatchChoice(match);
					// returning from match choice: timer can be stopped
					timerThreadActive = false;
					waitingDisplayed = false;
				} else if (!waitingDisplayed) {
					JansiHelper.print("Waiting for the other player to attack...");
					waitingDisplayed = true;
				}
			}
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_CONNECTION_MATCH);
		}
		// even if match ended unexpectedly, need to reset active live and endurance
		try {
			this.statistics = server.resetClientsActiveLiveAndEndurance(this.id);
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_SERVER_DISCONNECT);
		}
		timerThreadActive = false;
	}

	private void makeMatchChoice(MatchInterface match) throws RemoteException {
		while (true) {
			displayMatchMenuInfo();
			String input = scanner.nextLine();
			switch (input) {
				case CMD_STATS:
					JansiHelper.print(statistics.toString());
					break;
				case CMD_OTHER_STATS:
					JansiHelper.print(match.getOpponenStatistics(id).toString());
					break;
				case CMD_LIGHT_ATTACK:
					try {
						match.makeAttack(id, false);
					} catch (RolegameException e) {
						// never thrown
					} catch (TooSlowException e) {
						JansiHelper.printError(e.getMessage());
						return;
					}
					return;
				case CMD_HEAVY_ATTACK:
					try {
						match.makeAttack(id, true);
						return;
					} catch (RolegameException e) {
						JansiHelper.printError(e.getMessage());
					} catch (TooSlowException e) {
						JansiHelper.printError(e.getMessage());
						return;
					}
					break;
				default:
					JansiHelper.printError(ERR_UNKNOWN_CMD);
			}
		}
	}

	@Override
	public void receiveInformation(String information) throws RemoteException {
		JansiHelper.print("[" + JansiHelper.colorize(information, "yellow") + "]");
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
		Statistics newStats = server.getClientStatistics(this.id);
		statistics = newStats;
		return newStats;
	}

	@Override
	public void setStatistics(Statistics statistics) throws RemoteException {
		this.statistics = statistics;
		server.setClientStatistics(this.id, statistics);
	}

	public static void main(String args[]) throws Exception {
		try {
			Client chatClient = new Client();
			chatClient.createCharacter();
			chatClient.run();
			chatClient.close();
		} catch (RemoteException e) {
			JansiHelper.printError(ERR_SERVER_DISCONNECT);
		}
	}

	class TimerThread extends Thread {

		// all in millis
		private static final int[] WARNINGS_TIME_LEFT = { 10, 5, 3 };

		private MatchInterface match;

		public TimerThread(MatchInterface match) {
			super();
			this.match = match;
		}

		public void run() {
			int warningIndex = 0;
			while (timerThreadActive) {
				boolean warningMissing = warningIndex < WARNINGS_TIME_LEFT.length;
				try {
					if (warningMissing && (WARNINGS_TIME_LEFT[warningIndex] * 1000) > match.getAttackerTimeLeft()) {
						// new warning triggered
						String warning = "Only " + WARNINGS_TIME_LEFT[warningIndex++] + " seconds left to attack...";
						JansiHelper.print(JansiHelper.colorize(warning, "yellow"));
					}
				} catch (RemoteException e) {
					return;
				}
			}
		}
	}

}
