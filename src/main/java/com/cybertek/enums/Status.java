package com.cybertek.enums;

public enum Status {

    OPEN("Open"), IN_PROGRESS("In Progress"), COMPLETE("COmpleted"), UAT_TEST("UAT Testing");

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
