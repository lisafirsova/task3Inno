package org.firsova.task3.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.state.VanState;
import org.firsova.task3.state.impl.WaitingState;

import java.util.concurrent.Callable;

public class Van implements Callable<Integer> {
    private static final Logger LOGGER = LogManager.getLogger(Van.class);

    private final int vanId;
    private final CargoOperation operation;
    private final int cargoAmount;
    private final boolean perishable;
    private VanState state = new WaitingState();

    public Van(int vanId, CargoOperation operation, int cargoAmount, boolean perishable) {
        this.vanId = vanId;
        this.operation = operation;
        this.cargoAmount = cargoAmount;
        this.perishable = perishable;
    }

    @Override
    public Integer call() throws InterruptedException {
        LOGGER.info("Van {} arrived to {} {} goods, perishable: {}", vanId, operation, cargoAmount, perishable);
        while (!state.isFinal()) {
            state.handle(this);
        }
        return cargoAmount;
    }

    public int getVanId() {
        return vanId;
    }

    public CargoOperation getOperation() {
        return operation;
    }

    public int getCargoAmount() {
        return cargoAmount;
    }

    public boolean isPerishable() {
        return perishable;
    }

    public VanState getState() {
        return state;
    }

    public void setState(VanState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Van other = (Van) obj;
        return vanId == other.vanId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(vanId);
    }

    @Override
    public String toString() {
        return new StringBuilder("Van{vanId=")
                .append(vanId)
                .append(", operation=")
                .append(operation)
                .append(", cargoAmount=")
                .append(cargoAmount)
                .append(", perishable=")
                .append(perishable)
                .append('}')
                .toString();
    }
}
