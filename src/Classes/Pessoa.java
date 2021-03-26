package Classes;

import java.io.Serializable;
import java.util.GregorianCalendar;


public class Pessoa implements Serializable{

    private String nome;
    private String tipo;
    private String password;
    private Departamento departamento;
    private int CC;
    private GregorianCalendar validade_CC;
    private int telemovel;
    private String morada;


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

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPassword() {
        return password;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public int getCC() {
        return CC;
    }

    public GregorianCalendar getValidade_CC() {
        return validade_CC;
    }

    public int getTelemovel() {
        return telemovel;
    }

    public String getMorada() {
        return morada;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public void setCC(int CC) {
        this.CC = CC;
    }

    public void setValidade_CC(GregorianCalendar validade_CC) {
        this.validade_CC = validade_CC;
    }

    public void setTelemovel(int telemovel) {
        this.telemovel = telemovel;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    @Override
    public String toString() {
        return "Nome: " + this.nome + " - Tipo: " + this.tipo + " - CC " + this.CC;
    }
}