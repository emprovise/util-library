package utility;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public final class UniqueIdGenerator {

    private SecureRandom seeder;
    private String midValueUnformated;

    private UniqueIdGenerator() {
        try {
            StringBuffer sb = new StringBuffer();
            seeder = new SecureRandom();
            InetAddress localhost = InetAddress.getLocalHost();
            String localIP = hexFormat(getInt(localhost.getAddress()), 8);
            String hashCode = hexFormat(this.hashCode(), 8);
            sb.append(localIP.substring(0, 8));
            sb.append(hashCode.substring(0, 8)); // guaranteee we won't go over
            // 8 characters with the
            // hashcode
            midValueUnformated = sb.toString();
        } catch (UnknownHostException exception) {
            throw new RuntimeException(
                    "UnknownHostException while trying to get localhost for UUID: "
                            + exception.getMessage());
        }
    }

    private String getVal(String string) {
        int currentTime = (int) System.currentTimeMillis() & 0xffffffff;
        int randomInt = seeder.nextInt();
        return hexFormat(currentTime, 8) + string + hexFormat(randomInt, 8);
    }

    /**
     * Returns a 32 char UUID string.
     */
    public String getUUID() {
        return getVal(midValueUnformated);
    }

    /**
     * Get an int representation of the first four bytes of the given byte
     * array. This could be problematic if we ever go to IPv6. Then only the 4
     * least unique bytes of the IP address would be used.
     *
     * @param is
     * @return
     */
    private int getInt(byte[] is) {
        int i = 0;
        int shiftAmount = 24; // guarantees only using the first four bytes
        int index = 0;
        while (shiftAmount >= 0) {
            int byteValue = is[index] & 0xff;
            i += byteValue << shiftAmount;
            shiftAmount -= 8;
            index++;
        }
        return i;
    }

    private String hexFormat(int toConvert, int length) {
        String hexString = Integer.toHexString(toConvert);
        return padHex(length, hexString);
    }

    private String padHex(int length, String hexString) {
        StringBuffer sb = new StringBuffer();
        if (hexString.length() < length) {
            for (int i = 0; i < length - hexString.length(); i++) {
                sb.append("0");
            }

        }
        sb.append(hexString);
        return sb.toString();
    }
}
