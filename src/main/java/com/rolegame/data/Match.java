package com.rolegame.data;

import java.io.Serializable;

import com.rolegame.client.ClientInterface;

public class Match implements Serializable {
    
    private ClientInterface[] clients;

    // just test
    private int counter = 0;

    // TODO references to client statistics?

    public Match(ClientInterface clientOne, ClientInterface clientTwo) {
        clients = new ClientInterface[] {clientOne, clientTwo};
    }

    public void makeLightAttack(String clientId) {
        counter--;
    }

    public int getCounter() {
        return counter;
    }
}
