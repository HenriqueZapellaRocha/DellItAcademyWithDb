package zapella;

import java.util.BitSet;

public class Bet {


    private BitSet numberOfTheBet;
    private String cpf;
    private byte timesEqual;
    
    public Bet(BitSet numberOfTheBet, String cpf, byte timesEqual) {
        this.numberOfTheBet = numberOfTheBet;
        this.cpf = cpf;
        this.timesEqual = 0;
    }

    public BitSet getNumberOfTheBet() {
        return numberOfTheBet;
    }


    public int getTimesEqual() {
        return timesEqual;
    }

    public void timeEqualIncrease() {
        this.timesEqual++;
    }

    public String getCpf() {
        return cpf;
    }
    

}
