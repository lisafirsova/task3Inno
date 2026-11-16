package org.firsova.task3.base;

import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Terminal;
import org.firsova.task3.entity.Van;
import org.firsova.task3.train.SupplyTrain;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogisticsBaseTest {
    private final LogisticsBase base = LogisticsBase.getInstance();

    @Test
    void getInstanceReturnsSameObjectFromManyThreads() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<LogisticsBase>> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            tasks.add(LogisticsBase::getInstance);
        }

        Set<LogisticsBase> instances = new HashSet<>();
        for (Future<LogisticsBase> future : executor.invokeAll(tasks)) {
            instances.add(future.get());
        }
        executor.shutdown();

        assertEquals(1, instances.size());
    }

    @Test
    void takenTerminalIsReturnedBack() {
        Van van = new Van(301, CargoOperation.LOAD, 1, false);

        Optional<Terminal> terminal = base.takeTerminal(van);
        assertTrue(terminal.isPresent());
        base.releaseTerminal(terminal.get());

        assertTrue(base.takeTerminal(van).map(taken -> {
            base.releaseTerminal(taken);
            return true;
        }).orElse(false));
    }

    @Test
    void transferGoodsNeverGoesBeyondStorageLimits() {
        Van hugeLoad = new Van(302, CargoOperation.LOAD, 1_000, false);
        Van hugeUnload = new Van(303, CargoOperation.UNLOAD, 1_000, false);
        int stockBefore = base.getGoodsInStock();

        assertFalse(base.transferGoods(hugeLoad));
        assertFalse(base.transferGoods(hugeUnload));
        assertEquals(stockBefore, base.getGoodsInStock());
    }

    @Test
    void supplyTrainKeepsStockInNormalRange() {
        new SupplyTrain().run();

        int stock = base.getGoodsInStock();
        assertTrue(stock >= 25 && stock <= 75, "stock must be between 25 and 75 but was " + stock);
    }

    @Test
    void allVansAreServedAndStockIsConsistent() throws Exception {
        List<Van> vans = List.of(
                new Van(101, CargoOperation.UNLOAD, 20, false),
                new Van(102, CargoOperation.LOAD, 25, false),
                new Van(103, CargoOperation.LOAD, 15, true),
                new Van(104, CargoOperation.UNLOAD, 25, true),
                new Van(105, CargoOperation.LOAD, 10, false),
                new Van(106, CargoOperation.UNLOAD, 15, false));
        new SupplyTrain().run();
        int stockBefore = base.getGoodsInStock();

        ExecutorService executor = Executors.newFixedThreadPool(vans.size());
        executor.invokeAll(vans);
        executor.shutdown();

        for (Van van : vans) {
            assertTrue(van.getState().isFinal(), "Van " + van.getVanId() + " must be served");
        }
        assertEquals(stockBefore + 20 - 25 - 15 + 25 - 10 + 15, base.getGoodsInStock());
    }
}
