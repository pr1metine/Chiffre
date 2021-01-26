package de.pr1meti.chiffre;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VigenereTest {
    public static final String INPUT = "HELLOHELLOLOLO";
    public static HashMap<String, ArrayList<Integer>> result5Len;
    public static HashMap<String, ArrayList<Integer>> result2Len;
    public static ArrayList<HashMap<Character, AtomicInteger>> result1KeyLen;
    public static ArrayList<HashMap<Character, AtomicInteger>> result8KeyLen;

    @BeforeAll
    static void initResult() {
        result5Len = Vigenere.findReoccurringPassages(INPUT, 5);
        result2Len = Vigenere.findReoccurringPassages(INPUT, 2);
        result1KeyLen = Vigenere.frequencyAnalysis(INPUT, 1);
        result8KeyLen = Vigenere.frequencyAnalysis(INPUT, 8);
    }

    @Test
    @DisplayName("Test function findReoccurringPassages with 5 letter long words")
    void testFRP5() {
        assertEquals(2, result5Len.get("HELLO")
                                  .size());
    }

    @Test
    @DisplayName("Test function findReoccurringPassages with 2 letter long words")
    void testFRP2() {
        assertEquals(4, result2Len.get("LO")
                                  .size());
    }

    @Test
    @DisplayName("Test function frequencyAnalysis assuming a key length of 1")
    void testFA1() {
        assertEquals(2, result1KeyLen.get(0)
                                     .get('H')
                                     .get());
        assertEquals(2, result1KeyLen.get(0)
                                     .get('E')
                                     .get());
        assertEquals(6, result1KeyLen.get(0)
                                     .get('L')
                                     .get());
        assertEquals(4, result1KeyLen.get(0)
                                     .get('O')
                                     .get());
    }

    @Test
    @DisplayName("Test function frequencyAnalysis assuming a key length of 7")
    void testFA7() {
        assertEquals(1, result8KeyLen.get(7)
                                     .size());
    }
}
