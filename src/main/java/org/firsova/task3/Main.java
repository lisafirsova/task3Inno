package org.firsova.task3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.base.LogisticsBase;
import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Van;
import org.firsova.task3.exception.LogisticsException;
import org.firsova.task3.parser.VanParser;
import org.firsova.task3.parser.impl.VanParserImpl;
import org.firsova.task3.reader.VanReader;
import org.firsova.task3.reader.impl.VanReaderImpl;
import org.firsova.task3.train.SupplyTrain;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final String FILE_PATH = "data/vans.txt";
    private static final long TRAIN_PERIOD_MILLIS = 100;

    public static void main(String[] args) {
        VanReader reader = new VanReaderImpl();
        VanParser parser = new VanParserImpl();
        List<Van> vans;
        try {
            vans = parser.parseVans(reader.readLines(FILE_PATH));
        } catch (LogisticsException e) {
            LOGGER.error("Cannot load vans", e);
            return;
        }

        Timer timer = new Timer("supply-train", true);
        timer.schedule(new SupplyTrain(), 0, TRAIN_PERIOD_MILLIS);
        ExecutorService executor = Executors.newFixedThreadPool(vans.size(), Thread.ofPlatform().name("van-", 1).factory());
        try {
            List<Future<Integer>> results = executor.invokeAll(vans);
            int loadedGoods = 0;
            int unloadedGoods = 0;
            for (int i = 0; i < vans.size(); i++) {
                int processedGoods = results.get(i).get();
                if (vans.get(i).getOperation() == CargoOperation.LOAD) {
                    loadedGoods += processedGoods;
                } else {
                    unloadedGoods += processedGoods;
                }
            }
            LOGGER.info("All {} vans served. Loaded: {}, unloaded: {}, goods in stock: {}",
                    vans.size(), loadedGoods, unloadedGoods, LogisticsBase.getInstance().getGoodsInStock());
        } catch (InterruptedException e) {
            LOGGER.error("Main thread was interrupted", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOGGER.error("Van service failed", e);
        } finally {
            executor.shutdown();
            timer.cancel();
        }
    }
}
