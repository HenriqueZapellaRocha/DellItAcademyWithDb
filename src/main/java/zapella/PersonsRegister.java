package zapella;

import java.util.HashMap;

public class PersonsRegister {
    


    private HashMap<String,String> personRegister;

    public PersonsRegister() {
        this.personRegister = new HashMap<>();
    }

    
    public void addPerson(String cpf, String nome) {
        this.personRegister.put(cpf, nome);
    }

    public Boolean containKey(String cpf) {
        return personRegister.containsKey(cpf);
    }

    public String getPersonNameWithCpf(String cpf) {
        return this.personRegister.get(cpf);
    }

}
