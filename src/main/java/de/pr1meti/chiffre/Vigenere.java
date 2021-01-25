package de.pr1meti.chiffre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Vigenere {
    public static final String INPUT = "XZNIEITBVRYAKIHESSYGUXDAYFQNNEDWBPPAVRZVIYZVSXNPGBXKIVXMNSIPQIABJTEAWVVYEMAIFVYWIEINNWGEOIZTEMYNFRKSEHRRXXEDEJMENESMZEAYRNKFWGTUIYZIVGVRHRVTKKERFGUERCLGIAWVRARPGZVSGUMPAKEFRJVGUERQEJBIARIMREVNTHRXMVNRBVJXXERFYAWYOPYEAWJKQCJRGUAAVVVFDIXGIYFVEVXEZIWBIEIVMJEMAQFLVNHNHRKCUJGOEBVGSURRNETIEPNPVIXRVTXYTYAHQTNIVUMRKZMPNRQXMIIYIIHIBIEIVMLNKRRGKVFJRRZLJERVGUAFFJRQNGNIVQQVKMEVMIVAVNARRABTHMAHVXJEVXVVMZSGUIAERGIZMPAYEYGIABTHXZMG";

    public static void main(String[] args) {
        for (int i = 3; i <= 100; i++) {
            findReoccurringPassages(INPUT, i).forEach((k, v) -> {
                if (v.size() > 1) {
                    String res = v.stream()
                                  .map(ii -> "" + ii)
                                  .collect(Collectors.joining(","));
                    System.out.printf("%s has %4d occurrences at %s%n", k, v.size(), res);
                }
            });
        }

        frequencyAnalysis(INPUT, 7)
                .forEach(System.out::println);
    }

    public static HashMap<String, ArrayList<Integer>> findReoccurringPassages(String input, int passageLength) {
        HashMap<String, ArrayList<Integer>> res = new HashMap<>();

        for (int i = 0; i <= input.length() - passageLength; i++) {
            String cur = input.substring(i, i + passageLength);
            res.putIfAbsent(cur, new ArrayList<>());
            res.get(cur)
               .add(i);
        }

        return res;

    }

    public static ArrayList<HashMap<Character, AtomicInteger>> frequencyAnalysis(String input, int keyLength) {
        ArrayList<HashMap<Character, AtomicInteger>> res = new ArrayList<>(keyLength);
        for (int i = 0; i < keyLength; i++) {
            res.add(new HashMap<>());
        }
        for (int i = 0; i < input.length(); i += keyLength) {
            for (int j = 0; j < keyLength; j++) {
                if (i + j >= input.length()) {
                    return res;
                }
                char c = input.charAt(i + j);
                HashMap<Character, AtomicInteger> tmpMap = res.get(j);
                tmpMap.putIfAbsent(c, new AtomicInteger());
                tmpMap.get(c)
                      .incrementAndGet();
            }
        }

        return res;
    }
}
