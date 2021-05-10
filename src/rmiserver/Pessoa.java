package rmiserver;

import java.io.Serializable;
import java.util.GregorianCalendar;


public class Pessoa implements Serializable{

    private final String nome;
    private String tipo;
    private String password;
    private Departamento departamento;
    private final int CC;
    private GregorianCalendar validade_CC;
    private int telemovel;
    private String morada;
    private String localVoto;

    public Pessoa(String nome, String tipo, String password, Departamento departamento, int CC, GregorianCalendar CC_val,int telemovel, String morada) {
        this.nome = nome;
        this.tipo = tipo;
        this.password = password;
        this.departamento = departamento;
        this.CC = CC;
        this.validade_CC = CC_val;
        this.telemovel = telemovel;
        this.morada = morada;
    }

    public Pessoa(String nome, int CC, String localVoto) {
        this.nome = nome;
        this.CC = CC;
        this.localVoto = localVoto;
    }

    public String getLocalVoto() {
        return localVoto;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPassword() {
        return password;
    }

    public int getCC() {
        return CC;
    }

    @Override
    public String toString() {
        return "Nome: " + this.nome + "; Tipo: " + this.tipo + "; CC: " + this.CC + "; Departamento: " + this.departamento;
    }
}