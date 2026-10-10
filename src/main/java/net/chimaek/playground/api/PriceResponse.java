package net.chimaek.playground.api;

public record PriceResponse(
        int schemaVersion,
        String requestId,
        int price,
        boolean fallback,
        String fallbackStrategy,
        long elapsedMs,
        String circuitState,
        int attempts,
        int upstreamCalls
) {
    public static final int SCHEMA_VERSION = 1;
}
