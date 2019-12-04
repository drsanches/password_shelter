package ru.av.passwordshelter.console;

import ru.av.passwordshelter.core.AES256;
import ru.av.passwordshelter.core.Crypt;
import ru.av.passwordshelter.core.CryptException;
import ru.av.passwordshelter.core.FileWorker;
import ru.av.passwordshelter.core.JsonManager;
import ru.av.passwordshelter.core.ManagerException;
import ru.av.passwordshelter.core.TextFileWorker;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsolePasswordShelter {

    private static String filename = null;
    private static Crypt crypt = new AES256();
    private static FileWorker fileWorker = new TextFileWorker();

    public static void main(String[] args) {
        if (args.length == 1) {
            select(args);
        }
        welcome();
        help();
        menu();
    }

    private static void menu() {
        while (true) {
            System.out.print(">>");
            String[] commands = new Scanner(System.in).nextLine().split(" ");
            if (commands.length > 0) {
                String[] args = Arrays.copyOfRange(commands, 1, commands.length);
                switch (commands[0]) {
                    case "create":
                        create(args);
                        break;
                    case "encrypt":
                        encrypt(args);
                        break;
                    case "decrypt":
                        decrypt(args);
                        break;
                    case "select":
                        select(args);
                        break;
                    case "help":
                        help();
                        break;
                    case "exit":
                        System.out.println("Bye");
                        System.exit(0);
                        break;
                    case "sources":
                        if (fileSet()) {
                            sources(args);
                            break;
                        }
                    case "all-accounts":
                        if (fileSet()) {
                            allAccounts(args);
                            break;
                        }
                    case "accounts":
                        if (fileSet()) {
                            accounts(args);
                            break;
                        }
                    case "password":
                        if (fileSet()) {
                            password(args);
                            break;
                        }
                    case "add":
                        if (fileSet()) {
                            add(args);
                            break;
                        }
                    case "change":
                        if (fileSet()) {
                            change(args);
                            break;
                        }
                    case "delete":
                        if (fileSet()) {
                            delete(args);
                            break;
                        }
                    case "change-master":
                        if (fileSet()) {
                            changeMaster(args);
                            break;
                        }
                    case "":
                        break;
                    default:
                        wrongCommandError();
                        break;
                }
            }
        }
    }

    private static boolean fileSet() {
        return filename != null;
    }

    private static void help() {
        final String FORMAT = "%-20s%-25s%s%n";
        System.out.println("----------------------------------------------------------------------");
        System.out.println("                P A S S W O R D   S H E L T E R    1.0                ");
        System.out.println("----------------------------------------------------------------------");
        if (fileSet()) {
            System.out.println("Filename: " + filename + "");
        } else {
            System.out.println("Select file or create new.");
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf(FORMAT, "COMMAND", "OPTIONS", "DESCRIPTION");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf(FORMAT, "create", "[filename]", "- creates file");
        System.out.printf(FORMAT, "encrypt", "[input] [output]", "- encrypts input file and writes it to output");
        System.out.printf(FORMAT, "decrypt", "[input] [output]", "- decrypts input file and writes it to output");
        System.out.printf(FORMAT, "select", "[filename]", "- selects existent file");
        if (filename != null) {
            System.out.printf(FORMAT, "sources", "", "- shows source list");
            System.out.printf(FORMAT, "all-accounts", "", "- shows all accounts map");
            System.out.printf(FORMAT, "accounts", "[source]", "- shows account list");
            System.out.printf(FORMAT, "password", "[source] [account]", "- copies password to clipboard");
            System.out.printf(FORMAT, "add", "[source] [account]", "- adds new account");
            System.out.printf(FORMAT, "change", "[source] [account]", "- changes account password");
            System.out.printf(FORMAT, "delete", "[source] [account]", "- deletes account");
            System.out.printf(FORMAT, "change-master", "", "- changes master password");
        }
        System.out.printf(FORMAT, "help", "", "- shows this help");
        System.out.printf(FORMAT, "exit", "", "- exit");
        System.out.println("----------------------------------------------------------------------");
    }

    private static void welcome() {
        System.out.println("Welcome to the");
        System.out.println(
                " ____                                     _   ____  _          _ _              _   ___  \n" +
                "|  _ \\ __ _ ___ _____      _____  _ __ __| | / ___|| |__   ___| | |_ ___ _ __  / | / _ \\ \n" +
                "| |_) / _` / __/ __\\ \\ /\\ / / _ \\| '__/ _` | \\___ \\| '_ \\ / _ \\ | __/ _ \\ '__| | || | | |\n" +
                "|  __/ (_| \\__ \\__ \\\\ V  V / (_) | | | (_| |  ___) | | | |  __/ | ||  __/ |    | || |_| |\n" +
                "|_|   \\__,_|___/___/ \\_/\\_/ \\___/|_|  \\__,_| |____/|_| |_|\\___|_|\\__\\___|_|    |_(_)___/"
        );
        System.out.println("                                                         created by Alexander Voroshilov\n");
    }

    private static void wrongCommandError() {
        System.out.println("Wrong command. Use 'help' to to view help.");
    }

    private static void create(String... args) {
        String tmp;
        switch (args.length) {
            case 0:
                System.out.print("Filename: ");
                tmp = new Scanner(System.in).nextLine();
                break;
            case 1:
                tmp = args[0];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (tmp.contains(" ")) {
            System.out.println("Do not use spaces in filename.");
            return;
        }
        try {
            System.out.print("Enter master password: ");
            String masterPassword1 = new ConsolePasswordReader().readPassword();
            System.out.print("Repeat master password: ");
            String masterPassword2 = new ConsolePasswordReader().readPassword();
            if (masterPassword1.equals(masterPassword2)) {
                new JsonManager(tmp).createFile(masterPassword1);
                filename = tmp;
                System.out.println("File \"" + filename + "\" was created");
            } else {
                System.out.println("Passwords do not match!");
            }
        } catch (IOException e) {
            System.out.println("Wrong filename!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        }
    }

    private static void encrypt(String... args) {
        String input;
        String output;
        switch (args.length) {
            case 0:
                System.out.print("Input filename: ");
                input = new Scanner(System.in).nextLine();
                System.out.print("Output filename: ");
                output = new Scanner(System.in).nextLine();
                break;
            case 2:
                input = args[0];
                output = args[1];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (input.contains(" ") || output.contains(" ")) {
            System.out.println("Do not use spaces in filename.");
            return;
        }
        try {
            System.out.print("Enter password: ");
            String password1 = new ConsolePasswordReader().readPassword();
            System.out.print("Repeat password: ");
            String password2 = new ConsolePasswordReader().readPassword();
            if (password1.equals(password2)) {
                fileWorker.write(crypt.encrypt(fileWorker.read(input), password1), output);
                System.out.println("File \"" + input + "\" was encrypted and wrote to \"" + output + "\".");
            } else {
                System.out.println("Passwords do not match!");
            }
        } catch (IOException e) {
            System.out.println("Read / write error!");
        } catch (CryptException e) {
            System.out.println("Wrong password!");
        }
    }

    private static void decrypt(String... args) {
        String input;
        String output;
        switch (args.length) {
            case 0:
                System.out.print("Input filename: ");
                input = new Scanner(System.in).nextLine();
                System.out.print("Output filename: ");
                output = new Scanner(System.in).nextLine();
                break;
            case 2:
                input = args[0];
                output = args[1];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (input.contains(" ") || output.contains(" ")) {
            System.out.println("Do not use spaces in filename.");
            return;
        }
        try {
            System.out.print("Enter password: ");
            String password1 = new ConsolePasswordReader().readPassword();
            System.out.print("Repeat password: ");
            String password2 = new ConsolePasswordReader().readPassword();
            if (password1.equals(password2)) {
                fileWorker.write(crypt.decrypt(fileWorker.read(input), password1), output);
                System.out.println("File \"" + input + "\" was encrypted and wrote to \"" + output + "\".");
            } else {
                System.out.println("Passwords do not match!");
            }
        } catch (IOException e) {
            System.out.println("Read / write error!");
        } catch (CryptException e) {
            System.out.println("Wrong password!");
        }
    }

    private static void select(String... args) {
        String tmp;
        switch (args.length) {
            case 0:
                System.out.print("Filename: ");
                tmp = new Scanner(System.in).nextLine();
                break;
            case 1:
                tmp = args[0];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (tmp.contains(" ")) {
            System.out.println("Do not use spaces in file path!");
            return;
        }
        if (!fileWorker.exists(tmp)) {
            System.out.println("File \"" + tmp + "\" does not exist!");
            return;
        }
        filename = tmp;
        System.out.println("Filename was selected");
    }

    private static void sources(String... args) {
        if (args.length != 0) {
            wrongCommandError();
            return;
        }
        System.out.print("Master password: ");
        String masterPassword = new ConsolePasswordReader().readPassword();
        try {
            List<String> sources = new JsonManager(filename).getSourceList(masterPassword);
            sources.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("Wrong file structure!");
        }
    }

    private static void allAccounts(String... args) {
        if (args.length != 0) {
            wrongCommandError();
            return;
        }
        System.out.print("Master password: ");
        String masterPassword = new ConsolePasswordReader().readPassword();
        try {
            Map<String, List<String>> map = new JsonManager(filename).getAllAccountsMap(masterPassword);
            map.forEach((k, v) -> {
                System.out.println(k + ": ");
                v.forEach(a -> {
                    System.out.println("    " + a);
                });
            });
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("Wrong file structure!");
        }
    }

    private static void accounts(String... args) {
        String source;
        switch (args.length) {
            case 0:
                System.out.print("Source: ");
                source = new Scanner(System.in).nextLine();
                break;
            case 1:
                source = args[0];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (source.contains(" ")) {
            System.out.println("Do not use spaces in sources.");
            return;
        }
        try {
            System.out.print("Master password: ");
            String masterPassword = new ConsolePasswordReader().readPassword();
            List<String> accounts = new JsonManager(filename).getAccountList(source, masterPassword);
            accounts.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("No record!");
        }
    }

    private static void password(String... args) {
        String source;
        String account;
        switch (args.length) {
            case 0:
                System.out.print("Source: ");
                source = new Scanner(System.in).nextLine();
                System.out.print("Account: ");
                account = new Scanner(System.in).nextLine();
                break;
            case 2:
                source = args[0];
                account = args[1];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (source.contains(" ")) {
            System.out.println("Do not use spaces in sources.");
            return;
        }
        if (account.contains(" ")) {
            System.out.println("Do not use spaces in accounts.");
            return;
        }
        try {
            System.out.print("Master password: ");
            String masterPassword = new ConsolePasswordReader().readPassword();
            String password = new JsonManager(filename).getPassword(source, account, masterPassword);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(password), null);
            System.out.println("Password was copied to clipboard");
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("No record!");
        }
    }

    private static void add(String... args) {
        String source;
        String account;
        switch (args.length) {
            case 0:
                System.out.print("Source: ");
                source = new Scanner(System.in).nextLine();
                System.out.print("Account: ");
                account = new Scanner(System.in).nextLine();
                break;
            case 2:
                source = args[0];
                account = args[1];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (source.contains(" ")) {
            System.out.println("Do not use spaces in sources.");
            return;
        }
        if (account.contains(" ")) {
            System.out.println("Do not use spaces in accounts.");
            return;
        }
        System.out.print("Enter account password: ");
        String password1 = new ConsolePasswordReader().readPassword();
        System.out.print("Repeat account password: ");
        String password2 = new ConsolePasswordReader().readPassword();
        if (!password1.equals(password2)) {
            System.out.println("Passwords do not match!");
            return;
        }
        System.out.print("Master password: ");
        String masterPassword = new ConsolePasswordReader().readPassword();
        try {
            new JsonManager(filename).add(source, account, password1, masterPassword);
            System.out.println("New account was added");
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("Account already exists!");
        }
    }

    private static void change(String... args) {
        String source;
        String account;
        switch (args.length) {
            case 0:
                System.out.print("Source: ");
                source = new Scanner(System.in).nextLine();
                System.out.print("Account: ");
                account = new Scanner(System.in).nextLine();
                break;
            case 2:
                source = args[0];
                account = args[1];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (source.contains(" ")) {
            System.out.println("Do not use spaces in sources.");
            return;
        }
        if (account.contains(" ")) {
            System.out.println("Do not use spaces in accounts.");
            return;
        }
        System.out.print("Enter new password: ");
        String password1 = new ConsolePasswordReader().readPassword();
        System.out.print("Repeat new password: ");
        String password2 = new ConsolePasswordReader().readPassword();
        if (!password1.equals(password2)) {
            System.out.println("Passwords do not match!");
            return;
        }
        System.out.print("Master password: ");
        String masterPassword = new ConsolePasswordReader().readPassword();
        try {
            new JsonManager(filename).changePassword(source, account, password1, masterPassword);
            System.out.println("Password was changed");
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("No record!");
        }
    }

    private static void delete(String... args) {
        String source;
        String account;
        switch (args.length) {
            case 0:
                System.out.print("Source: ");
                source = new Scanner(System.in).nextLine();
                System.out.print("Account: ");
                account = new Scanner(System.in).nextLine();
                break;
            case 2:
                source = args[0];
                account = args[1];
                break;
            default:
                wrongCommandError();
                return;
        }
        if (source.contains(" ")) {
            System.out.println("Do not use spaces in sources.");
            return;
        }
        if (account.contains(" ")) {
            System.out.println("Do not use spaces in accounts.");
            return;
        }
        System.out.print("Master password: ");
        String masterPassword = new ConsolePasswordReader().readPassword();
        try {
            new JsonManager(filename).delete(source, account, masterPassword);
            System.out.println("Account was deleted");
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        } catch (ManagerException e) {
            System.out.println("No record!");
        }
    }

    private static void changeMaster(String... args) {
        if (args.length != 0) {
            wrongCommandError();
            return;
        }
        System.out.print("Old master password: ");
        String oldMasterPassword = new ConsolePasswordReader().readPassword();
        System.out.print("Enter new master password: ");
        String newMasterPassword1 = new ConsolePasswordReader().readPassword();
        System.out.print("Repeat new master password: ");
        String newMasterPassword2 = new ConsolePasswordReader().readPassword();
        if (!newMasterPassword1.equals(newMasterPassword2)) {
            System.out.println("Passwords do not match!");
            return;
        }
        try {
            new JsonManager(filename).changeMasterPassword(oldMasterPassword, newMasterPassword1);
            System.out.println("Master password was changed");
        } catch (IOException e) {
            System.out.println("File access problems!");
        } catch (CryptException e) {
            System.out.println("Wrong master password!");
        }
    }
}