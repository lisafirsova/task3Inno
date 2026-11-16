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
    private static final int RETRY_DELAY_MILLIS = 50;

    private final Terminal terminal;

    public ProcessingState(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void handle(Van van) {
        LogisticsBase base = LogisticsBase.getInstance();
        try {
            while (!base.transferGoods(van)) {
                TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MILLIS);
            }
            TimeUnit.MILLISECONDS.sleep((long) van.getCargoAmount() * MILLIS_PER_CARGO_UNIT);
            LOGGER.info("Van {} is served at terminal {}", van.getVanId(), terminal.terminalId());
            van.setState(new ServedState());
        } catch (InterruptedException e) {
            LOGGER.warn("Van {} was interrupted at terminal {}", van.getVanId(), terminal.terminalId(), e);
            Thread.currentThread().interrupt();
        } finally {
            base.releaseTerminal(terminal);
        }
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
