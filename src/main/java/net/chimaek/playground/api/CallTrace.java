package net.chimaek.playground.api;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class CallTrace {

    private final String requestId = UUID.randomUUID().toString().substring(0, 8);
    private final AtomicInteger attempts = new AtomicInteger();
    private final AtomicInteger upstreamCalls = new AtomicInteger();
    private final AtomicBoolean fallback = new AtomicBoolean();

    public String requestId() {
        return requestId;
    }

    public void markAttempt() {
        attempts.incrementAndGet();
    }

    public void markUpstream() {
        upstreamCalls.incrementAndGet();
    }

    public void markFallback() {
        fallback.set(true);
        attempts.compareAndSet(0, 1);
    }

    public int attempts() {
        return attempts.get();
    }

    public int upstreamCalls() {
        return upstreamCalls.get();
    }

    public boolean fallback() {
        return fallback.get();
    }
}
