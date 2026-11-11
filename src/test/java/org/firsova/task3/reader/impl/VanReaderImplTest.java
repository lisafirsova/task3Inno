package org.firsova.task3.reader.impl;

import org.firsova.task3.exception.LogisticsException;
import org.firsova.task3.reader.VanReader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VanReaderImplTest {
    private final VanReader reader = new VanReaderImpl();

    @Test
    void missingFileFallsBackToDefaultFile() throws LogisticsException {
        List<String> lines = reader.readLines("no-such-file.txt");

        assertEquals("1;UNLOAD;30;false", lines.get(0));
    }
}
