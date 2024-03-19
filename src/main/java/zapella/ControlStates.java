package zapella;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Comparator;

public class ControlStates {



    private PersonsBets personsBets;
    private PersonsRegister personsRegister;
    private boolean anyBet;
    private Connection con;


    public ControlStates() {

        this.personsBets = new PersonsBets();
        this.personsRegister = new PersonsRegister();
        this.anyBet = false;
        this.con = Database.getInstance().gConnection();
    }


    public PersonsBets getPersonsBets() {

        return personsBets;
    }


    public PersonsRegister getPersonsRegister() {

        return personsRegister;
    }

    public void addNewBet() throws SQLException {
        
        BitSet numberOfTheBet = new BitSet(5);
        Scanner sc = new Scanner(System.in);
        int number = 0;
        System.out.print(MenuFeatures.ANSI_RED_BACKGROUND +"Nome da pessoa: " + MenuFeatures.ANSI_RESET);
        String nome = sc.nextLine();

        while(!(nome.matches("[a-zA-Z]+"))) {
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_RED_BACKGROUND + "O nome deve conter apenas letras" + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.GREEN_BACKGROUND + "Pressione 'enter' para digitar novamente o nome" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            MenuFeatures.clearMenu();
            System.out.print(MenuFeatures.ANSI_RED_BACKGROUND +"Nome da pessoa: " + MenuFeatures.ANSI_RESET);
            nome = sc.nextLine();
        }

        MenuFeatures.clearMenu();

        System.out.print("Cpf da pessoa: ");
        String input = sc.nextLine();

        while (!(input.matches("[0-9]+")) || input.length() > 11 || input.length() < 11) {
            
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_WHITE + "A entrada de cpf desse ser apenas de numeros e deve ter 11 digitos" + MenuFeatures.ANSI_RESET);
            System.err.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND + "Pressione enter para digitar o cpf novamente" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            MenuFeatures.clearMenu();
            System.out.print("Cpf da pessoa: ");
            input = sc.nextLine();
        }

    
            while ((personsRegister.containKey(input)) && !(personsRegister.getPersonNameWithCpf(input).equals(nome))) {
                System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_WHITE + "Este cpf digitado esta registrado em outro nome" + MenuFeatures.ANSI_RESET);
                System.err.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND + "Pressione enter para digitar o cpf novamente" + MenuFeatures.ANSI_RESET);
                MenuFeatures.waitingEnter();
                MenuFeatures.clearMenu();
                System.out.print("Cpf da pessoa: ");
                input = sc.nextLine();
            }
        
            Database.addPersonRegisterInDb(new Person(nome, input), con);
            

        System.out.println("1-Digitar manualmente os numeros");
        System.out.println("2-Randomizar aposta");
        String selection = sc.nextLine();
        MenuFeatures.clearMenu();
        if(selection.equals("1")) {
        for(int i =1; i < 6; i++) {
            System.out.print(i +"º number: ");
            number = sc.nextInt();
            while(number > 50 || number < 1 ||numberOfTheBet.get(number) == true ) {
                if (number > 50 || number < 1) {
                    
                System.out.print("O numero precisa ser entre 1 a 50. Digite novamente: ");
                number = sc.nextInt();
                }
                if(numberOfTheBet.get(number)) {

                    System.out.print("Não e possivel apostar em dois numeros iguais em uma mesma aposta. Digite novamente um numero diferente: ");
                    number = sc.nextInt();
                }
                }
            
                numberOfTheBet.set(number);
                MenuFeatures.clearMenu();
            }
    }
        else if(selection.equals("2")) {
        
            for(int i =0; i < 5; i++) {

                numberOfTheBet.set(randomNumberGenerator(numberOfTheBet));
            }

        }


        Byte timesEqual = 0;
        Bet bet = new Bet(numberOfTheBet, selection, timesEqual);
        this.personsBets.Addbet(bet);
        this.personsRegister.addPerson(input, nome);

        if (this.anyBet == false) {
            this.anyBet = true;
        }

        Database.addTheBetPerson(con,bet, personsBets);

    }

    public void printPersonsAndBets() {
        MenuFeatures.clearMenu();
         LinkedList<Bet> bets = personsBets.getBets();
        for(Bet bet : bets) {

            System.out.println("Nome: " + personsRegister.getPersonNameWithCpf(bet.getCpf()) + " Cpf: " + personsBets.getCpf(bet) + "\nBetId: " +  personsBets.getBetId(bet) + " Bet: " + personsBets.getBet(bet));
        }
        System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.GREEN_BACKGROUND + "Aperte 'enter' para voltar ao menu" + MenuFeatures.ANSI_RESET);
        MenuFeatures.waitingEnter();

    }

    public void drawExecuter() {

        if(this.anyBet == false) {
            System.out.println(MenuFeatures.ANSI_RED_BACKGROUND + "E necessario a existencia de pelo menos uma aposta para o sorteio ocorrer!");
            return;
        } else {

            BitSet sortedNumbers = new BitSet();
            Byte drawnNumber;
            int i =0;
            LinkedList<Winner> winners = new LinkedList<>();

            while (i != 30) {
                drawnNumber = randomNumberGenerator(sortedNumbers);
                sortedNumbers.set(drawnNumber);
                winners =  Drawer.drawnNumber(this.personsBets, drawnNumber,winners, personsRegister);
                if(winners.size() >=1 ) {
                    break;
                }
                i++;
            }


            drawResults(winners, sortedNumbers,i);
        }

    }

    public void drawResults(LinkedList<Winner> winners, BitSet sortedNumbers, int numberOfRounds) {

        MenuFeatures.clearMenu();

        System.out.println(sortedNumbers);
        System.out.println(numberOfRounds);
        System.out.println(winners.size());
        if(winners.size() == 0) {
            System.out.println("não houveram vencedores!");
        } else {
            Collections.sort(winners, new Comparator<Winner>() {
                public int compare(Winner winner1, Winner winner2) {
                    return winner1.getName().compareTo(winner2.getName());
                }
            });
            for(Winner win : winners) {
                System.out.println("Nome: " + win.getName() + "Cpf: " + win.getCpf() + "BetId" + win.getBetId() + "Bet: " + win.getBet());
            }
        }


        System.out.println("Pressione enter para ver as premiações");
        MenuFeatures.waitingEnter();

        } 
        
        
        









        



    private byte randomNumberGenerator(BitSet sortedNumbers) {
     
        Random random = new Random();
        Byte sorted;
        sorted = (byte) (random.nextInt(49) + 1);

        while (sortedNumbers.get(sorted)) {
            sorted = (byte) (random.nextInt(49) + 1);
       }

       return sorted;
        
    }


    public boolean isAnyBet() {
        return anyBet;
    }

}
