package org.firsova.task3.state.impl;

import org.firsova.task3.base.LogisticsBase;
import org.firsova.task3.entity.Terminal;
import org.firsova.task3.entity.Van;
import org.firsova.task3.state.VanState;

import java.util.Optional;

public class WaitingState implements VanState {
    @Override
    public void handle(Van van) {
        Optional<Terminal> terminal = LogisticsBase.getInstance().takeTerminal(van);
        terminal.ifPresent(taken -> van.setState(new ProcessingState(taken)));
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
