package me.exrates.model.enums;

import me.exrates.model.ngExceptions.NgDashboardException;

public enum  PolicyEnum {
    IEO("IEO");

    private final String name;

    PolicyEnum(String name) {
        this.name = name;
    }

    public static PolicyEnum convert(String name) {
        switch (name) {
            case "IEO":
                return IEO;
            default:
                throw new NgDashboardException("Error policy name " + name);
        }
    }

    public String getName() {
        return name;
    }
}
