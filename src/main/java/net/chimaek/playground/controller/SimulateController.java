package net.chimaek.playground.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import net.chimaek.playground.api.CallTrace;
import net.chimaek.playground.api.PriceResponse;
import net.chimaek.playground.service.VeggieService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulateController {

    private final VeggieService veggieService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public SimulateController(VeggieService veggieService, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.veggieService = veggieService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostMapping("/api/simulate/veggie-price")
    public PriceResponse defended(@RequestParam String sessionId) {
        SessionIds.require(sessionId);
        long started = System.nanoTime();
        CallTrace trace = new CallTrace();
        int price = veggieService.price(sessionId, trace).join();
        return response(sessionId, trace, price, started);
    }

    @PostMapping("/api/simulate/veggie-price/naive")
    public PriceResponse naive(@RequestParam String sessionId) throws Exception {
        SessionIds.require(sessionId);
        long started = System.nanoTime();
        CallTrace trace = new CallTrace();
        int price = veggieService.naive(sessionId, trace);
        return response(sessionId, trace, price, started);
    }

    private PriceResponse response(String sessionId, CallTrace trace, int price, long started) {
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;
        String state = circuitBreakerRegistry.find(sessionId)
                .map(breaker -> breaker.getState().name())
                .orElse("CLOSED");
        return new PriceResponse(
                PriceResponse.SCHEMA_VERSION,
                trace.requestId(),
                price,
                trace.fallback(),
                trace.fallback() ? "FIXED" : null,
                elapsedMs,
                state,
                trace.attempts(),
                trace.upstreamCalls()
        );
    }
}
