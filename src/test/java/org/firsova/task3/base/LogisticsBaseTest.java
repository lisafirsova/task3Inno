package org.firsova.task3.base;

import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Van;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogisticsBaseTest {
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
    void allVansAreServedAndStockIsConsistent() throws Exception {
        List<Van> vans = List.of(
                new Van(101, CargoOperation.UNLOAD, 30, false),
                new Van(102, CargoOperation.LOAD, 40, false),
                new Van(103, CargoOperation.LOAD, 20, true),
                new Van(104, CargoOperation.UNLOAD, 25, true),
                new Van(105, CargoOperation.LOAD, 10, false),
                new Van(106, CargoOperation.UNLOAD, 35, false));
        LogisticsBase base = LogisticsBase.getInstance();
        int stockBefore = base.getGoodsInStock();

        ExecutorService executor = Executors.newFixedThreadPool(vans.size());
        executor.invokeAll(vans);
        executor.shutdown();

        for (Van van : vans) {
            assertTrue(van.getState().isFinal(), "Van " + van.getVanId() + " must be served");
        }
        int expectedStock = stockBefore + 30 - 40 - 20 + 25 - 10 + 35;
        assertEquals(expectedStock, base.getGoodsInStock());
    }
}
