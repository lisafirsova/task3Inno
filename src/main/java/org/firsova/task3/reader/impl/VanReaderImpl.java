package org.firsova.task3.reader.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.firsova.task3.exception.LogisticsException;
import org.firsova.task3.reader.VanReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VanReaderImpl implements VanReader {
    private static final Logger LOGGER = LogManager.getLogger(VanReaderImpl.class);
    private static final String DEFAULT_FILE_PATH = "data/vans.txt";

    @Override
    public List<String> readLines(String path) throws LogisticsException {
        String fileName = path;
        if (fileName == null || Files.notExists(Path.of(fileName))) {
            fileName = DEFAULT_FILE_PATH;
        }
        Path filePath = Path.of(fileName);
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            LOGGER.info("Read {} lines from file {}", lines.size(), fileName);
            return lines;
        } catch (IOException e) {
            throw new LogisticsException("Cannot read file: " + fileName, e);
        }
    }
}
