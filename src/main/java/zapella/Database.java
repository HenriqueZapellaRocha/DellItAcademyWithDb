package zapella;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;


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

   public static void addPersonRegisterInDb(Person person, Connection con)  {
    
       
        try {
            Statement st = con.createStatement();

            st.executeUpdate("CREATE TABLE IF NOT EXISTS personRegister (cpf TEXT UNIQUE, name TEXT)");
            st.executeUpdate("INSERT INTO personRegister VALUES ('" + person.getCpf() + "','" + person.getName() + "')");
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

        String numbersString = Arrays.toString(betNummbers)
                                     .replace("[", "")
                                     .replace("]", "")
                                     .replace(" ", "");


        st.executeUpdate("CREATE TABLE IF NOT EXISTS bet ("
                + "bet_id INTEGER PRIMARY KEY,"
                + "cpf TEXT, "
                + "bet TEXT, "
                + "FOREIGN KEY(cpf) REFERENCES personRegister(cpf)"
                + ")");

        st.executeUpdate("INSERT INTO bet (bet_id, cpf, bet) VALUES ('" + bets.getBetId(bet) + "', '" + bet.getCpf() + "', '" + numbersString + "')");
            

    
        
        st.close();

    } catch (SQLException e) {
      
      System.out.println("CPF já existe na tabela. Nada foi inserido.");
    }
   }



}
