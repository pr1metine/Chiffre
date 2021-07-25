package de.pr1meti.chiffre;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implement RFC8032
 * Note: big endian is used instead of little endian
 */
public class Ed25519 {
    public static BigInteger sha512(BigInteger s) {
        try {
            return new BigInteger(MessageDigest.getInstance("SHA-512").digest(s.toByteArray()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static BigInteger P = BigInteger.TWO.pow(255).subtract(BigInteger.valueOf(19));
    private static BigInteger Q = BigInteger.TWO.pow(252).add(new BigInteger("27742317777372353535851937790883648493"));

    public static BigInteger sha512_modq(BigInteger s) {
        return sha512(s).mod(P);
    }

    private static BigInteger D = BigInteger.valueOf(-121_665).multiply(BigInteger.valueOf(121_666).modInverse(P)).mod(P);

    public static class Point {
        private BigInteger x;
        private BigInteger y;
        private BigInteger z;
        private BigInteger t;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {return false; }
            Point p2 = (Point) o;
            if (! getX().multiply(p2.getZ()).subtract(p2.getX().multiply(getZ())).mod(P).equals(BigInteger.ZERO)) { return false; }
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

        public Point multiply(BigInteger s, Point p) {
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

        Point(BigInteger x, BigInteger y, BigInteger z, BigInteger t) {
            this.x = x;
            this.y = y;
            this.t = t;
            this.z = z;
        }

        public BigInteger getX() { return x; }

        public BigInteger getY() { return y; }

        public BigInteger getT() { return t; }

        public BigInteger getZ() { return z; }
    }

}