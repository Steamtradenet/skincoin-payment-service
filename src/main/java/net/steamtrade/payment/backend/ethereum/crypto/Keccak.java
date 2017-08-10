package net.steamtrade.payment.backend.ethereum.crypto;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLongs;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;


public class Keccak {

    /** Round Constants */

    private static final long[] ROUND_CONSTANTS = {
            0x0000000000000001L,
            0x0000000000008082L,
            0x800000000000808AL,
            0x8000000080008000L,
            0x000000000000808BL,
            0x0000000080000001L,
            0x8000000080008081L,
            0x8000000000008009L,
            0x000000000000008AL,
            0x0000000000000088L,
            0x0000000080008009L,
            0x000000008000000AL,
            0x000000008000808BL,
            0x800000000000008BL,
            0x8000000000008089L,
            0x8000000000008003L,
            0x8000000000008002L,
            0x8000000000000080L,
            0x000000000000800AL,
            0x800000008000000AL,
            0x8000000080008081L,
            0x8000000000008080L,
            0x0000000080000001L,
            0x8000000080008008L
    };

    /** Rotation Offsets */

    private static final int[][] ROTATION_OFFSETS = {
            {0,    36,     3,    41,    18},
            {1,    44,    10,    45,     2},
            {62,    6,    43,    15,    61},
            {28,   55,    25,    21,    56},
            {27,   20,    39,     8,    14}
    };

    /**
     * Width of the permutation (parameter b from the specification).
     * Default value: 1600
     */

    private int width;

    /**
     * Capacity of the permutation.
     * Default value: 576
     */

    private int capacity;

    /**
     * Bitrate of the permutation.
     * Default value: 1024 (for width = 1600 and capacity 576)
     */

    private int bitrate;

    /**
     * Lane width.
     * Default value: 64 (for a width of 1600)
     *
     * This is calculated using b/25
     */

    private int laneWidth;

    /**
     * Number of Keccak-f rounds.
     * Default value: 24 (for a width of 1600)
     *
     * This is calculated using 12 + 2 (w/25)
     */

    private int numberOfRounds;

    /**
     * State of the sponge construction.
     */

    private long[][] state;

    /**
     * Size of the final digest;
     */

    private int size;

    /**
     * Default constructor. Uses a width of the permutation of 1600.
     */

    public Keccak (){
        // Log.set(Log.LEVEL_DEBUG);
        this.width = 1600;
        this.capacity = 576;
        this.bitrate = 1024;
        this.laneWidth = 64;
        this.numberOfRounds = 24;
        this.state = new long[5][5];
        this.size = 512;
        this.occupation = 0;
        this.squeezing = false;
        this.queue = new byte[this.bitrate / 8];
    }

    /**
     * Constructor using the size of the digest.
     *
     * @param size of the final digest; possible values: 224, 256, 384 or 512
     */

    public Keccak (int size) {
        this();

        switch (size) {
            case 224: this.setParametersForSHA3(1152, 448); break;
            case 256: this.setParametersForSHA3(1088, 512); break;
            case 384: this.setParametersForSHA3(832, 768); break;
            case 512: this.setParametersForSHA3(576, 1024); break;
            default: throw new RuntimeException("The size of the digest should be 224, 256, 384 or 512.");
        }

        this.size = size;
        this.queue = new byte[this.bitrate / 8];
    }

    /**
     * Static factory to get a Keccak object
     * using width of the permutation.
     *
     * @param width width of the permutation (b parameter);
     *              possible values: 25, 50, 100, 200, 400, 800 or 1600.
     */

    public static Keccak getKeccakWithPermutationWidth(int width) {
        Keccak keccak = new Keccak();
        keccak.setWidth(width);

        return keccak;
    }

    /**
     * Logs the current the state of the permutation.
     * Intended only for debugging purposes.
     *
     * @param message message to be used as title of the current state
     */

    private void logState(String message) {
        String logEntry = message + "[ ";
        for (int y = 0; y < 5; y++) {
            logEntry += "\t[ ";
            for (int x = 0; x < 5; x++) {
                logEntry += "0x" + UnsignedLongs.toString(this.state[x][y], 16) + ", ";
            }
            logEntry += " ]";
        }
        logEntry += " ]";

        debug(logEntry);
    }


    public void debug(String msg) {
        System.out.println(msg);
    }

    /**
     * Validates the width of a permutation.
     * It should be 25, 50, 100, 200, 400, 800 or 1600.
     *
     * @param width given width
     * @return validation result
     */

    private boolean isValidWidth(int width) {
        return (width == 25  || width == 50  || width == 100 ||
                width == 200 || width == 400 || width == 800 ||
                width == 1600);
    }

    /**
     * Setter for the width of the permutation.
     * Possible values: 25, 50, 100, 200, 400, 800, 1600.
     *
     * @param width width of the permutation
     */

    private void setWidth(int width) {

        assert isValidWidth(width) :
                "The width of the permutation (b) should be" +
                        "25, 50, 100, 200, 400, 800 or 1600.";

        this.width = width;
        this.laneWidth = width / 25;

        /* 31 - Integer.numberOfLeadingZeros()
           is log base 2 for integers */
        this.numberOfRounds = 12 + 2 * (31 - Integer.numberOfLeadingZeros(this.laneWidth));

    }

    /**
     * Setter for the parameters of the construction. Uses the default permutation
     * width of 1600 bits of SHA-3.
     *
     * @param bitrate bitrate of the permutation
     * @param capacity capacity of the permutation
     */

    private void setParametersForSHA3(int bitrate, int capacity) {

        assert bitrate + capacity == 1600:
                "The parameters don't match.";

        this.bitrate = bitrate;
        this.capacity = capacity;
    }

    /**
     * Calculates the padding of the message using
     * follow the specification:
     *
     *      P = M || 0x01 || 0x00 || … || 0x00
     *      P = P xor (0x00 || … || 0x00 || 0x80)
     *
     * Note this only works for a message with
     * a length divisible by 8.
     *
     * @param length length of the message
     * @param message message to pad
     * @return padded message
     */

    private byte[] paddingSimplified(int length, byte[] message, int blockSize) {

        assert length % 8 == 0 :
                "The message length is not divisible by 8.";

        /* if the length is the same size of the message
           we have to add a byte for padding */
        if (length / 8 == message.length)
            length += 8;

        /* size of the padding */
        double blocks = Math.ceil((double) length / blockSize);
        int size = (int) (blockSize * blocks / 8);

        byte[] padding = new byte[size];

        /* M || 0x01 || 0x00 || … || 0x00 */
        System.arraycopy(message,0,padding,0,(length / 8) - 1);
        padding[(length / 8) - 1] = (byte) 0x01;

        /* P = P xor (0x00 || … || 0x00 || 0x80) */
        byte[] temp = new byte[padding.length];
        temp[padding.length - 1] = (byte) 0x80;
        for (int i = 0;i < padding.length;i++)
            padding[i] = (byte) (padding[i] ^ temp[i]);

        return padding;
    }

    /**
     * Calculates the padded message using the 10*1 padding rule,
     * or multi-rate padding.
     *
     * Note that the last byte has its bits shifted as in the specification
     * for the SHA-3:
     *
     * <blockquote>In practice, the above operation cancels with the change
     * of convention, so there is nothing to do, except:
     *
     * • For the last byte if it contains p < 8 bits, the bits are shifted
     * by 8 − p positions towards the LSB (i.e., to the "right").</blockquote>
     *
     * @see <a href="http://keccak.noekeon.org/Keccak-submission-3.pdf">
     *      The Keccak SHA-3 submission, 6.1 Bit and byte numbering</a>
     *
     * @param length length of the message
     * @param message the message to pad
     * @param blockSize the size of a block
     * @return padded message
     */

    private byte[] padding10star1(int length, byte[] message, int blockSize) {

        /* padding offset in the byte */
        int offset = length % 8;

        /* length of the padding in bits */
        int paddingBits = 8 - offset;

        /* remaining bits in the last block */
        int lastBlockLength = length % blockSize;

        byte[] paddedMessage;
        byte padding = 0;

        /* if the padding fits, we get the corresponding
           byte in the message */
        if (length / 8 != 8 || offset != 0)
            padding = (byte) message[length / 8];

        /* shift the bits in the padding byte to the right as noted in the spec;
         * we need the additional operation because Java promotes bytes to int,
         * and they're signed */
        padding = (byte) ((padding >> paddingBits) & (0xFF >>> paddingBits));

        if (lastBlockLength >= (blockSize - 8) &&
                lastBlockLength <= (blockSize - 2)) {

            /* set the first and last bit of padding */
            padding += (int)(Math.pow(2,8 - paddingBits)) + (int)Math.pow(2,7);

            /* create the padded message */
            paddedMessage = new byte[((int)Math.ceil((length + paddingBits)) / 8)];

        } else {
            padding += (int)(Math.pow(2,8 - paddingBits));

            if (paddingBits < 2)
                paddingBits = 2;

            double blocks = Math.ceil(((double)length + paddingBits) / blockSize);
            paddedMessage = new byte[(int) (blockSize * blocks / 8.0)];

            /* append the last block of padding */
            paddedMessage[paddedMessage.length - 1] |= 0x80;
        }

        System.arraycopy(message, 0, paddedMessage, 0, length / 8);

        paddedMessage[length / 8] = (byte)padding;

        return paddedMessage;
    }

    /**
     * Calculator for the padded message.
     *
     * We use two different padding rules. If the length of
     * the message is divisible by 8 we use a simplification
     * of the multi-rate padding rule, otherwise this rule is
     * used.
     *
     * @param length length of the message
     * @param message message to pad
     * @param blockSize size of the block
     * @return the padded message
     */

    private byte[] padMessage(int length, byte[] message, int blockSize) {

        assert blockSize % 8 == 0 :
                "The size of the block must be a multiple of 8.";

        if (length % 8 == 0 && length > 0)
            return paddingSimplified(length,message,blockSize);
        else
            return padding10star1(length, message, blockSize);

    }

    /**
     * Converts a byte array to a lane value.
     *
     * @param data the byte array to convert
     * @return converted lane value
     */

    private long toLane(byte[] data) {
        Collections.reverse(Bytes.asList(data));
        return ByteBuffer.wrap(data).getLong();
    }

    /**
     * Converts a lane value into a byte array.
     *
     * @param data lane value to convert
     * @return converted byte array
     */

    private byte[] fromLane(long data) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(data);
        buffer.clear();
        byte[] output = new byte[buffer.remaining()];
        buffer.get(output, 0, output.length);
        Collections.reverse(Bytes.asList(output));
        return output;
    }

    /**
     * Converts a message into a table representation (5x5 matrix).
     *
     * @param message message to convert into a table
     * @return 5x5 matrix representation of the message
     */

    private long[][] convertMessageToTable(byte[] message) {

        assert this.laneWidth % 8 == 0 :
                "The lane width must be divisible by 8.";

        assert message.length * 8 == this.width :
                "The length of the message must be " +
                        "the same as the width of the permutation";

        long[][] output = new long[5][5];

        int offset;

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                offset = ((5 * y + x) * this.laneWidth) / 8;

                output[x][y] = this.toLane(Arrays.copyOfRange(message, offset, offset + this.laneWidth / 8));
            }
        }

        return output;
    }

    /**
     * Converts a table representation (5x5 matrix) into a message.
     *
     * @param table 5x5 matrix representation of the message
     * @return converted message
     */

    private byte[] convertTableToMessage(long[][] table) {

        assert this.laneWidth % 8 == 0 :
                "The lane width must be a multiple of 8";

        assert table.length == 5 :
                "The table must be 5x5";

        assert table[0].length == 5 &&
                table[1].length == 5 &&
                table[2].length == 5 &&
                table[3].length == 5 &&
                table[4].length == 5 :
                "The table must be 5x5";

        byte[][] outputs = new byte[25][];

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                outputs[5 * y + x] = fromLane(table[x][y]);
            }
        }

        int size = 0;
        for (byte[] temp : outputs)
            size += temp.length;

        byte[] output = new byte[size];

        for (int i = 0; i < 25; i++)
            System.arraycopy(outputs[i],0,output,i * 8,outputs[i].length);

        return output;
    }

    /**
     * Performs a bitwise right rotation on the given long.
     *
     * @param a long to be rotated
     * @param n number of shifts to make
     * @return rotated long
     */

    private long rotate(long a, int n) {
        return (a << n) | (a >>> (64 - n));
    }

    /**
     * θ step of the Keccak permutation.
     *
     * @param state current state of the permutation
     * @param C intermediate variable
     * @param D intermediate variable
     * @return state after this step
     */

    private long[][] theta(long[][] state, long[] C, long[] D) {
        for (int x = 0; x < 5; x++)
            C[x] = state[x][0] ^ state[x][1] ^ state[x][2] ^ state[x][3] ^ state[x][4];

        for (int x = 0; x < 5; x++)
            D[x] = C[((x - 1) % 5 + 5) % 5] ^ this.rotate(C[(x + 1) % 5],1);

        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
                state[x][y] = state[x][y] ^ D[x];

        return state;
    }

    /**
     * ρ and π steps of the Keccak permutation.
     *
     * @param state current state of the permutation
     * @param B intermediate variable
     * @return state after this step
     */

    private long[][] rhoAndPi(long[][] state, long[][] B) {
        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
                B[y][(2 * x + 3 * y) % 5] = this.rotate(state[x][y], ROTATION_OFFSETS[x][y]);

        return state;
    }

    /**
     * χ step of the Keccak permutation.
     *
     * @param state current state of the permutation
     * @param B intermediate variable
     * @return state after this step
     */

    private long[][] chi(long[][] state, long[][] B) {
        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
                state[x][y] = B[x][y] ^ ((~B[(x + 1) % 5][y]) & B[(x + 2) % 5][y]);

        return state;
    }

    /**
     * ι step of the Keccak permutation.
     *
     * @param state current state of the permutation
     * @param constant round constant to use in this step
     * @return state after this step
     */

    private long[][] iota(long[][] state, long constant) {
        state[0][0] = state[0][0] ^ constant;
        return state;
    }

    /**
     * Keccak-f permutation round.
     *
     * @param state current state of the permutation
     * @param constant round constant to be used in this round
     * @return state after this round
     */

    private long[][] round(long[][] state,long constant) {
        long[][] B = new long[5][5];
        long[] C = new long[5];
        long[] D = new long[5];

        state = this.theta(state,C,D);

        state = this.rhoAndPi(state,B);

        state = this.chi(state,B);

        state = this.iota(state,constant);

        return state;
    }

    /**
     * Keccak-f permutation.
     *
     * @param state current state of the permutation
     * @return state after the permutation
     */

    private long[][] keccakF(long[][] state) {
        this.logState("Before first round: ");

        for (int i = 0; i < this.numberOfRounds; i++) {
            state = this.round(state,ROUND_CONSTANTS[i]);

            this.logState("End of round " + (i + 1) + ": ");
        }

        return state;
    }

    /**
     * Absorbing phase of the Keccak sponge construction.
     *
     * Used for calculating a digest at once.
     */

    private void absorb(byte[] paddedMessage) {
        long[][] block;
        for (int i = 0; i < paddedMessage.length; i += this.bitrate / 8) {
            byte[] message = new byte[(this.bitrate / 8) + (this.capacity / 8)];
            System.arraycopy(paddedMessage, i, message, 0, this.bitrate / 8);
            block = this.convertMessageToTable(message);

            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    this.state[x][y] = this.state[x][y] ^ block[x][y];
                }
            }

            this.state = this.keccakF(this.state);
        }
    }

    /**
     * Squeezing phase of the Keccak sponge construction.
     *
     * @param size size of the final digest
     * @return digest
     */

    private byte[] squeeze(int size) {
        this.squeezing = true;
        byte[] output = new byte[size];

        int length = this.bitrate / 8;
        for (int position = 0, temp = size;temp > 0; position += length) {
            byte[] message = this.convertTableToMessage(this.state);
            System.arraycopy(message,0,output,position,length);
            temp -= this.bitrate;

            if (temp > 0)
                this.state = this.keccakF(this.state);
        }

        return Arrays.copyOfRange(output,0,size / 8);
    }

    /**
     * Calculates the digest using length bits of the message,
     * a Keccak permutation with bitrate and capacity parameters
     * and final digest size.
     *
     * @param length number of bits of the message to use
     * @param message message of which to calculate the digest
     * @param bitrate bitrate of the Keccak construction
     * @param capacity capacity of the Keccak construction
     * @param size size of the final digest
     * @return the digest of length bits of the message
     */

    public byte[] digest(int length, byte[] message, int bitrate, int capacity, int size) {

        assert bitrate > 0 && bitrate % 8 == 0 :
                "The bitrate must be a multiple of 8.";

        assert size % 8 == 0 :
                "The output length must be a multiple of 8.";

        this.setWidth(bitrate + capacity);

        this.laneWidth = (bitrate + capacity) / 25;

        this.bitrate = bitrate;

        this.capacity = capacity;

        byte[] paddedMessage = this.padMessage(length, message, this.bitrate);

        /* debugging log */
//        Log.debug("Before Absorption: " + KeccakUtilities.bytesToHex(paddedMessage));

        this.absorb(paddedMessage);

        /* debugging log */
//        Log.debug("After Absorption: " + KeccakUtilities.bytesToHex(this.convertTableToMessage(this.state)));

        byte[] digest = squeeze(size);

        /* debugging log */
//        Log.debug("After squeezing: " + KeccakUtilities.bytesToHex(this.convertTableToMessage(this.state)));

        this.reset();

        return digest;

    }

    /**
     * Number of bits occupied in the queue.
     */

    private int occupation;

    /**
     * Indicator of the status of the digest.
     */

    private boolean squeezing;

    /**
     * Queue of bytes to queue.
     */

    private byte[] queue;

    /**
     * Absorbing phase for a single block.
     */

    private void absorb() {
        byte[] message = new byte[this.width / 8];
        System.arraycopy(this.queue, 0, message, 0, this.bitrate / 8);
        long[][] block = this.convertMessageToTable(message);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                this.state[x][y] = this.state[x][y] ^ block[x][y];
            }
        }
        this.state = this.keccakF(this.state);

        this.occupation = 0;
    }

    /**
     * Enqueues the first length bits of the specified array of bytes.
     *
     * @param data data to be enqueued
     * @param length number of bits to enqueue
     */

    private void enqueue(byte[] data, int length) {

        assert !this.squeezing :
                "Too late for additional input";

        int remaining;

        do {
            /* check if the queue is full before inserting
               because otherwise we could absorb a block
               without padding (we don't know beforehand if
               this is the last block) */
            if (occupation == queue.length) {
                this.absorb();
            }

            remaining = Math.min(length, (this.queue.length * 8) - this.occupation);
            System.arraycopy(data, 0, queue, occupation / 8, remaining / 8);
            occupation += remaining;

            length -= remaining;
        } while (length > 0);
    }

    /**
     * Updates the digest using the specified byte.
     *
     * @param b the byte with which to update the digest
     */

    public void update(byte b) {
        this.enqueue(new byte[]{b}, 8);
    }

    /**
     * Updates the digest from the specified offset to the specified length
     * of the given array.
     *
     * @param bytes the array with the bytes with which to update the digest
     * @param offset the offset from which the bytes will be used
     * @param length the number of values to use to update the digest
     */

    public void update(byte[] bytes, int offset, int length) {
        this.enqueue(Arrays.copyOfRange(bytes, offset, bytes.length), length * 8);
    }

    /**
     * Performs final computations such as padding and
     * calculates the digest of the previously updated bytes.
     *
     * @return the digest
     */

    public byte[] digest() {
        byte[] lastBlockPadded = new byte[occupation / 8];
        System.arraycopy(this.queue, 0, lastBlockPadded, 0, occupation / 8);
        lastBlockPadded = this.padMessage(occupation, lastBlockPadded, this.bitrate);

        this.queue = lastBlockPadded;
        this.absorb();

        /* debugging log */
//        Log.debug("After Absorption: " + KeccakUtilities.bytesToHex(this.convertTableToMessage(this.state)));

        byte[] digest = squeeze(this.size);

        /* debugging log */
//        Log.debug("After squeezing: " + KeccakUtilities.bytesToHex(this.convertTableToMessage(this.state)));

        this.reset();

        return digest;
    }

    /**
     * Resets the state of the digest.
     */

    public void reset() {

        for (int i = 0; i < 5; i++) {
            Arrays.fill(this.state[i], 0);
        }

        this.squeezing = false;
        this.occupation = 0;

        Arrays.fill(this.queue, (byte)0);
    }

}
