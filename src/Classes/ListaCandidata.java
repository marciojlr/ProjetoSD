package Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaCandidata implements Serializable {

    private String nome;
    private int votes;
    //TODO: Lista de candidatos
    public ListaCandidata(String nome) {
        this.nome = nome;
        this.votes = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void addVote(){
        this.votes++;
    }

    public int getVotes() {
        return votes;
    }

    @Override
    public String toString() {
        return "Lista: " + this.nome;
    }

}
