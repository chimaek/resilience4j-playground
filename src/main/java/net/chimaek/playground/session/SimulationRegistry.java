package net.chimaek.playground.session;

import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import net.chimaek.playground.chaos.ExternalApiMock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimulationRegistry {

    public static final int MAX_SESSIONS = 200;

    private final Map<String, Simulation> sessions = new ConcurrentHashMap<>();
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;

    public SimulationRegistry(
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry,
            TimeLimiterRegistry timeLimiterRegistry,
            ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry
    ) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.timeLimiterRegistry = timeLimiterRegistry;
        this.threadPoolBulkheadRegistry = threadPoolBulkheadRegistry;
    }

    public Simulation getOrCreate(String sessionId) {
        Simulation existing = sessions.get(sessionId);
        if (existing != null) {
            existing.touch();
            return existing;
        }
        synchronized (sessions) {
            Simulation current = sessions.get(sessionId);
            if (current != null) {
                current.touch();
                return current;
            }
            if (sessions.size() >= MAX_SESSIONS) {
                throw new SessionCapacityException();
            }
            Simulation created = new Simulation(sessionId, new ExternalApiMock());
            sessions.put(sessionId, created);
            return created;
        }
    }

    @Scheduled(fixedRate = 300_000)
    public void evictExpired() {
        sessions.entrySet().removeIf(entry -> {
            if (!entry.getValue().isExpired()) {
                return false;
            }
            String name = entry.getKey();
            circuitBreakerRegistry.remove(name);
            retryRegistry.remove(name);
            timeLimiterRegistry.remove(name);
            threadPoolBulkheadRegistry.remove(name);
            return true;
        });
    }
}
