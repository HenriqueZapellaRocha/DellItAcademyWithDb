package zapella;

public class Winner {

    private String cpf;
    private int bet_id;

    public Winner(String cpf, int bet_id) {
        this.cpf = cpf;
        this.bet_id = bet_id;
    }

    public String getCpf() {
        return cpf;
    }

    public int getBet_id() {
        return bet_id;
    }

}
