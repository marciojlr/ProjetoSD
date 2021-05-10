package rmiserver;

import java.io.Serializable;

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
