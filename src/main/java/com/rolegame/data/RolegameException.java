package com.rolegame.data;

/**
 * Custom exception class for Battleship game exceptions.
 */
public class RolegameException extends Exception {

    /**
     * Constructs a BattleshipException with the specified error message.
     *
     * @param message the error message
     */
    public RolegameException(String message) {
        super(message);
    }
}