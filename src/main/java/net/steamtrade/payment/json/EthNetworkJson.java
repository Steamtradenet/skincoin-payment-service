package net.steamtrade.payment.json;

/**
 * Created by sasha on 19.07.17.
 */
public enum EthNetworkJson {
    MAIN_NET("MAIN_NET"),
    TEST_NET("TEST_NET"),
    PRIVATE_NET("PRIVATE_NET");

    private final String name;

    EthNetworkJson(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
