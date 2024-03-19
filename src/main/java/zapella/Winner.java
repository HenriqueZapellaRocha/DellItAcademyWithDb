package zapella;

import java.util.BitSet;

public class Winner {


    private String name;
    private String cpf;
    private int betId;
    private BitSet bet;
    
    public Winner(String cpf) {
    
        this.cpf = cpf;
        
    }

    public String getName() {
        return name;
    }


    public int getBetId() {
        return betId;
    }

    public BitSet getBet() {
        return bet;
    }

    public String getCpf() {
        return cpf;
    }
    
    
    
}
