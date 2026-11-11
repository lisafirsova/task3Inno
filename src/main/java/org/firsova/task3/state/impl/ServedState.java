package org.firsova.task3.state.impl;

import org.firsova.task3.entity.Van;
import org.firsova.task3.state.VanState;

public class ServedState implements VanState {
    @Override
    public void handle(Van van) {
        throw new UnsupportedOperationException("Van " + van.getVanId() + " is already served");
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
