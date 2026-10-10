package net.chimaek.playground.session;

import net.chimaek.playground.chaos.ExternalApiMock;

import java.time.Duration;
import java.time.Instant;

public class Simulation {

    public static final Duration TTL = Duration.ofMinutes(30);

    private final String sessionId;
    private final ExternalApiMock mock;
    private volatile Instant lastAccess = Instant.now();

    public Simulation(String sessionId, ExternalApiMock mock) {
        this.sessionId = sessionId;
        this.mock = mock;
    }

    public String sessionId() {
        return sessionId;
    }

    public ExternalApiMock mock() {
        return mock;
    }

    public void touch() {
        lastAccess = Instant.now();
    }

    public boolean isExpired() {
        return lastAccess.isBefore(Instant.now().minus(TTL));
    }
}
