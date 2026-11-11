package org.firsova.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.base.LogisticsBase;
import org.firsova.task3.entity.Terminal;
import org.firsova.task3.entity.Van;
import org.firsova.task3.state.VanState;

import java.util.concurrent.TimeUnit;

public class ProcessingState implements VanState {
    private static final Logger LOGGER = LogManager.getLogger(ProcessingState.class);
    private static final int MILLIS_PER_CARGO_UNIT = 10;

    @Override
    public void handle(Van van) throws InterruptedException {
        LogisticsBase base = LogisticsBase.getInstance();
        Terminal terminal = base.acquireTerminal();
        try {
            LOGGER.info("Van {} started to {} {} goods at terminal {}",
                    van.getVanId(), van.getOperation(), van.getCargoAmount(), terminal.getTerminalId());
            TimeUnit.MILLISECONDS.sleep((long) van.getCargoAmount() * MILLIS_PER_CARGO_UNIT);
        } finally {
            base.releaseTerminal(terminal);
        }
        base.completeService(van);
        van.setState(new ServedState());
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
