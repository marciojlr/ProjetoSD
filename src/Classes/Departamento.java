package Classes;

import java.io.Serializable;

public class Departamento implements Serializable {
    private String nome;
    private boolean mesaOn;

    public Departamento(String nome) {
        this.nome = nome;
        this.mesaOn = false;
    }

    public String getNome() {
        return nome;
    }

    public boolean isMesaOn() {
        return mesaOn;
    }

    public void setMesaOn(boolean mesaOn) {
        this.mesaOn = mesaOn;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
