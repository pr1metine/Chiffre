package de.pr1meti.chiffre;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Vignere {
    public static final String INPUT="XZNIEITBVRYAKIHESSYGUXDAYFQNNEDWBPPAVRZVIYZVSXNPGBXKIVXMNSIPQIABJTEAWVVYEMAIFVYWIEINNWGEOIZTEMYNFRKSEHRRXXEDEJMENESMZEAYRNKFWGTUIYZIVGVRHRVTKKERFGUERCLGIAWVRARPGZVSGUMPAKEFRJVGUERQEJBIARIMREVNTHRXMVNRBVJXXERFYAWYOPYEAWJKQCJRGUAAVVVFDIXGIYFVEVXEZIWBIEIVMJEMAQFLVNHNHRKCUJGOEBVGSURRNETIEPNPVIXRVTXYTYAHQTNIVUMRKZMPNRQXMIIYIIHIBIEIVMLNKRRGKVFJRRZLJERVGUAFFJRQNGNIVQQVKMEVMIVAVNARRABTHMAHVXJEVXVVMZSGUIAERGIZMPAYEYGIABTHXZMG";
    public static void main(String[] args) {
        // System.out.println("Hello World");
        // System.out.println(INPUT);
        // System.out.println("Begin execution");
        for (int i = 3; i <= 100; i++) {
            findAndPrintOccurences(i);
        }
    }

    public static void findAndPrintOccurences(int stringLength) {
        HashMap<String, ArrayList<Integer>> map = new HashMap<>();

        for (int i = 0; i < INPUT.length() - stringLength; i++) {
            String cur = INPUT.substring(i, i + stringLength);

            map.putIfAbsent(cur, new ArrayList<Integer>());


            map.get(cur).add(i);
        }

        map.forEach((k, v) -> { 
            if (v.size() > 1) {
                String res = v.stream().map(i -> "" + i).collect(Collectors.joining(","));
                System.out.printf("%s has %4d occurences at %s%n", k, v.size(), res) ;
            }
        });
    }
}
