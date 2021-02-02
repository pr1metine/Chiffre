package de.pr1meti.chiffre;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains useful tools to decrypt and encrypt messages
 */
public class CryptUtils {
    /**
     * <p>Shifts a letter with a key.</p>
     *
     * <p>
     * A <strong>clockwise</strong> shift is synonymous to <em>encryption</em>,
     * a <strong>counter-clockwise</strong> shift to <em>decryption</em>.
     * </p>
     *
     * @param toBeShifted             An individual char of a plain text or an
     *                                encrypted message (A-Za-z)
     * @param key                     A char serving as a key (A-Za-z)
     * @param clockwiseShiftDirection If true, encrypt. Otherwise, decrypt
     * @return The shifted key. Can be 'A' - 'Z'
     */
    public static char shift(char toBeShifted, char key,
                             boolean clockwiseShiftDirection) {
        int toBeShiftedInt = Character.toUpperCase(toBeShifted) - 'A';
        int keyInt = Character.toUpperCase(key) - 'A';
        int range = 'Z' - 'A' + 1;
        return (char) ('A' + (toBeShiftedInt + (clockwiseShiftDirection
                                                ? keyInt
                                                : -keyInt) + range)
                             % range);
    }

    /**
     * Performs a frequency analysis of a given input.
     *
     * @param input     Usually an encrypted message
     * @param keyLength When dealing with Vigenère, pass a key length to
     *                  distinguish between <em>key positions</em>.
     * @return A list of maps for each key position containing a count of each
     * occurring character
     */
    public static List<Map<Character, Long>> frequencyAnalysis(String input,
                                                               int keyLength) {
        return IntStream.range(0, keyLength)
                        .mapToObj(
                                i -> IntStream.iterate(i,
                                        ii -> ii < input.length(),
                                        ii -> ii + keyLength)
                                              .mapToObj(input::charAt)
                                              .collect(
                                                      Collectors.groupingBy(
                                                              c -> c,
                                                              Collectors.counting()
                                                      )
                                              )
                        )
                        .collect(Collectors.toUnmodifiableList())
                ;
    }


    /**
     * Finds reoccurring passages inside a usually encrypted message. Essential,
     * when determining the key length of a Vigenère-encrypted message.
     *
     * @param input         A usually Vigenère-encrypted message
     * @param passageLength Length of the reoccurring passage
     * @return A map containing the passage itself and a list of indices
     */
    public static Map<String, List<Integer>> findReoccurringPassages(
            String input, int passageLength) {
        Map<String, List<Integer>> res;

        res = IntStream.rangeClosed(0, input.length() - passageLength)
                       .boxed()
                       .collect(Collectors.groupingBy(
                               i -> input.substring(i, i + passageLength)
                       ));

        res.entrySet()
           .removeIf(
                   stringListEntry -> stringListEntry.getValue()
                                                     .size() <= 1
           );
        return res;
    }

    /**
     * <p>
     * Extracts the most frequently found letters of a frequency analysis.
     * </p>
     *
     * <p>
     * Also provides a way to determine a key by shifting the most frequent
     * letters with the letter known to be the most frequent one in the plain
     * text.
     * </p>
     *
     * @param fAInput                    A frequency analysis output of a
     *                                   previous computation
     * @param numberOfReturnedTopLetters Number of most frequent letters of each
     *                                   key position
     * @param keyToShiftWith             Key to shift with. Usually the one
     *                                   which occurs most often in the plain
     *                                   text of an encrypted message.
     * @param clockwiseShiftDirection    Shifting direction. Usually
     *                                   <code>false</code>.
     * @return A List of the most frequent letter for each position. May be
     * shifted to return possible key characters.
     */
    public static List<Map<Character, Long>> getMostFrequentLetters(
            List<Map<Character, Long>> fAInput, int numberOfReturnedTopLetters,
            char keyToShiftWith, boolean clockwiseShiftDirection) {
        return fAInput.stream()
                      .map(Map::entrySet)
                      .map(entries -> entries.stream()
                                             .sorted((o1, o2) ->
                                                     Long.compare(
                                                             o2.getValue(),
                                                             o1.getValue()
                                                     ))
                                             .limit(numberOfReturnedTopLetters)
                                             .map(characterLongEntry ->
                                                     Map.entry(
                                                             shift(
                                                                     characterLongEntry.getKey(),
                                                                     keyToShiftWith,
                                                                     clockwiseShiftDirection
                                                             ),
                                                             characterLongEntry.getValue()
                                                     ))
                                             .collect(
                                                     Collectors.toMap(
                                                             Map.Entry::getKey,
                                                             Map.Entry::getValue
                                                     ))
                      )
                      .collect(Collectors.toList())
                ;
    }
}