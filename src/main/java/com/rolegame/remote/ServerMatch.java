package com.rolegame.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;

import com.rolegame.client.ClientInterface;
import com.rolegame.data.RolegameException;
import com.rolegame.data.Statistics;

public class ServerMatch extends UnicastRemoteObject implements MatchInterface {

    private static final String SERVER_NAME = "Robot";

    private ClientInterface client;
    private Statistics serverStats;

    public ServerMatch() throws RemoteException {
    }

    @Override
    public boolean isReady() throws RemoteException {
        return true;
    }

    @Override
    public boolean isActiveClient(String clientId) throws RemoteException {
        return true;
    }

    @Override
    public void registerClient(ClientInterface client) throws RemoteException {
        this.client = client;
        Statistics clientStats = client.getStatistics();
        final int serverLevel = clientStats.getLevel();
        final int serverMaxLive = clientStats.getMaxLive() + getRandomOffset();
        final int serverMaxEndurance = clientStats.getMaxEndurance() + getRandomOffset();
        final int serverProtection = clientStats.getProtection() + getRandomOffset();
        final int serverPower = clientStats.getPower() + getRandomOffset();
        final int serverStamina = clientStats.getStamina() + getRandomOffset();
        // client may always attack first
        final int serverSpeed = clientStats.getSpeed() - 1;
        this.serverStats = new Statistics(SERVER_NAME,
                serverLevel,
                serverMaxLive,
                serverMaxLive,
                serverMaxEndurance,
                serverMaxEndurance,
                serverProtection,
                serverPower,
                serverStamina,
                serverSpeed);
    }

    private int getRandomOffset() {
        return ThreadLocalRandom.current().nextInt(-2, 3);
    }

    @Override
    public void makeAttack(String clientId, boolean heavy) throws RemoteException, RolegameException {
        final int heavyAttackCost = 5;
        Statistics clientStats = client.getStatistics();
        if (heavy && clientStats.getActiveEndurance() < heavyAttackCost) {
            throw new RolegameException(
                    "heavy attack costs " + heavyAttackCost + ", this exceeds your active endurance.");
        }
        if (heavy) {
            clientStats.setActiveEndurance(clientStats.getActiveEndurance() - heavyAttackCost);
        }
        double powerCoefficient = heavy ? 1.5 : 1;
        serverStats.setActiveLive(serverStats.getActiveLive() + serverStats.getProtection()
                - (int) (powerCoefficient * clientStats.getPower()));
        if (!heavy) {
            int attackerNewEndurance = clientStats.getActiveEndurance() + (int) (clientStats.getStamina()
                    * clientStats.getActiveEndurance() / clientStats.getMaxEndurance());
            clientStats.setActiveEndurance(Math.min(clientStats.getMaxEndurance(), attackerNewEndurance));
        }

        client.setStatistics(clientStats);
        // killed the server, no need to continue?
        if (serverStats.getActiveLive() <= 0) return;

        // server makes light attack (always)
        clientStats.setActiveLive(clientStats.getActiveLive() + clientStats.getProtection() - serverStats.getPower());
        client.setStatistics(clientStats);
        client.receiveInformation("Server opponent might light attack.");
    }

    @Override
    public String getWinningClient() throws RemoteException {
        if (client.getStatistics().getActiveLive() <= 0) {
            return SERVER_NAME;
        } else if (serverStats.getActiveLive() <= 0) {
            // assume this method is only called once with this outcome:
            // update the client stats
            upgradeClientStats();
            return client.getId();
        } else {
            return null;
        }
    }

    private void upgradeClientStats() throws RemoteException {
        final int step = 2;
        Statistics stats = client.getStatistics();
        stats.setLevel(stats.getLevel() + 1);
        int powerUpgrade = ThreadLocalRandom.current().nextInt(0, 4);
        switch (powerUpgrade) {
            case 0:
                stats.setProtection(stats.getProtection() + step);
                break;
            case 1:
                stats.setPower(stats.getPower() + step);
                break;
            case 2: 
                stats.setStamina(stats.getStamina() + step);
                break;
            case 3:
                stats.setSpeed(stats.getSpeed() + step);
                break;
        }
        client.setStatistics(stats);
    }

    @Override
    public Statistics getOpponenStatistics(String clientId) throws RemoteException {
        return serverStats;
    }

}
