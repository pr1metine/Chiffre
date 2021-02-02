package de.pr1meti.chiffre;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Vigenere {
    public static final String INPUT =
            "XZNIEITBVRYAKIHESSYGUXDAYFQNNEDWBPPAVRZVIYZVSXNPGBXKIVXMNSIPQIABJTEAWVVYEMAIFVYWIEINNWGEOIZTEMYNFRKSEHRRXXEDEJMENESMZEAYRNKFWGTUIYZIVGVRHRVTKKERFGUERCLGIAWVRARPGZVSGUMPAKEFRJVGUERQEJBIARIMREVNTHRXMVNRBVJXXERFYAWYOPYEAWJKQCJRGUAAVVVFDIXGIYFVEVXEZIWBIEIVMJEMAQFLVNHNHRKCUJGOEBVGSURRNETIEPNPVIXRVTXYTYAHQTNIVUMRKZMPNRQXMIIYIIHIBIEIVMLNKRRGKVFJRRZLJERVGUAFFJRQNGNIVQQVKMEVMIVAVNARRABTHMAHVXJEVXVVMZSGUIAERGIZMPAYEYGIABTHXZMG";

    public static void main(String[] args) {
        for (int i = 3; i <= 100; i++) {
            CryptUtils.findReoccurringPassages(INPUT, i)
                      .forEach((k, v) -> {
                          String res = v.stream()
                                        .map(ii -> "" + ii)
                                        .collect(Collectors.joining(","));
                          System.out.printf("%s has %4d occurrences at %s%n",
                                  k,
                                  v.size(),
                                  res);
                      });
        }

        List<Map<Character, Long>> frequencyAnalysis =
                CryptUtils.frequencyAnalysis(INPUT, 7);
        frequencyAnalysis.forEach(System.out::println);
        List<Map<Character, Long>> mostFrequentLetters =
                CryptUtils.getMostFrequentLetters(frequencyAnalysis,
                        3,
                        'E',
                        false);
        mostFrequentLetters.forEach(System.out::println);
    }

}