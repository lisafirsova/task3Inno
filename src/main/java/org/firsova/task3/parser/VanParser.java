package org.firsova.task3.parser;

import org.firsova.task3.entity.Van;

import java.util.List;

public interface VanParser {
    String DELIMITER_REGEX = ";";

    List<Van> parseVans(List<String> lines);
}
