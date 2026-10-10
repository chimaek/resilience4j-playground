package net.chimaek.playground.controller;

import net.chimaek.playground.api.ChaosResponse;
import net.chimaek.playground.api.PriceResponse;
import net.chimaek.playground.chaos.ExternalApiMock;
import net.chimaek.playground.session.SimulationRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChaosController {

    private final SimulationRegistry registry;

    public ChaosController(SimulationRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/api/chaos/latency")
    public ChaosResponse latency(@RequestParam String sessionId, @RequestParam long ms) {
        SessionIds.require(sessionId);
        ExternalApiMock mock = registry.getOrCreate(sessionId).mock();
        mock.setLatency(ms);
        return view(sessionId, mock);
    }

    @PostMapping("/api/chaos/error-rate")
    public ChaosResponse errorRate(@RequestParam String sessionId, @RequestParam int percent) {
        SessionIds.require(sessionId);
        ExternalApiMock mock = registry.getOrCreate(sessionId).mock();
        mock.setErrorRate(percent);
        return view(sessionId, mock);
    }

    @PostMapping("/api/chaos/reset")
    public ChaosResponse reset(@RequestParam String sessionId) {
        SessionIds.require(sessionId);
        ExternalApiMock mock = registry.getOrCreate(sessionId).mock();
        mock.reset();
        return view(sessionId, mock);
    }

    @PostMapping("/api/chaos/stockout")
    public ChaosResponse stockout(@RequestParam String sessionId) {
        SessionIds.require(sessionId);
        ExternalApiMock mock = registry.getOrCreate(sessionId).mock();
        mock.armStockout();
        return view(sessionId, mock);
    }

    private ChaosResponse view(String sessionId, ExternalApiMock mock) {
        return new ChaosResponse(
                PriceResponse.SCHEMA_VERSION,
                sessionId,
                mock.latencyMs(),
                mock.errorRatePercent(),
                mock.stockoutArmed()
        );
    }
}
