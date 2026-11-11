package org.firsova.task3.reader;

import org.firsova.task3.exception.LogisticsException;

import java.util.List;

public interface VanReader {
    List<String> readLines(String path) throws LogisticsException;
}
