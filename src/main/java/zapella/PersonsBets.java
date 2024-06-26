package zapella;

import java.util.BitSet;
import java.util.LinkedList;

public class PersonsBets {

    private LinkedList<Bet> bets;

    public PersonsBets() {
        this.bets = new LinkedList<>();
    }

    public void Addbet(Bet bet) {

        bets.add(bet);

    }

    public LinkedList<Bet> getBets() {

        return bets;
    }

    public int getIndex(Bet bet) {
        return this.bets.indexOf(bet);
    }

    public int getBetId(Bet bet) {

        return 1000 + getIndex(bet);
    }

    public String getCpf(Bet bet) {

        return bet.getCpf();
    }

    public BitSet getBet(Bet bet) {

        return bet.getNumberOfTheBet();
    }

    public static BitSet stringToBitSet(String set) {

        BitSet betSet = new BitSet();
        String[] numbers = set.split(",");

        for (int i = 0; i <= 4; i++) {

            betSet.set(Integer.parseInt(numbers[i]));

        }

        return betSet;
    }

}