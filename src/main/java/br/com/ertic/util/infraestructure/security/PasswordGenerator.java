package br.com.ertic.util.infraestructure.security;

import java.util.Random;


public final class PasswordGenerator {

    private static char[] chars = new char[] {
        'a', 'b', 'c', 'd', 'e', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v',
        'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '_'
    };

    private PasswordGenerator() {
    }

    public static String generate(int size) {

        String out = "";
        Random rn = new Random();
        for(int i = 1; i <= size; i++) {
            out += Character.toString(chars[rn.nextInt(chars.length)]);
        }
        return out;

    }

}
