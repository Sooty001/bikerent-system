package com.example.statisticsservice;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

@Component
public class StatsStore {
    private final AtomicLong totalCustomers = new AtomicLong(0);
    private final AtomicLong totalRentals = new AtomicLong(0);
    private final AtomicLong totalBookings = new AtomicLong(0);
    private final AtomicLong activeBicycles = new AtomicLong(0);

    private final DoubleAdder totalRevenue = new DoubleAdder();

    public void incrementCustomers() {
        totalCustomers.incrementAndGet();
    }

    public void incrementBookings() {
        totalBookings.incrementAndGet();
    }

    public void decrementBicycles() {
        activeBicycles.decrementAndGet();
    }

    public void recordRental(double price) {
        totalRentals.incrementAndGet();
        totalRevenue.add(price);
    }

    public long getTotalCustomers() { return totalCustomers.get(); }
    public long getTotalRentals() { return totalRentals.get(); }
    public double getTotalRevenue() { return totalRevenue.sum(); }

    public long getTotalBookings() { return totalBookings.get(); }
    public long getActiveBicycles() { return activeBicycles.get(); }

    public double getAverageCheck() {
        long rentals = totalRentals.get();
        if (rentals == 0) return 0.0;
        double avg = totalRevenue.sum() / rentals;
        return Math.round(avg * 100.0) / 100.0;
    }
}