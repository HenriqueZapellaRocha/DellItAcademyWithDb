package zapella;

import java.util.BitSet;

public class Winner {


    private String name;
    private String cpf;
    private int betId;
    private BitSet bet;
    
    public Winner(String name, String cpf, int betId, BitSet bet) {
        this.name = name;
        this.cpf = cpf;
        this.betId = betId;
        this.bet = bet;
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
