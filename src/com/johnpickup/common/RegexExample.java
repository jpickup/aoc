package com.johnpickup.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExample {
    public static void main(String[] args) {
        Pattern mulPattern = Pattern.compile(".*\\W([a-z]+)\\(([a-z]+)\\).*");
        String input = "some input(text)";
        Matcher matcher = mulPattern.matcher(input);
        if (matcher.matches()) {
            String p1 = matcher.group(1);
            String p2 = matcher.group(2);
            System.out.println(p1 + " " + p2);  // input text
        }
    }
}
