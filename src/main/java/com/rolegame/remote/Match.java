package com.rolegame.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import com.rolegame.client.ClientInterface;
import com.rolegame.data.RolegameException;
import com.rolegame.data.Statistics;

public class Match extends UnicastRemoteObject implements MatchInterface {

    private ClientInterface clientA;
    private ClientInterface clientB;
    private String activeClientId;
    private Random random = new Random();

    public Match() throws RemoteException {
    }

    @Override
    public boolean isReady() throws RemoteException {
        return (clientA != null && clientB != null);
    }

    @Override
    public boolean isActiveClient(String clientId) throws RemoteException {
        return activeClientId.equals(clientId);
    }

    private void switchActiveClient() throws RemoteException {
        activeClientId = (clientA.getId().equals(activeClientId)) ? clientB.getId() : clientA.getId();
    }

    @Override
    public void registerClient(ClientInterface client) throws RemoteException {
        if (clientA == null) {
            clientA = client;
        } else if (clientB == null) {
            clientB = client;
        }
        // match ready: determine who may start
        if (this.isReady()) {
            final int speedA = clientA.getStatistics().getSpeed();
            final int speedB = clientB.getStatistics().getSpeed();
            if (speedA > speedB) {
                activeClientId = clientA.getId();
            } else if (speedA == speedB) {
                // random choice
                activeClientId = random.nextBoolean() ? clientA.getId() : clientB.getId();
            } else {
                activeClientId = clientB.getId();
            }
        }
    }

    private ClientInterface getClientWithId(String id) throws RemoteException {
        if (clientA.getId().equals(id)) {
            return clientA;
        } else if (clientB.getId().equals(id)) {
            return clientB;
        }
        return null;
    }

    private ClientInterface getOther(ClientInterface client) {
        if (client.equals(clientA)) {
            return clientB;
        }
        return clientA;
    }

    @Override
    public void makeAttack(String clientId, boolean heavy) throws RemoteException, RolegameException {
        final int heavyAttackCost = 5;
        ClientInterface attacker = getClientWithId(clientId);
        ClientInterface defender = getOther(attacker);
        Statistics attackerStats = attacker.getStatistics();
        Statistics defenderStats = defender.getStatistics();
        if (heavy && attackerStats.getActiveEndurance() < heavyAttackCost) {
            throw new RolegameException(
                    "heavy attack costs " + heavyAttackCost + ", this exceeds your active endurance.");
        }
        if (heavy) {
            attackerStats.setActiveEndurance(attackerStats.getActiveEndurance() - heavyAttackCost);
        }
        double powerCoefficient = heavy ? 1.5 : 1;
        defenderStats.setActiveLive(defenderStats.getActiveLive() + defenderStats.getProtection()
                - (int) (powerCoefficient * attackerStats.getPower()));
        if (!heavy) {
            int attackerNewEndurance = attackerStats.getActiveEndurance() + (int) (attackerStats.getStamina()
                    * attackerStats.getActiveEndurance() / attackerStats.getMaxEndurance());
            attackerStats.setActiveEndurance(Math.min(attackerStats.getMaxEndurance(), attackerNewEndurance));
        }
        attacker.setStatistics(attackerStats);
        defender.setStatistics(defenderStats);

        final String attackType = heavy ? "heavy" : "light";
        defender.receiveInformation("Your opponent made a " + attackType + " attack.");
        switchActiveClient();
        // TODO start timer for the active client
    }

    @Override
    public String getWinningClient() throws RemoteException {
        if (clientA.getStatistics().getActiveLive() <= 0) {
            return clientB.getId();
        } else if (clientB.getStatistics().getActiveLive() <= 0) {
            return clientA.getId();
        } else {
            return null;
        }
    }

    @Override
    public Statistics getOpponenStatistics(String clientId) throws RemoteException {
        return getOther(getClientWithId(clientId)).getStatistics();
    }

}
