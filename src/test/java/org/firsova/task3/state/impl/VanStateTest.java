package org.firsova.task3.state.impl;

import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Van;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VanStateTest {
    @Test
    void newVanIsWaiting() {
        Van van = new Van(201, CargoOperation.UNLOAD, 5, false);

        assertInstanceOf(WaitingState.class, van.getState());
        assertFalse(van.getState().isFinal());
    }

    @Test
    void vanPassesAllStatesAndReturnsCargoAmount() throws InterruptedException {
        Van van = new Van(202, CargoOperation.UNLOAD, 5, false);

        int processed = van.call();

        assertEquals(5, processed);
        assertInstanceOf(ServedState.class, van.getState());
        assertTrue(van.getState().isFinal());
    }

    @Test
    void servedStateCannotBeHandledAgain() {
        Van van = new Van(203, CargoOperation.LOAD, 5, false);

        assertThrows(UnsupportedOperationException.class, () -> new ServedState().handle(van));
    }
}
