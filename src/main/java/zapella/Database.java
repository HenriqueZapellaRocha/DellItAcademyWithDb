package zapella;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


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
            sql.append(") AND b.bet_id IN ("); // Adiciona a condição para os bet_id
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
            int index = 1; // Inicia o índice do parâmetro em 1
            for (int i = 0; i < winners.size(); i++) {
                pstmt.setString(index++, winners.get(i).getCpf());
            }
            
            // Definir os bet_id como parâmetros
            for (int i = 0; i < winners.size(); i++) {
                pstmt.setInt(index++, winners.get(i).getBet_id()); // Assume que getBetId() retorna um int
            }
            
            // Executar a consulta
            ResultSet rs = pstmt.executeQuery();
            
            // Exibir os resultados
            while (rs.next()) {
                System.out.println("CPF: " + rs.getString("cpf") + ", Nome: " + rs.getString("name") + ", Bet_id: " + rs.getInt("bet_id") + ", Bet: " + rs.getString("bet"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar vencedores: " + e.getMessage());
        }
        // Supondo que MenuFeatures.waitingEnter(); é um método que você tem para pausar a execução.
        MenuFeatures.waitingEnter();
    }









    


    public static void numbersInBetAndQua(Connection con) {
        

        // Consulta para selecionar os dados da tabela
        String query = "SELECT bet FROM bet";

        // Mapa para contar a frequência dos números
        Map<Integer, Integer> numberFrequency = new HashMap<>();

        try (
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Processar cada linha do resultado
            while (rs.next()) {
                // Obter a string de números e dividir
                String[] numbers = rs.getString("bet").split(",");
                
                // Converter para Integer e contar
                for (String numberStr : numbers) {
                    int number = Integer.parseInt(numberStr.trim());
                    numberFrequency.put(number, numberFrequency.getOrDefault(number, 0) + 1);
                }
            }

            for (Map.Entry<Integer, Integer> entry : numberFrequency.entrySet()) {
                Integer number = entry.getKey();
                Integer count = entry.getValue();
                System.out.println(number + " aparece " + count + " vezes");
            }

        } catch (Exception e) {
            System.out.println("Erro ao acessar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    
    }

}
