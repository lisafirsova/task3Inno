package org.firsova.task3.parser.impl;

import org.firsova.task3.entity.CargoOperation;
import org.firsova.task3.entity.Van;
import org.firsova.task3.parser.VanParser;

import java.util.ArrayList;
import java.util.List;

public class VanParserImpl implements VanParser {
    private static final int ID_INDEX = 0;
    private static final int OPERATION_INDEX = 1;
    private static final int CARGO_INDEX = 2;
    private static final int PERISHABLE_INDEX = 3;

    @Override
    public List<Van> parseVans(List<String> lines) {
        List<Van> vans = new ArrayList<>();
        for (String line : lines) {
            String[] fields = line.split(DELIMITER_REGEX);
            int vanId = Integer.parseInt(fields[ID_INDEX]);
            CargoOperation operation = CargoOperation.valueOf(fields[OPERATION_INDEX]);
            int cargoAmount = Integer.parseInt(fields[CARGO_INDEX]);
            boolean perishable = Boolean.parseBoolean(fields[PERISHABLE_INDEX]);
            vans.add(new Van(vanId, operation, cargoAmount, perishable));
        }
        return vans;
    }
}
