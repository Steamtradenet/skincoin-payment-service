package net.steamtrade.payment.backend.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by sasha on 29.06.17.
 */
public class PasswordGen {

    public static String generate(int length) {
        return new BigInteger(length * 5, new SecureRandom()).toString(32);
    }

}
