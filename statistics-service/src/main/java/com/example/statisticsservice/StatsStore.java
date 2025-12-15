package com.example.statisticsservice;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StatsStore {
    private final AtomicLong totalCustomers = new AtomicLong(0);
    private String lastEvent = "No events yet";

    public long incrementCustomers() {
        return totalCustomers.incrementAndGet();
    }

    public long getTotalCustomers() {
        return totalCustomers.get();
    }

    public void setLastEvent(String event) {
        this.lastEvent = event;
    }

    public String getLastEvent() {
        return lastEvent;
    }
}
