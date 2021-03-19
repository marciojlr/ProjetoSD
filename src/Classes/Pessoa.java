package Classes;

import java.io.Serializable;

public class Pessoa implements Serializable{
    private String nome;
    private int num_eleitor;
    private String tipo;

    public Pessoa(String nome, int num_eleitor, String tipo) {
        this.nome = nome;
        this.num_eleitor = num_eleitor;
        this.tipo= tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNum_eleitor() {
        return num_eleitor;
    }

    public void setNum_eleitor(int num_eleitor) {
        this.num_eleitor = num_eleitor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
