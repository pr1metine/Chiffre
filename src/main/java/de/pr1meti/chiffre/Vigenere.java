package de.pr1meti.chiffre;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Vigenere {
    public static final String INPUT = "XZNIEITBVRYAKIHESSYGUXDAYFQNNEDWBPPAVRZVIYZVSXNPGBXKIVXMNSIPQIABJTEAWVVYEMAIFVYWIEINNWGEOIZTEMYNFRKSEHRRXXEDEJMENESMZEAYRNKFWGTUIYZIVGVRHRVTKKERFGUERCLGIAWVRARPGZVSGUMPAKEFRJVGUERQEJBIARIMREVNTHRXMVNRBVJXXERFYAWYOPYEAWJKQCJRGUAAVVVFDIXGIYFVEVXEZIWBIEIVMJEMAQFLVNHNHRKCUJGOEBVGSURRNETIEPNPVIXRVTXYTYAHQTNIVUMRKZMPNRQXMIIYIIHIBIEIVMLNKRRGKVFJRRZLJERVGUAFFJRQNGNIVQQVKMEVMIVAVNARRABTHMAHVXJEVXVVMZSGUIAERGIZMPAYEYGIABTHXZMG";

    public static void main(String[] args) {
        for (int i = 3; i <= 100; i++) {
            findReoccurringPassages(INPUT, i).forEach((k, v) -> {
                String res = v.stream()
                              .map(ii -> "" + ii)
                              .collect(Collectors.joining(","));
                System.out.printf("%s has %4d occurrences at %s%n", k, v.size(), res);
            });
        }

        List<Map<Character, Long>> frequencyAnalysis = frequencyAnalysis(INPUT, 7);
        frequencyAnalysis.forEach(System.out::println);
        List<Map<Character, Long>> mostFrequentLetters = getMostFrequentLetters(frequencyAnalysis, 3, 'E', false);
        mostFrequentLetters.forEach(System.out::println);
    }

    public static Map<String, List<Integer>> findReoccurringPassages(String input, int passageLength) {
        Map<String, List<Integer>> res;
        res = IntStream.rangeClosed(0, input.length() - passageLength)
                       .boxed()
                       .collect(Collectors.groupingBy(i -> input.substring(i, i + passageLength)));
        res.entrySet()
           .removeIf(stringListEntry -> stringListEntry.getValue()
                                                       .size() <= 1);
        return res;
    }

    public static List<Map<Character, Long>> frequencyAnalysis(String input, int keyLength) {
        return IntStream.range(0, keyLength)
                        .mapToObj(i -> IntStream.iterate(i, ii -> ii < input.length(), ii -> ii + keyLength)
                                                .mapToObj(input::charAt)
                                                .collect(Collectors.groupingBy(c -> c, Collectors.counting())))
                        .collect(Collectors.toUnmodifiableList())
                ;
    }

    public static char shift(char toBeShifted, char key, boolean clockwiseShiftDirection) {
        int toBeShiftedInt = Character.toUpperCase(toBeShifted) - 'A';
        int keyInt = Character.toUpperCase(key) - 'A';
        int range = 'Z' - 'A' + 1;
        return (char) ('A' + (toBeShiftedInt + (clockwiseShiftDirection ? keyInt : -keyInt) + range) % range);
    }

    public static List<Map<Character, Long>> getMostFrequentLetters(List<Map<Character, Long>> fAInput, int numberOfReturnedTopLetters, char keyToShiftWith, boolean clockwiseShiftDirection) {
        Comparator<Map.Entry<Character, Long>> comparator;
        comparator = Comparator.comparingLong((ToLongFunction<Map.Entry<Character, Long>>) Map.Entry::getValue)
                               .reversed();
        return fAInput.stream()
                      .map(Map::entrySet)
                      .map(entries -> entries.stream()
                                             .sorted(comparator)
                                             .limit(numberOfReturnedTopLetters)
                                             .map(characterLongEntry ->
                                                     Map.entry(
                                                             shift(characterLongEntry.getKey(), keyToShiftWith, clockwiseShiftDirection),
                                                             characterLongEntry.getValue()
                                                     ))
                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                      .collect(Collectors.toList())
                ;
    }
}
