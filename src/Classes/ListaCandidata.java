package Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaCandidata implements Serializable {

    private String nome;

    //TODO: Lista de candidatos
    public ListaCandidata(String nome) {
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
        return "ListaCandidata{" +
                "nome='" + nome + '\'' +
                '}';
    }

}
