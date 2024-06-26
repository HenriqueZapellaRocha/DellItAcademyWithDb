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
        MenuFeatures.clearMenu();
        this.personsBets = new PersonsBets();
        this.anyBet = false;

        // Connect to the database
        System.out.println("Verificando se existe banco e se conectando a ele. Caso não criando um novo");
        this.con = Database.getInstance().gConnection();
        MenuFeatures.clearMenu();

        // If there's an existing database, retrieve the data inserted into it
        System.out.println("Recuperando dados do banco para a memoria");
        ResultSet rs = Database.recoverBetsFromDb(this.con);

        // Check if the query is null
        try {

            if (rs != null) {

                while (rs.next()) {
                    // Retrieve the data returned from the query to memory
                    personsBets.Addbet(
                            new Bet(PersonsBets.stringToBitSet(rs.getString("bet")), rs.getString("cpf"), (byte) 0));
                }
                anyBet = true;
            }
        } catch (SQLException e) {

            System.out.println(e);
        }
        MenuFeatures.clearMenu();

    }

    public void addNewBet() throws SQLException {

        this.addNewPerson = true;
        BitSet numberOfTheBet = new BitSet(5);
        Scanner sc = new Scanner(System.in);
        int number = 0;

        MenuFeatures.clearMenu();

        System.out.print("Cpf da pessoa: ");
        String input = sc.nextLine();

        // check if the CPF is correct as expected for a CPF.
        input = cpfIsCorrect(input, sc);

        // Check if this CPF already exists in the database.
        input = existThisCpfIndb(input);

        // case the cpf doesnt exist in db
        if (addNewPerson == true) {
            System.out.print("Nome da pessoa: " + MenuFeatures.ANSI_RESET);
            String name = sc.nextLine();

            // verify if the name is correct(numbers)
            name = nameIsCorrect(name, sc);

            Database.addPersonRegisterInDb(new Person(name, input), con);
        }

        System.out.println("1-Digitar manualmente os numeros");
        System.out.println("2-Randomizar aposta");
        String selection = sc.nextLine();
        MenuFeatures.clearMenu();

        // manual bets choosen
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
            // generate random bet numbers
        } else if (selection.equals("2")) {

            for (int i = 0; i < 5; i++) {

                numberOfTheBet.set(randomNumberGenerator(numberOfTheBet));
            }

        }

        Byte timesEqual = 0;
        Bet bet = new Bet(numberOfTheBet, input, timesEqual);
        this.personsBets.Addbet(bet);

        Database.addTheBetPerson(con, bet, personsBets);

        // indicate exist a bet in the system
        if (this.anyBet == false) {
            this.anyBet = true;
        }

    }

    public void printPersonsAndBets() {
        MenuFeatures.clearMenu();
        // verufy if a bet exisits
        if (anyBet == false) {

            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                    + "Deve a ver pelo menos uma aposta para ve-las" + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND
                    + "Aperte 'enter' para voltar ao menu principal" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            return;
        }
        // consult the persons and bets
        Database.personsAndBetsRegistered(this.con);
    }

    public void drawExecuter() {
        MenuFeatures.clearMenu();
        // verufy if a bet exisits
        if (anyBet == false) {

            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                    + "Deve existir pelo menos uma aposta para haver sorteio" + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND
                    + "Aperte 'enter' para voltar ao menu principal" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            return;
        }
        // veirfy if the user wants to continues to draw
        System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                + "Tem certeza que deseja executar o sorteio? (digite y/n)" + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND
                + "y continua sorteio. n volta para o menu principal" + MenuFeatures.ANSI_RESET);
        Scanner sc = new Scanner(System.in);
        String verify = sc.nextLine();
        if (!(verify.equals("y"))) {
            return;
        }

        BitSet sortedNumbers = new BitSet();
        Byte drawnNumber;
        int i = 0;
        LinkedList<Winner> winners = new LinkedList<>();
        // execute the draw and compare them with bets
        while (i != 30) {
            System.out.println(i);
            drawnNumber = randomNumberGenerator(sortedNumbers);
            sortedNumbers.set(drawnNumber);
            // if it returns a winner the loop break and go to drawresults
            winners = Drawer.drawnNumber(this.personsBets, drawnNumber, winners);
            if (winners.size() >= 1) {
                break;
            }
            i++;
        }
        // go to de the draw results
        drawResults(winners, sortedNumbers, i);
    }

    // print the draw result
    public void drawResults(LinkedList<Winner> winners, BitSet sortedNumbers, int numberOfRounds) {

        MenuFeatures.clearMenu();
        System.out.println(MenuFeatures.CYAN_BACKGROUND + MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE
                + " RESULTADOS DO SORTEIO " + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_BLACK + MenuFeatures.ANSI_WHITE_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                + "Numeros sorteados: " + sortedNumbers + MenuFeatures.ANSI_RESET);
        System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.YELLOW_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                + "Quantidade de Rodadas: " + (numberOfRounds - 4) + " " + MenuFeatures.ANSI_RESET);

        // verify if exist winners
        if (winners.size() == 0) {
            System.out.println(MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO
                    + "não houveram vencedores!" + MenuFeatures.ANSI_RESET);
        } else {
            System.out.println(MenuFeatures.GREEN_BACKGROUND + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO
                    + "Quantidade de vencedores: " + winners.size() + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO + MenuFeatures.BLUE_BACKGROUND
                    + "Vencedores:" + MenuFeatures.ANSI_RESET);
            Database.Winners(con, winners);

        }

        System.out.println();
        System.out.println(MenuFeatures.GREEN_BACKGROUND + MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE
                + "Aperete 'enter' para ver numeros apostas e quantidade de apostas" + MenuFeatures.ANSI_RESET);
        MenuFeatures.waitingEnter();
        // make a querry in db to search every numbers bets and the quantity this
        // appears in bets
        Database.numbersInBetAndQuan(con);

        // if doesnt exist winner the program finish
        if (winners.size() <= 0) {
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND
                    + "Aperte 'enter' para finalizar o programa" + MenuFeatures.ANSI_RESET);

            MenuFeatures.waitingEnter();
            System.exit(0);
            // go to premiation
        } else {
            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.YELLOW_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                    + "Aperte 'enter' para ver a premiação" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
        }

        // print the award
        if (winners.size() > 0) {
            MenuFeatures.clearMenu();
            int award = Database.awardCalcDb(con);
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.YELLOW_BACKGROUND
                    + "valor do premio: " + award + MenuFeatures.ANSI_RESET);
            System.out.println(MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_NEGRITO + MenuFeatures.CYAN_BACKGROUND
                    + "Cada aposta recebera: " + award / winners.size() + " cerca de "
                    + (award / winners.size() * 100) / award + "% para cada aposta" + MenuFeatures.ANSI_RESET);
            System.out.println();
            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE + MenuFeatures.ANSI_RED_BACKGROUND
                    + "Aperte 'enter' para finalizar o programa" + MenuFeatures.ANSI_RESET);
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
    //Search if the CPF is already present in the database
    private String existThisCpfIndb(String cpf) {

        ResultSet rs = Database.existAPersonWithThisCpf(con, cpf);
        Scanner sc = new Scanner(System.in);
        //If the CPF exists in the database.
        if (rs != null) {
            try {
                while (rs.next()) {
                    //Give the user the option on how to proceed, either by adding a new bet for the person or by changing the CPF and adding a different person.
                    MenuFeatures.clearMenu();
                    System.out.println("Ja existe uma pessoa com este cpf");
                    System.out.println("1-Deseja adicionar uma nova aposta esta pessoa");
                    System.out.println("2-Deseja escrever um cpf diferente");
                    int escolha = sc.nextInt();
                    sc.nextLine();

                    rs = null;
                    //just add a new bet for this person
                    if (escolha == 1) {
                        this.addNewPerson = false;
                        return cpf;
                    } else if (escolha == 2) {
                        // add other new person
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

    //look if the input of cpf by user is correct how a cpf need be
    private String cpfIsCorrect(String cpf, Scanner sc) {

        while (!(cpf.matches("[0-9]+")) || cpf.length() > 11 || cpf.length() < 11) {

            System.out.println(MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_RED_BACKGROUND + MenuFeatures.ANSI_WHITE
                    + "A entrada de cpf desse ser apenas de numeros e deve possuir 11 digitos"
                    + MenuFeatures.ANSI_RESET);
            System.err.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND
                    + "Pressione enter para digitar o cpf novamente" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
            MenuFeatures.clearMenu();
            System.out.print("Cpf da pessoa: ");
            cpf = sc.nextLine();
        }
        return cpf;
    }

    //look if the user input of name have numbers
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
