package zapella;

import java.util.LinkedList;

public class Drawer {


    public  static LinkedList<Winner> drawnNumber(PersonsBets personsBets, byte numberSorted, LinkedList<Winner> winners,PersonsRegister personsRegister ) {
    
        LinkedList<Bet> bets = personsBets.getBets();

        for(Bet bet : bets) {
            if(bet.getNumberOfTheBet().get(numberSorted)) {
                bet.timeEqualIncrease();
                if(bet.getTimesEqual() > 5) {
                    winners.add(new Winner(personsRegister.getPersonNameWithCpf(bet.getCpf()),bet.getCpf(), personsBets.getBetId(bet), bet.getNumberOfTheBet()));
                }
            }
        }

        return winners;
    }
    
}
