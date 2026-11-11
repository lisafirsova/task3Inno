package org.firsova.task3.state.impl;

import org.firsova.task3.base.LogisticsBase;
import org.firsova.task3.entity.Van;
import org.firsova.task3.state.VanState;

public class WaitingState implements VanState {
    @Override
    public void handle(Van van) throws InterruptedException {
        LogisticsBase.getInstance().enterTerritory(van);
        van.setState(new ProcessingState());
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
