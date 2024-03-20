package zapella;

import java.util.Scanner;

public class MenuFeatures {

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_ROXO = "\u001B[35m";
    public static final String ANSI_NEGRITO = "\u001B[1m";
    public static final String ANSI_BLACK = "\u001B[30m";

    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";

    public static void waitingEnter() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input == "") {
                break;
            }
        }
    }

    public static void clearMenu() {
        System.out.print("\u001B[H\u001B[2J");
    }

}
