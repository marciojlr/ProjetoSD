package Classes;

import java.io.Serializable;
import java.rmi.Remote;

public class Departamento implements Serializable {
    private String nome;
    private String ip;

    public Departamento(String nome, String ip) {
        this.nome = nome;
        this.ip = ip;
    }

    public String getNome() {
        return nome;
    }

    public String getIp() {
        return ip;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
