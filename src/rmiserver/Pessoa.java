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
    private boolean admin;
    private boolean on;
    private String id_face = null;

    public Pessoa(String nome, String tipo, String password, Departamento departamento, int CC, GregorianCalendar CC_val,int telemovel, String morada, boolean admin) {
        this.nome = nome;
        this.tipo = tipo;
        this.password = password;
        this.departamento = departamento;
        this.CC = CC;
        this.validade_CC = CC_val;
        this.telemovel = telemovel;
        this.morada = morada;
        this.admin = admin;
        this.on = false;
    }

    public Pessoa(String nome, int CC, String localVoto) {
        this.nome = nome;
        this.CC = CC;
        this.localVoto = localVoto;
    }

    public String getLocalVoto() {
        return localVoto;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
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

    public boolean getAdmin(){
        return this.admin;
    }

    public String getId_face() { return id_face; }

    public void setId_face(String id_face) { this.id_face = id_face; }

    @Override
    public String toString() {
        return "Nome: " + this.nome + "; Tipo: " + this.tipo + "; CC: " + this.CC + "; Departamento: " + this.departamento + "; Admin: " + this.admin;
    }
}