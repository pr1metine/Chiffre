package de.pr1meti.chiffre;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

public class Ed25519Test {

    @Test
    void testPrivateToPublic() {
        String sec = "9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60";
        String pub = "d75a980182b10ab7d54bfed3c964073a0ee172f3daa62325af021a68f707511a";

        assertEquals(CryptUtils.littleEndianToBigInt(pub), CryptUtils.littleEndianToBigInt(Ed25519.secret_to_public(sec)));
    }

    @Test
    void test1() {
        BigInteger sec = new BigInteger("9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60", 16);
        BigInteger pub = new BigInteger("d75a980182b10ab7d54bfed3c964073a0ee172f3daa62325af021a68f707511a", 16);
        BigInteger msg = BigInteger.ZERO;

        BigInteger actual = Ed25519.sign(sec, msg);
        BigInteger expected = new BigInteger("e5564300c360ac729086e2cc806e828a84877f1eb8e5d974d873e065224901555fb8821590a33bacc61e39701cf9b46bd25bf5f0595bbe24655141438e7a100b", 16);

        assertEquals(expected, actual);
    }

    @Test
    void testLittleEndian() {
        assertArrayEquals(new byte[]{-128}, CryptUtils.bigintToLittleEndian(BigInteger.ONE));
        assertArrayEquals(new byte[]{-128, -128}, CryptUtils.bigintToLittleEndian(BigInteger.ONE.or(BigInteger.ONE.shiftLeft(8))));
        assertEquals(BigInteger.ONE, CryptUtils.littleEndianToBigInt(new byte[]{-128}));
        assertEquals(BigInteger.ONE.or(BigInteger.ONE.shiftLeft(8)), CryptUtils.littleEndianToBigInt(new byte[]{-128, -128}));
    }
}
