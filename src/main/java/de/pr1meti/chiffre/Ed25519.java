package de.pr1meti.chiffre;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Implement RFC8032
 * Note: big endian is used instead of little endian
 */
public class Ed25519 {
    public static BigInteger sha512(BigInteger s) {
        try {
            return CryptUtils.littleEndianToBigInt(MessageDigest.getInstance("SHA-512").digest(CryptUtils.bigintToLittleEndian(s)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static BigInteger P = BigInteger.TWO.pow(255).subtract(BigInteger.valueOf(19));
    private static BigInteger Q = BigInteger.TWO.pow(252).add(new BigInteger("27742317777372353535851937790883648493"));

    public static BigInteger sha512_modq(BigInteger s) {
        return sha512(s).mod(P);
    }

    public static BigInteger modp_inv(BigInteger x) {
        return x.modPow(P.subtract(BigInteger.TWO), P);
    }

    private static BigInteger D = BigInteger.valueOf(-121_665).multiply(modp_inv(BigInteger.valueOf(121_666))).mod(P);

    public static class PrivateKey {
        public BigInteger getA() {
            return a;
        }

        public BigInteger getDummy() {
            return dummy;
        }

        public PrivateKey(BigInteger a, BigInteger dummy) {
            this.a = a;
            this.dummy = dummy;
        }

        private final BigInteger a;
        private final BigInteger dummy;
    }

    public static PrivateKey secret_expand(String sec) {
        if (sec.length() / 2 != 32) throw new IllegalArgumentException("Invalid input length for compression");
        return secret_expand(CryptUtils.littleEndianToBigInt(sec));
    }
    public static PrivateKey secret_expand(BigInteger sec) {
        BigInteger h = sha512(sec);
        BigInteger a = h.and(BigInteger.ONE.shiftLeft(254).subtract(BigInteger.valueOf(8)))
                .or(BigInteger.ONE.shiftLeft(254));

        BigInteger dummy = h.shiftRight(256);
        return new PrivateKey(a, dummy);
    }

    public static byte[] secret_to_public(String sec) {
        PrivateKey key = secret_expand(sec);
        return Point.multiply(key.getA(), Point.G).pointCompress();
    }

    public static byte[] secret_to_public(BigInteger sec) {
        PrivateKey key = secret_expand(sec);
        return Point.multiply(key.getA(), Point.G).pointCompress();
    }

    public static BigInteger sign(BigInteger sec, BigInteger msg) {
        return null;
    }

    public static boolean verify(BigInteger pub, BigInteger msg, BigInteger sig) {
        return false;
    }

    public static class Point {
        private BigInteger x;
        private BigInteger y;
        private BigInteger z;
        private BigInteger t;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {
                return false;
            }
            Point p2 = (Point) o;
            if (!getX().multiply(p2.getZ()).subtract(p2.getX().multiply(getZ())).mod(P).equals(BigInteger.ZERO)) {
                return false;
            }
            return getY().multiply(p2.getZ()).subtract(p2.getY().multiply(getZ())).mod(P).equals(BigInteger.ZERO);
        }

        @Override
        public int hashCode() {
            int result = x != null ? x.hashCode() : 0;
            result = 31 * result + (y != null ? y.hashCode() : 0);
            result = 31 * result + (z != null ? z.hashCode() : 0);
            result = 31 * result + (t != null ? t.hashCode() : 0);
            return result;
        }

        public Point add(Point p2) {
            var a = getY().subtract(getX()).multiply(p2.getY().subtract(p2.getX()));
            var b = getY().add(getX()).multiply(p2.getY().add(p2.getX()));
            var c = BigInteger.TWO.multiply(getT()).multiply(p2.getT()).multiply(D).mod(P);
            var d = BigInteger.TWO.multiply(getZ()).multiply(p2.getZ()).mod(P);
            var e = b.subtract(a);
            var f = d.subtract(c);
            var g = d.add(c);
            var h = b.add(a);

            return new Point(e.multiply(f), g.multiply(h), f.multiply(g), e.multiply(h));
        }

        public static Point multiply(BigInteger s, Point p) {
            Point q = new Point(BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO);

            while (s.compareTo(BigInteger.ZERO) > 0) {
                if (s.and(BigInteger.ONE).equals(BigInteger.ONE)) {
                    q = q.add(p);
                }
                p = p.add(p);
                s = s.shiftRight(1);
            }

            return q;
        }

        private static BigInteger modp_sqrt_m1 = BigInteger.TWO.modPow(P.subtract(BigInteger.ONE).divide(BigInteger.valueOf(4)), P);

        public static BigInteger recover_x(BigInteger y, BigInteger sign) {
            if (y.compareTo(P) > 0) {
                return null;
            }

            BigInteger x2 = (y.multiply(y).subtract(BigInteger.ONE)).multiply(modp_inv(D.multiply(y).multiply(y).add(BigInteger.ONE)));

            if (x2.equals(BigInteger.ZERO)) {
                if (sign.compareTo(BigInteger.ZERO) > 0) {
                    return null;
                }
                return BigInteger.ZERO;
            }

            BigInteger x = x2.modPow(P.add(BigInteger.valueOf(3)).divide(BigInteger.valueOf(8)), P);

            if (!x.multiply(x).subtract(x2).mod(P).equals(BigInteger.ZERO)) {
                x = x.multiply(modp_sqrt_m1).mod(P);
            }

            if (!x.multiply(x).subtract(x2).mod(P).equals(BigInteger.ZERO)) {
                return null;
            }

            if (!x.add(BigInteger.ONE).equals(sign)) {
                return P.subtract(x);
            }

            return x;
        }

        private static final BigInteger G_Y = BigInteger.valueOf(4).multiply(modp_inv(BigInteger.valueOf(5))).mod(P);
        private static final BigInteger G_X = recover_x(G_Y, BigInteger.ZERO);
        public static final Point G = new Point(G_X, G_Y, BigInteger.ONE, Objects.requireNonNull(G_X).multiply(G_Y).mod(P));

        public byte[] pointCompress() {
            var z_inv = modp_inv(z);
            var x = this.x.multiply(z_inv).mod(P);
            var y = this.y.multiply(z_inv).mod(P);
            return CryptUtils.bigintToLittleEndian(y.or(x.and(BigInteger.ONE).shiftLeft(255)), 32);
        }

        public static Point pointDecompress(byte[] s) {
            if (s.length != 32) { throw new IllegalArgumentException("Invalid input length for compression"); }

            BigInteger y = CryptUtils.littleEndianToBigInt(s);
            BigInteger sign = y.shiftRight(255);
            y = y.clearBit(255);

            BigInteger x = recover_x(y, sign);
            if (x == null) return null;

            return new Point(x, y, BigInteger.ONE, x.multiply(y).mod(P));
        }

        Point(BigInteger x, BigInteger y, BigInteger z, BigInteger t) {
            this.x = x;
            this.y = y;
            this.t = t;
            this.z = z;
        }

        public BigInteger getX() {
            return x;
        }

        public BigInteger getY() {
            return y;
        }

        public BigInteger getT() {
            return t;
        }

        public BigInteger getZ() {
            return z;
        }
    }

}