package Classes;

import java.io.Serializable;

public class Pessoa implements Serializable{
    private String nome;
    private int num_eleitor;
    private String tipo;
    private String password;
    //vai ser classe
    private String Departamento;
    private int tel;
    private String morada;
    private int CC;
    //Date
    private int validade_CC;


    public Pessoa(String nome, int num_eleitor, String tipo, String password, String departamento, int tel, String morada, int CC, int validade_CC) {
        this.nome = nome;
        this.num_eleitor = num_eleitor;
        this.tipo = tipo;
        this.password = password;
        Departamento = departamento;
        this.tel = tel;
        this.morada = morada;
        this.CC = CC;
        this.validade_CC = validade_CC;
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

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public int getTel() { return tel; }

    public void setTel(int tel) { this.tel = tel; }
}
