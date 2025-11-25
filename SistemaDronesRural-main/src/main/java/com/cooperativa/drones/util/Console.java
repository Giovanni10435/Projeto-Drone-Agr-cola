package com.cooperativa.drones.util;

import java.util.Scanner;

public class Console {
    private static final Scanner SC = new Scanner(System.in);
    public static Scanner scanner() { return SC; }
    public static String readLine(String label) {
        System.out.print(label);
        return SC.nextLine();
    }
    public static String readPassword() {
        java.io.Console cons = System.console();
        if (cons != null) return String.valueOf(cons.readPassword());
        return SC.nextLine();
    }
}