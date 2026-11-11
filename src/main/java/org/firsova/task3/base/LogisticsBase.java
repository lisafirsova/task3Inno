package org.firsova.task3.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Terminal;
import org.firsova.task3.entity.Van;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogisticsBase {
    private static final Logger LOGGER = LogManager.getLogger(LogisticsBase.class);
    private static final int TERRITORY_CAPACITY = 3;
    private static final int TERMINAL_COUNT = 2;
    private static final int STORAGE_CAPACITY = 100;
    private static final int INITIAL_GOODS = 50;

    private static final Lock instanceLock = new ReentrantLock();
    private static final AtomicBoolean instanceCreated = new AtomicBoolean(false);
    private static LogisticsBase instance;

    private final Lock lock = new ReentrantLock(true);
    private final Condition baseChanged = lock.newCondition();
    private final Deque<Terminal> freeTerminals = new ArrayDeque<>();
    private final Deque<Van> waitingPerishableVans = new ArrayDeque<>();
    private int vansOnTerritory;
    private int goodsInStock = INITIAL_GOODS;
    private int reservedForLoading;
    private int reservedForUnloading;

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

    public void enterTerritory(Van van) throws InterruptedException {
        lock.lock();
        try {
            if (van.isPerishable()) {
                waitingPerishableVans.add(van);
            }
            try {
                if (!canEnter(van)) {
                    LOGGER.info("Van {} waits in the queue outside the base", van.getVanId());
                }
                while (!canEnter(van)) {
                    baseChanged.await();
                }
            } finally {
                waitingPerishableVans.remove(van);
            }
            vansOnTerritory++;
            reserveStorage(van);
            LOGGER.info("Van {} entered the territory, vans on territory: {}", van.getVanId(), vansOnTerritory);
        } finally {
            lock.unlock();
        }
    }

    public Terminal acquireTerminal() throws InterruptedException {
        lock.lock();
        try {
            while (freeTerminals.isEmpty()) {
                baseChanged.await();
            }
            return freeTerminals.poll();
        } finally {
            lock.unlock();
        }
    }

    public void releaseTerminal(Terminal terminal) {
        lock.lock();
        try {
            freeTerminals.add(terminal);
            baseChanged.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void completeService(Van van) {
        lock.lock();
        try {
            if (van.getOperation() == CargoOperation.LOAD) {
                goodsInStock -= van.getCargoAmount();
                reservedForLoading -= van.getCargoAmount();
            } else {
                goodsInStock += van.getCargoAmount();
                reservedForUnloading -= van.getCargoAmount();
            }
            vansOnTerritory--;
            LOGGER.info("Van {} served and left the base, goods in stock: {}", van.getVanId(), goodsInStock);
            baseChanged.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getGoodsInStock() {
        lock.lock();
        try {
            return goodsInStock;
        } finally {
            lock.unlock();
        }
    }

    private boolean canEnter(Van van) {
        boolean hasFreePlace = vansOnTerritory < TERRITORY_CAPACITY;
        boolean yieldsToPerishable = !van.isPerishable() && hasServablePerishableVan();
        return hasFreePlace && canServe(van) && !yieldsToPerishable;
    }

    private boolean canServe(Van van) {
        if (van.getOperation() == CargoOperation.LOAD) {
            return goodsInStock - reservedForLoading >= van.getCargoAmount();
        }
        return goodsInStock + reservedForUnloading + van.getCargoAmount() <= STORAGE_CAPACITY;
    }

    private boolean hasServablePerishableVan() {
        for (Van perishableVan : waitingPerishableVans) {
            if (canServe(perishableVan)) {
                return true;
            }
        }
        return false;
    }

    private void reserveStorage(Van van) {
        if (van.getOperation() == CargoOperation.LOAD) {
            reservedForLoading += van.getCargoAmount();
        } else {
            reservedForUnloading += van.getCargoAmount();
        }
    }
}
