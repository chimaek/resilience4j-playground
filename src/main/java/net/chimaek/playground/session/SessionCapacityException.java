package net.chimaek.playground.session;

public class SessionCapacityException extends RuntimeException {

    public SessionCapacityException() {
        super("session_capacity");
    }
}
