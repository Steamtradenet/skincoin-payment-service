package net.steamtrade.payment.backend.ethereum.crypto;

/**
 * Created by sasha on 29.06.17.
 */
public class Hash {

    private Hash() {
    }

    public static String sha3(String hexInput) {
        byte[] result = sha3(hexInput.getBytes());
        return hashToString(result);
    }

    public static byte[] sha3(byte[] input, int offset, int length) {
        Keccak kecc = new Keccak(256);
        kecc.update(input, offset, length);
        return kecc.digest();
    }

    public static byte[] sha3(byte[] input) {
        return sha3(input, 0, input.length);
    }

    private static String hashToString(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }
}
