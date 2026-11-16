package org.firsova.task3.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Terminal;
import org.firsova.task3.entity.Van;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogisticsBase {
    private static final Logger LOGGER = LogManager.getLogger(LogisticsBase.class);
    private static final int TERMINAL_COUNT = 3;
    private static final int STORAGE_CAPACITY = 100;
    private static final int NORMAL_STOCK = STORAGE_CAPACITY / 2;
    private static final int LOW_STOCK = STORAGE_CAPACITY / 4;
    private static final int HIGH_STOCK = STORAGE_CAPACITY * 3 / 4;

    private static final Lock instanceLock = new ReentrantLock();
    private static final AtomicBoolean instanceCreated = new AtomicBoolean(false);
    private static LogisticsBase instance;

    private final Lock terminalLock = new ReentrantLock(true);
    private final Condition terminalReleased = terminalLock.newCondition();
    private final Deque<Terminal> freeTerminals = new ArrayDeque<>();
    private final AtomicInteger goodsInStock = new AtomicInteger(NORMAL_STOCK);
    private int waitingPerishableVans;

    private LogisticsBase() {
        for (int i = 1; i <= TERMINAL_COUNT; i++) {
            freeTerminals.add(new Terminal(i));
        }
    }

    public static LogisticsBase getInstance() {
        if (!instanceCreated.get()) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    instance = new LogisticsBase();
                    instanceCreated.set(true);
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }

    public Optional<Terminal> takeTerminal(Van van) {
        terminalLock.lock();
        if (van.isPerishable()) {
            waitingPerishableVans++;
        }
        try {
            while (freeTerminals.isEmpty() || (!van.isPerishable() && waitingPerishableVans > 0)) {
                terminalReleased.await();
            }
            Terminal terminal = freeTerminals.poll();
            LOGGER.info("Van {} drove to terminal {}", van.getVanId(), terminal.terminalId());
            return Optional.of(terminal);
        } catch (InterruptedException e) {
            LOGGER.warn("Van {} stopped waiting for a terminal", van.getVanId(), e);
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            if (van.isPerishable()) {
                waitingPerishableVans--;
                terminalReleased.signalAll();
            }
            terminalLock.unlock();
        }
    }

    public void releaseTerminal(Terminal terminal) {
        terminalLock.lock();
        try {
            freeTerminals.add(terminal);
            terminalReleased.signalAll();
        } finally {
            terminalLock.unlock();
        }
    }

    public boolean transferGoods(Van van) {
        int amount = van.getCargoAmount();
        int current;
        int updated;
        do {
            current = goodsInStock.get();
            updated = van.getOperation() == CargoOperation.LOAD ? current - amount : current + amount;
            if (updated < 0 || updated > STORAGE_CAPACITY) {
                return false;
            }
        } while (!goodsInStock.compareAndSet(current, updated));
        LOGGER.info("Van {} {} {} goods, goods in stock: {}", van.getVanId(), van.getOperation(), amount, updated);
        return true;
    }

    public int balanceStock() {
        int current;
        do {
            current = goodsInStock.get();
            if (current >= LOW_STOCK && current <= HIGH_STOCK) {
                return 0;
            }
        } while (!goodsInStock.compareAndSet(current, NORMAL_STOCK));
        return NORMAL_STOCK - current;
    }

    public int getGoodsInStock() {
        return goodsInStock.get();
    }
}
