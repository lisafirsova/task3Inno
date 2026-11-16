package org.firsova.task3.train;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.base.LogisticsBase;

import java.util.TimerTask;

public class SupplyTrain extends TimerTask {
    private static final Logger LOGGER = LogManager.getLogger(SupplyTrain.class);

    @Override
    public void run() {
        int delivered = LogisticsBase.getInstance().balanceStock();
        if (delivered > 0) {
            LOGGER.info("Supply train brought {} goods to the base", delivered);
        } else if (delivered < 0) {
            LOGGER.info("Supply train took {} goods from the base", -delivered);
        }
    }
}
