package org.firsova.task3.state;

import org.firsova.task3.entity.Van;

public interface VanState {
    void handle(Van van);

    boolean isFinal();
}
