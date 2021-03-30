package Classes;

import java.io.Serializable;
import java.rmi.Remote;

public class Departamento implements Serializable {
    private String nome;

    public Departamento(String nomes) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
