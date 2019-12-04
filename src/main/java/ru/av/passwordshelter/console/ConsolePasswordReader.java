package ru.av.passwordshelter.console;

import java.io.Console;
import java.util.Scanner;

class ConsolePasswordReader {

    String readPassword() {
        Console console = System.console();
        if (console != null) {
            char[] pass = System.console().readPassword("%s", "").clone();
            return new String(pass);
        } else {
            System.out.print("(VISIBLE) ");
            return new Scanner(System.in).nextLine();
        }
    }
}