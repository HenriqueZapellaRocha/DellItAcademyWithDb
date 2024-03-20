package zapella;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;





public class ControlStates {

    private PersonsBets personsBets;
    private boolean anyBet;
    private Connection con;
    Boolean addNewPerson;

    public ControlStates() {

        this.personsBets = new PersonsBets();
        this.anyBet = false;
        System.out.println("Verificando se existe banco e se conectando a ele. Caso não criando um novo");
        this.con = Database.getInstance().gConnection();
        MenuFeatures.clearMenu();
        System.out.println("Recuperando dados do banco para a memoria");

        ResultSet rs = Database.recoverBetsFromDb(this.con);

        try {

            if (rs != null) {

                while (rs.next()) {
                    personsBets.Addbet(
                            new Bet(PersonsBets.stringToBitSet(rs.getString("bet")), rs.getString("cpf"), (byte) 0));
                }
                anyBet = true;
            }
        } catch (SQLException e) {

            System.out.println("Erro ao recuperar as apostas do banco de dados");
        }
        MenuFeatures.clearMenu();

    }

    public PersonsBets getPersonsBets() {

        return personsBets;
    }



    public void addNewBet() throws SQLException {
        this.addNewPerson = true;
        BitSet numberOfTheBet = new BitSet(5);
        Scanner sc = new Scanner(System.in);
        int number = 0;

        MenuFeatures.clearMenu();

        System.out.print("Cpf da pessoa: ");
        String input = sc.nextLine();

        input = cpfIsCorrect(input, sc);

        input = existThisCpfIndb(input);
    
        if(addNewPerson == true) {
        System.out.print("Nome da pessoa: " + MenuFeatures.ANSI_RESET);
        String name = sc.nextLine();

     
        name = nameIsCorrect(name, sc);
      

    
        Database.addPersonRegisterInDb(new Person(name, input), con);
        }


        System.out.println("1-Digitar manualmente os numeros");
        System.out.println("2-Randomizar aposta");
        String selection = sc.nextLine();
        MenuFeatures.clearMenu();
        if (selection.equals("1")) {
            for (int i = 1; i < 6; i++) {
                System.out.print(i + "º number: ");
                number = sc.nextInt();
                while (number > 50 || number < 1 || numberOfTheBet.get(number) == true) {
                    if (number > 50 || number < 1) {

                        System.out.print("O numero precisa ser entre 1 a 50. Digite novamente: ");
                        number = sc.nextInt();
                    }
                    if (numberOfTheBet.get(number)) {

                        System.out.print(
                                "Não e possivel apostar em dois numeros iguais em uma mesma aposta. Digite novamente um numero diferente: ");
                        number = sc.nextInt();
                    }
                }

                numberOfTheBet.set(number);
                MenuFeatures.clearMenu();
            }
        } else if (selection.equals("2")) {

            for (int i = 0; i < 5; i++) {

                numberOfTheBet.set(randomNumberGenerator(numberOfTheBet));
            }

        }

        Byte timesEqual = 0;
        Bet bet = new Bet(numberOfTheBet, input, timesEqual);
        this.personsBets.Addbet(bet);
   
        Database.addTheBetPerson(con, bet, personsBets);

        if (this.anyBet == false) {
            this.anyBet = true;
        }

    }

    public void printPersonsAndBets() {
        MenuFeatures.clearMenu();

        Database.personsAndBetsRegistered(this.con);
    }

    public void drawExecuter() {

        if (this.anyBet == false) {
            System.out.println(MenuFeatures.ANSI_RED_BACKGROUND
                    + "E necessario a existencia de pelo menos uma aposta para o sorteio ocorrer!");
            return;
        } else {

            BitSet sortedNumbers = new BitSet();
            Byte drawnNumber;
            int i = 0;
            LinkedList<Winner> winners = new LinkedList<>();

            while (i != 30) {
                System.out.println(i);
                drawnNumber = randomNumberGenerator(sortedNumbers);
                sortedNumbers.set(drawnNumber);
                winners = Drawer.drawnNumber(this.personsBets, drawnNumber, winners);
                if (winners.size() >= 1) {
                    break;
                }
                i++;
            }

            drawResults(winners, sortedNumbers, i);
        }

    }

    public void drawResults(LinkedList<Winner> winners, BitSet sortedNumbers, int numberOfRounds) {

        MenuFeatures.clearMenu();
        System.out.println(MenuFeatures.CYAN_BACKGROUND + MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + " RESULTADOS DO SORTEIO " + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_BLACK + MenuFeatures.ANSI_WHITE_BACKGROUND + MenuFeatures.ANSI_NEGRITO + "Numeros sorteados: " + sortedNumbers + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.YELLOW_BACKGROUND + MenuFeatures.ANSI_NEGRITO + "Quantidade de Rodadas: " + numberOfRounds + " "  + MenuFeatures.ANSI_RESET);
        
        if (winners.size() == 0) {
            System.out.println(MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO + "não houveram vencedores!" + MenuFeatures.ANSI_RESET);
        } else {
            System.out.println(MenuFeatures.GREEN_BACKGROUND + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO + "Quantidade de vencedores: " + winners.size() + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO + MenuFeatures.BLUE_BACKGROUND + "Vencedores:" + MenuFeatures.ANSI_RESET);
            Database.Winners(con, winners);

        }

        System.out.println();
        System.out.println(MenuFeatures.GREEN_BACKGROUND + MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + "Aperete 'enter' para ver numeros apostas e quantidade de apostas" + MenuFeatures.ANSI_RESET);
        MenuFeatures.waitingEnter();
        Database.numbersInBetAndQuan(con);

        if(winners.size() <= 0) {
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND + "Aperte 'enter' para finalizar o programa" + MenuFeatures.ANSI_RESET);
        
        MenuFeatures.waitingEnter();
        System.exit(0);
        } else {
            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.YELLOW_BACKGROUND + MenuFeatures.ANSI_NEGRITO + "Aperte 'enter' para ver a premiação" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter(); 
        }
      
        if(winners.size() > 0) {

            int award = Database.awardCalcDb(con);
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.YELLOW_BACKGROUND + "valor do premio: " + award + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO + MenuFeatures.CYAN_BACKGROUND + "Cada aposta recebera: " + award/winners.size() + " cerca de " + (award/winners.size() * 100) / award +  "% para cada aposta" + MenuFeatures.ANSI_RESET);
            System.out.println();
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND + "Aperte 'enter' para finalizar o programa" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            System.exit(0);
        }
      
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

    private String existThisCpfIndb(String cpf) {

        ResultSet rs = Database.existAPersonWithThisCpf(con, cpf);
        Scanner sc = new Scanner(System.in);
        if(rs != null) {
        try {
            while (rs.next()) { 
                MenuFeatures.clearMenu();
                System.out.println("Ja existe uma pessoa com este cpf");
                System.out.println("1-Deseja adicionar uma nova aposta esta pessoa");
                System.out.println("2-Deseja escrever um cpf diferente");
                int escolha = sc.nextInt();
                sc.nextLine();

                rs = null;

                if(escolha == 1) {
                    this.addNewPerson = false;
                    return cpf;
                } else if (escolha == 2) {
                    MenuFeatures.clearMenu();
                    System.out.print("Cpf da pessoa: ");
                    cpf = sc.nextLine();
                    rs = Database.existAPersonWithThisCpf(con, cpf);
                }
            }
        } catch (SQLException e) {
       
        }
    }
        return cpf;

    }

    private String cpfIsCorrect(String cpf, Scanner sc) {
 
        while (!(cpf.matches("[0-9]+")) || cpf.length() > 11 || cpf.length() < 11) {

            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_WHITE
                    + "A entrada de cpf desse ser apenas de numeros e deve possuir 11 digitos" + MenuFeatures.ANSI_RESET);
            System.err.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND
                    + "Pressione enter para digitar o cpf novamente" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            MenuFeatures.clearMenu();
            System.out.print("Cpf da pessoa: ");
            cpf = sc.nextLine();
        }
        return cpf;
    }

    private String nameIsCorrect(String name, Scanner sc) {

        name = name.trim();
        name = name.replaceAll("\\s", "");
        while (!(name.matches("[a-zA-Z\\\\p{L}áéíóúâêîôûàèìòùãẽĩõũ]+"))) {
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_RED_BACKGROUND
                    + "O nome deve conter apenas letras" + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.GREEN_BACKGROUND
                    + "Pressione 'enter' para digitar novamente o nome" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            MenuFeatures.clearMenu();
            System.out.print(MenuFeatures.ANSI_RED_BACKGROUND + "Nome da pessoa: " + MenuFeatures.ANSI_RESET);
            name = sc.nextLine();
        }
        return name;
    }

}
