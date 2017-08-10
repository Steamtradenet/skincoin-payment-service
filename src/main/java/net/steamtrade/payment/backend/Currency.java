package net.steamtrade.payment.backend;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sasha on 08.02.16.
 */
public enum Currency {
    SKIN("SKIN"),
    ETHER("ETHER");

    private String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Currency fromName(String name) {
        switch (name.toLowerCase()) {
            case "skin":
                return SKIN;
            case "ether":
                return ETHER;
            default:
                throw new RuntimeException("Unsupported currency '" + name +"'");
        }
    }

    public static List<String> getCurrencyNames() {
        return Arrays.asList(SKIN.getName(), ETHER.getName());
    }

}
