package util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger userCounter = new AtomicInteger(1000);
    private static final AtomicInteger internshipCounter = new AtomicInteger(1000);
    private static final AtomicInteger applicationCounter = new AtomicInteger(1000);
    private static final AtomicInteger withdrawalCounter = new AtomicInteger(1000);

    public static String generateUserId(String prefix) {
        return prefix + userCounter.incrementAndGet();
    }

    public static String generateInternshipId() {
        return "INT" + internshipCounter.incrementAndGet();
    }

    public static String generateApplicationId() {
        return "APP" + applicationCounter.incrementAndGet();
    }

    public static String generateWithdrawalId() {
        return "WD" + withdrawalCounter.incrementAndGet();
    }
}
