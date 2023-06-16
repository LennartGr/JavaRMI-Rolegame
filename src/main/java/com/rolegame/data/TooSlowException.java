package com.rolegame.data;

public class TooSlowException extends Exception {

    /**
     * Constructs a BattleshipException with the specified error message.
     *
     * @param message the error message
     */
    public TooSlowException(String message) {
        super(message);
    }
}
