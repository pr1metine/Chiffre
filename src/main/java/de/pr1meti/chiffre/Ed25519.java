package de.pr1meti.chiffre;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Implement RFC8032
 */
public class Ed25519 {

    private static class Point {
        private BigInteger x;
        private BigInteger y;

        private Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    private static MessageDigest md;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * P = 2 ^ 255 - 19
     */
    private static final BigInteger P = BigInteger.TWO.pow(255).subtract(BigInteger.valueOf(19));
    private static final BigInteger B_LENGTH = BigInteger.valueOf(256);
    private static final BigInteger C = BigInteger.valueOf(3);
    private static final BigInteger N = BigInteger.valueOf(254);
    private static final BigInteger D = new BigInteger("37095705934669439343138083508754565189542113879843219016388785533085940283555");
    private static final Point B_POINT =
            new Point(
                    new BigInteger("15112221349535400772501151409588531511454012693041857206046113283949847762202"),
                    new BigInteger("46316835694926478169428394003475163141307993866256225615783033603165251855960")
            );
    private static final BigInteger L = BigInteger.TWO.pow(252).add(new BigInteger("27742317777372353535851937790883648493"));

    private BigInteger sec;

    static {
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static Ed25519 keygen() {
        BigInteger sec = new BigInteger(256, RANDOM);
        BigInteger h = new BigInteger(md.digest(sec.toByteArray()));
        return new Ed25519();
    }
}
