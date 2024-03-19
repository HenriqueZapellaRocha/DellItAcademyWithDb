package zapella;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Database {

    private Connection connection;
    private static Database INSTANCE;

    public Database() {
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

        if (INSTANCE == null) {
            INSTANCE = new Database();
        }

        return INSTANCE;
    }

    public static void addPersonRegisterInDb(Person person, Connection con) {

        try {
            Statement st = con.createStatement();

            st.executeUpdate("CREATE TABLE IF NOT EXISTS personRegister (cpf TEXT UNIQUE PRIMARY KEY, name TEXT)");
            st.executeUpdate(
                    "INSERT INTO personRegister VALUES ('" + person.getCpf() + "','" + person.getName() + "')");
            st.close();

        } catch (SQLException e) {

            System.out.println("CPF já existe na tabela. Nada foi inserido.");
        }
    }

    public static void addTheBetPerson(Connection con, Bet bet, PersonsBets bets) {

        try {
            Statement st = con.createStatement();

            int[] betNummbers = new int[bet.getNumberOfTheBet().cardinality()];
            int index = 0;
            for (int i = bet.getNumberOfTheBet().nextSetBit(0); i >= 0; i = bet.getNumberOfTheBet().nextSetBit(i + 1)) {
                betNummbers[index++] = i;
            }

            String numbersString = Arrays.toString(betNummbers).replace("[", "").replace("]", "").replace(" ", "");

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

    public static void personsAndBetsRegistered(Connection con) {
        try {

            Statement st = con.createStatement();

            
            String query = "SELECT pr.name, pr.cpf, b.bet_id, b.bet FROM personRegister pr "
                    + "JOIN bet b ON pr.cpf = b.cpf";

            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                
                System.out.println("Nome: " + rs.getString("name") + " Cpf: " + rs.getString("cpf") + "\nBetId: " + rs.getInt("bet_id") + " Bet: {" + rs.getString("bet") + "}\n");
            }
            System.err.println(MenuFeatures.ANSI_WHITE + MenuFeatures.GREEN_BACKGROUND + "Pressione 'enter' para voltar ao menu" + MenuFeatures.ANSI_RESET);
            MenuFeatures.waitingEnter();
        } catch (SQLException e) {
            e.printStackTrace();

            MenuFeatures.waitingEnter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static  ResultSet recoverBetsFromDb(Connection con) {

      
        try {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from bet");
            return rs;
        } catch (SQLException e) {

           
           return null;
        } 
      
    }

    public static void Winners(Connection con, LinkedList<Winner> winners) {
        try {
         
         
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT pr.cpf, pr.name, b.bet_id, b.bet ");
            sql.append("FROM personRegister pr ");
            sql.append("JOIN bet b ON pr.cpf = b.cpf ");
            sql.append("WHERE pr.cpf IN (");
            for (int i = 0; i < winners.size(); i++) {
                sql.append("?");
                if (i < winners.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(") ");
            sql.append("ORDER BY pr.name;");
            PreparedStatement pstmt = con.prepareStatement(sql.toString());
            
            // Definir os CPFs como parâmetros
            for (int i = 0; i < winners.size(); i++) {
                pstmt.setString(i + 1, winners.get(i).getCpf());
            }
            
            // Executar a consulta
            ResultSet rs = pstmt.executeQuery();
            
            // Exibir os resultados
            while (rs.next()) {
                String cpf = rs.getString("cpf");
                String name = rs.getString("name");
                System.out.println("CPF: " + cpf + ", Nome: " + name);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar pessoas por CPF: " + e.getMessage());
        }
        MenuFeatures.waitingEnter();
    }
}
