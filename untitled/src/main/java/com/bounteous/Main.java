package com.bounteous;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String my_input = "aaaabbbcddd";
        System.out.println(removeDup(my_input));
    }

    /**
     *
     * @param input
     * @return
     */
    public static  String removeDup(String input) {
        boolean[] chars = new boolean[26];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (!chars[character - 'a']) {
                sb.append(character);
                chars[character - 'a'] = true;
            }
        }
        return sb.toString();
    }
}