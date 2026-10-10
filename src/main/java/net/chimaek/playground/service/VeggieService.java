package net.chimaek.playground.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import net.chimaek.playground.api.CallTrace;
import net.chimaek.playground.session.SimulationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class VeggieService {

    public static final String CONFIG = "veggie";
    public static final int FALLBACK_PRICE = 4500;

    private static final Logger log = LoggerFactory.getLogger(VeggieService.class);

    private final SimulationRegistry registry;

    public VeggieService(SimulationRegistry registry) {
        this.registry = registry;
    }

    @Retry(name = "#sessionId", configuration = CONFIG, fallbackMethod = "priceFallback")
    @CircuitBreaker(name = "#sessionId", configuration = CONFIG)
    @TimeLimiter(name = "#sessionId", configuration = CONFIG)
    @Bulkhead(name = "#sessionId", type = Bulkhead.Type.THREADPOOL, configuration = CONFIG)
    public CompletableFuture<Integer> price(String sessionId, CallTrace trace) {
        trace.markAttempt();
        try {
            int price = registry.getOrCreate(sessionId).mock().getPrice(trace);
            return CompletableFuture.completedFuture(price);
        } catch (Exception ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    public CompletableFuture<Integer> priceFallback(String sessionId, CallTrace trace, Exception ex) {
        trace.markFallback();
        if (ex instanceof net.chimaek.playground.chaos.UnknownVeggieException) {
            log.info("품절 폴백. sessionId={}", sessionId);
        } else {
            log.warn("장애 폴백. sessionId={} cause={}", sessionId, ex.toString());
        }
        return CompletableFuture.completedFuture(FALLBACK_PRICE);
    }

    public int naive(String sessionId, CallTrace trace) throws Exception {
        trace.markAttempt();
        return registry.getOrCreate(sessionId).mock().getPrice(trace);
    }
}
