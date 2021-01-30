package de.pr1meti.chiffre;

import java.util.List;
import java.util.Map;
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

        frequencyAnalysis(INPUT, 7)
                .forEach(System.out::println);
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
}
