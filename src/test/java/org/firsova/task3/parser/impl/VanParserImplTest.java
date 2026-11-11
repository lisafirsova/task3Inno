package org.firsova.task3.parser.impl;

import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Van;
import org.firsova.task3.parser.VanParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VanParserImplTest {
    private final VanParser parser = new VanParserImpl();

    @Test
    void parseVansCreatesVanFromEachLine() {
        List<Van> vans = parser.parseVans(List.of("1;UNLOAD;30;false", "2;LOAD;20;true"));

        assertEquals(2, vans.size());
        Van first = vans.get(0);
        assertEquals(1, first.getVanId());
        assertEquals(CargoOperation.UNLOAD, first.getOperation());
        assertEquals(30, first.getCargoAmount());
        assertFalse(first.isPerishable());
        assertTrue(vans.get(1).isPerishable());
    }
}
