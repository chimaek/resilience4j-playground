package net.chimaek.playground.chaos;

import net.chimaek.playground.api.CallTrace;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExternalApiMock {

    public static final int SUCCESS_PRICE = 5200;

    private final AtomicLong latencyMs = new AtomicLong();
    private final AtomicInteger errorRatePercent = new AtomicInteger();
    private final AtomicBoolean stockoutArmed = new AtomicBoolean();

    public void setLatency(long latencyMs) {
        if (latencyMs < 0) {
            throw new IllegalArgumentException("latencyMs는 0 이상이어야 합니다.");
        }
        this.latencyMs.set(latencyMs);
    }

    public void setErrorRate(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("errorRate는 0에서 100 사이여야 합니다.");
        }
        this.errorRatePercent.set(percent);
    }

    public void armStockout() {
        stockoutArmed.set(true);
    }

    public void reset() {
        latencyMs.set(0);
        errorRatePercent.set(0);
        stockoutArmed.set(false);
    }

    public long latencyMs() {
        return latencyMs.get();
    }

    public int errorRatePercent() {
        return errorRatePercent.get();
    }

    public boolean stockoutArmed() {
        return stockoutArmed.get();
    }

    public int getPrice(CallTrace trace) throws IOException {
        trace.markUpstream();
        long delay = latencyMs.get();
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException("지연 대기가 중단되었습니다.", ex);
            }
        }
        if (stockoutArmed.compareAndSet(true, false)) {
            throw new UnknownVeggieException();
        }
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll < errorRatePercent.get()) {
            throw new IOException("외부 시세 API 오류");
        }
        return SUCCESS_PRICE;
    }
}
