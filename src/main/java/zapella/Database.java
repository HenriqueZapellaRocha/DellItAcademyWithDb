package zapella;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database {

    private Connection connection;
    private static Database INSTANCE;

    public Database() {

        //creates the database if its not exist
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:teste.db");
        } catch (SQLException e) {
            System.err.println("Houve um problema ao inciar o banco de dados");
            e.printStackTrace();
        }

    }

    public Connection gConnection() {
        return this.connection;
    }

    public void cloConnection() {

        try {
            this.connection.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexao com banco de dados");
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        // get a instance of the database
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }

        return INSTANCE;
    }

    // add a person in personRegister table
    public static void addPersonRegisterInDb(Person person, Connection con) {

        try {
            Statement st = con.createStatement();
            //creating table and instering
            st.executeUpdate("CREATE TABLE IF NOT EXISTS personRegister (cpf TEXT UNIQUE PRIMARY KEY, name TEXT)");
            st.executeUpdate(
                    "INSERT INTO personRegister VALUES ('" + person.getCpf() + "','" + person.getName() + "')");
            st.close();

        } catch (SQLException e) {

            System.out.println("CPF já existe na tabela. Nada foi inserido.");
        }
    }

    // add the person bets at bet table
    public static void addTheBetPerson(Connection con, Bet bet, PersonsBets bets) {

        try {
            Statement st = con.createStatement();

            //transforming the bitset to array
            int[] betNummbers = new int[bet.getNumberOfTheBet().cardinality()];
            int index = 0;
            for (int i = bet.getNumberOfTheBet().nextSetBit(0); i >= 0; i = bet.getNumberOfTheBet().nextSetBit(i + 1)) {
                betNummbers[index++] = i;
            }
            //creatng the array string splited by ,
            String numbersString = Arrays.toString(betNummbers).replace("[", "").replace("]", "").replace(" ", "");

            //inserting and creating the table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS bet ("
                    + "bet_id INTEGER PRIMARY KEY,"
                    + "cpf TEXT, "
                    + "bet TEXT, "
                    + "FOREIGN KEY(cpf) REFERENCES personRegister(cpf)"
                    + ")");

            st.executeUpdate("INSERT INTO bet (bet_id, cpf, bet) VALUES ('" + bets.getBetId(bet) + "', '" + bet.getCpf()
                    + "', '" + numbersString + "')");

            st.close();

        } catch (SQLException e) {

            System.err.println(e);
        }
    }

    //querry and print every person and bets at database
    public static void personsAndBetsRegistered(Connection con) {
        try {

            Statement st = con.createStatement();

            
            //querry
            ResultSet rs = st.executeQuery("SELECT pr.name, pr.cpf, b.bet_id, b.bet FROM personRegister pr "
            + "JOIN bet b ON pr.cpf = b.cpf");
            //print the querry
            while (rs.next()) {

                System.out.println("Nome: " + rs.getString("name") + " Cpf: " + rs.getString("cpf") + "\nBetId: "
                        + rs.getInt("bet_id") + " Bet: {" + rs.getString("bet") + "}\n");
            }
            System.err.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND
                    + "Pressione 'enter' para voltar ao menu" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
        } catch (SQLException e) {
            e.printStackTrace();

            MenuFeatures.waitingEnter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //for recover every bet in bet tables. used to get the best from the databse to memory
    public static ResultSet recoverBetsFromDb(Connection con) {

        try {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from bet");
            return rs;
        } catch (SQLException e) {

            return null;
        }

    }

    //querry and print the winners
    public static void Winners(Connection con, LinkedList<Winner> winners) {
        try {
            //Concatenating the query string
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT pr.cpf, pr.name, b.bet_id, b.bet ");
            sql.append("FROM personRegister pr ");
            sql.append("JOIN bet b ON pr.cpf = b.cpf ");
            sql.append("WHERE pr.cpf IN (");
            //puting the cpf in the querry
            for (int i = 0; i < winners.size(); i++) {
                sql.append("?");
                if (i < winners.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(") AND b.bet_id IN (");
            //putting the bet_ids in th querry
            for (int i = 0; i < winners.size(); i++) {
                sql.append("?");
                if (i < winners.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(") ");
            sql.append("ORDER BY pr.name;");

            PreparedStatement pstmt = con.prepareStatement(sql.toString());

            //puting the cpf in the querry
            int index = 1;
            for (int i = 0; i < winners.size(); i++) {
                pstmt.setString(index++, winners.get(i).getCpf());
            }

            //putting the bet_ids in th querry
            for (int i = 0; i < winners.size(); i++) {
                pstmt.setInt(index++, winners.get(i).getBet_id());
            }

            ResultSet rs = pstmt.executeQuery();

            //printing the querry result
            while (rs.next()) {
                System.out.println(
                        "CPF: " + rs.getString("cpf") + ", Nome: " + rs.getString("name") + ", número da aposta: "
                                + rs.getInt("bet_id") + ", Aposta: " + rs.getString("bet"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar vencedores: " + e.getMessage());
        }

    }

    public static void numbersInBetAndQuan(Connection con) {

        Map<Integer, Integer> numberFrequency = new HashMap<>();

        try (
                Statement stmt = con.createStatement();
                
                ResultSet rs = stmt.executeQuery("SELECT bet FROM bet")) {
            //taking the querry results and putting in hahs map
            while (rs.next()) {

                String[] numbers = rs.getString("bet").split(",");

                for (String numberStr : numbers) {
                    int number = Integer.parseInt(numberStr.trim());
                    numberFrequency.put(number, numberFrequency.getOrDefault(number, 0) + 1);
                }
            }

            //matrix of linkedlist
            List<Integer[]> matriz = new LinkedList<>();

            for (Map.Entry<Integer, Integer> entry : numberFrequency.entrySet()) {
                matriz.add(new Integer[] { entry.getKey(), entry.getValue() });
            }

            //sort from the largest to smallest
            Collections.sort(matriz, new Comparator<Integer[]>() {
                public int compare(Integer[] number1, Integer[] number2) {
                    return number2[1] - number1[1];
                }
            });


            // print the head of the table
            MenuFeatures.clearMenu();
            System.out.println(MenuFeatures.CYAN_BACKGROUND + MenuFeatures.ANSI_NEGRITO + MenuFeatures.ANSI_WHITE
                    + " Nro apostado " + MenuFeatures.YELLOW_BACKGROUND + MenuFeatures.ANSI_NEGRITO
                    + MenuFeatures.ANSI_WHITE + " Qtd de apostas " + MenuFeatures.ANSI_RESET);

            //printing the table
            for (Integer[] linha : matriz) {
                System.out.printf("%6d       |   %5d%n", linha[0], linha[1]);
                System.out.println("------------------------------");
            }

        } catch (Exception e) {
            System.out.println("Erro ao acessar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //check if a person whit a epsecific cpf exist return the result of the querry
    public static ResultSet existAPersonWithThisCpf(Connection con, String cpf) {

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT cpf FROM personRegister WHERE cpf = '" + cpf + "'");
            return rs;
        } catch (SQLException e) {

            return null;
        }

    }

    public static int awardCalcDb(Connection con) {

        try {

            // cont lines from personRegister table
            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT COUNT(*) AS lines_count FROM personRegister");

            // taking teh querry results
            int totalLinesFromPersonRegister = 0;
            if (rs1.next()) {
                totalLinesFromPersonRegister = rs1.getInt("lines_count");
            }

            // sum all the bet_id collumns
            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT SUM(bet_id) AS collom_sum FROM bet");

            // taking teh querry results
            int collomSum = 0;
            if (rs2.next()) {
                collomSum = rs2.getInt("collom_sum");
            }

            // cont lines from bet table
            Statement st3 = con.createStatement();
            ResultSet rs3 = st3.executeQuery("SELECT COUNT(*) AS lines_count FROM bet");

            // taking teh querry results
            int totalLinesFromBet = 0;
            if (rs3.next()) {
                totalLinesFromBet = rs1.getInt("lines_count");
            }
            return totalLinesFromPersonRegister * totalLinesFromBet * collomSum;

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return 0;

    }

}
