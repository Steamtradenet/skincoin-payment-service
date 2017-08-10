package net.steamtrade.payment.backend.ethereum.utils;

import net.steamtrade.payment.backend.ethereum.crypto.Hash;

import java.security.InvalidAlgorithmParameterException;

/**
 * Created by sasha on 29.06.17.
 */
public class AddressUtils {

    public static String accountToAddress(String hexAccount) throws InvalidAlgorithmParameterException {
        if (hexAccount == null || hexAccount.length() > 42 || hexAccount.length() < 40) {
            throw new IllegalArgumentException("Account must be 20 bytes");
        }
        hexAccount = hexAccount.replaceFirst("0x","");

        String hash = Hash.sha3(hexAccount);

        StringBuilder sb = new StringBuilder();
        for (int i=0; i< hexAccount.length(); i++) {

            String str = hash.substring(i, i+1);

            if (Integer.parseInt(str, 16) >=8) {
                sb.append(hexAccount.substring(i, i + 1).toUpperCase());
            } else {
                sb.append(hexAccount.substring(i, i+1));
            }
        }
        return "0x" + sb.toString();
    }

}
