package zapella;

import java.util.Scanner;


public class App {
    public static void main(String[] args)  {
        
        ControlStates programControl = new ControlStates();
        Scanner scanner = new Scanner(System.in);
        

        MenuFeatures.clearMenu();

        System.out.println("TESTE DE CORES PARA VER SE ESTA FUNCIONANDO");
        System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.CYAN_BACKGROUND +  "Este Codigo foi criado por henrique Zapella Rocha para o processo seletvio Dell It academy fase desafio" + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND +  "Presionee enter para continuar o programa" + MenuFeatures.ANSI_RESET);
            
        MenuFeatures.waitingEnter();
        MenuFeatures.clearMenu();
        while (true) {
            
        
        System.out.println(MenuFeatures.CYAN_BACKGROUND + "         MENU INICIAL         "+ MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_WHITE_BACKGROUND +  "                              " + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.GREEN_BACKGROUND+  MenuFeatures.ANSI_NEGRITO +"     1-Adicionar aposta       " + MenuFeatures.ANSI_RESET );
        System.out.println(MenuFeatures.ANSI_WHITE_BACKGROUND + "                              " + MenuFeatures.ANSI_RESET);
        if(programControl.isAnyBet() == false) {
            System.out.println(MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_NEGRITO +" 2-Listar apostas registradas " + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_WHITE_BACKGROUND + "                              " + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_NEGRITO +"   3-Iniciar fase do sorteio  " + MenuFeatures.ANSI_RESET);
        } else{
            System.out.println(MenuFeatures.GREEN_BACKGROUND + MenuFeatures.ANSI_NEGRITO +" 2-Listar apostas registradas " + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_WHITE_BACKGROUND + "                              " + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.GREEN_BACKGROUND+  MenuFeatures.ANSI_NEGRITO +"   3-Iniciar fase do sorteio  " + MenuFeatures.ANSI_RESET);
        }
        int option;

        option = scanner.nextInt();
        
        if(option == 1) {
            programControl.addNewBet();
        } else if(option == 2) {
            programControl.printPersonsAndBets();
        } else if(option == 3) {
            programControl.drawExecuter();
        }

        MenuFeatures.clearMenu();
    }
    }
    }

