package net.chimaek.playground.api;

public record ChaosResponse(
        int schemaVersion,
        String sessionId,
        long latencyMs,
        int errorRatePercent,
        boolean stockoutArmed
) {
}
