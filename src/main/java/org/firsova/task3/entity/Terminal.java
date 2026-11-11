package org.firsova.task3.entity;

public class Terminal {
    private final int terminalId;

    public Terminal(int terminalId) {
        this.terminalId = terminalId;
    }

    public int getTerminalId() {
        return terminalId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Terminal other = (Terminal) obj;
        return terminalId == other.terminalId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(terminalId);
    }

    @Override
    public String toString() {
        return new StringBuilder("Terminal{terminalId=")
                .append(terminalId)
                .append('}')
                .toString();
    }
}
