package Classes;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Eleicao implements Serializable {
    //usar Date
    private GregorianCalendar data_inicio;
    private GregorianCalendar data_final;
    private String titulo;
    private String descricao;
    //So podem votar pessoas deste tipo
    private String tipo_Pessoa;
    // LISTAS
    private ArrayList<ListaCandidata> listaCandidata;
    private ArrayList<Departamento> dept;

    private ArrayList<Pessoa> votantes;

    public Eleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, Departamento dept, String tipo_Pessoa) {
        this.data_inicio = data_inicio;
        this.data_final = data_final;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dept = new ArrayList<Departamento>();
        this.dept.add(dept);
        this.tipo_Pessoa = tipo_Pessoa;
        this.listaCandidata= new ArrayList<ListaCandidata>();
        this.votantes = new ArrayList<>();
    }

    public GregorianCalendar getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(GregorianCalendar data_inicio) {
        this.data_inicio = data_inicio;
    }

    public GregorianCalendar getData_final() {
        return data_final;
    }

    public void setData_final(GregorianCalendar data_final) {
        this.data_final = data_final;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ArrayList<Departamento> getDept() {
        return dept;
    }

    public ArrayList<Pessoa> getVotantes() {
        return votantes;
    }

    public void setVotantes(ArrayList<Pessoa> votantes) {
        this.votantes = votantes;
    }


    public void setDept(Departamento dept) {
        this.dept.add(dept);
    }

    public String getTipo_Pessoa() {
        return tipo_Pessoa;
    }

    public void setTipo_Pessoa(String tipo_Pessoa) {
        this.tipo_Pessoa = tipo_Pessoa;
    }

    public ArrayList<ListaCandidata> getListaCandidata() {
        return listaCandidata;
    }

    public void setListaCandidata(ArrayList<ListaCandidata> listaCandidata) {
        this.listaCandidata = listaCandidata;
    }

    public void addListaCandidata(String nome){
        ListaCandidata l = new ListaCandidata(nome);
        this.listaCandidata.add(l);
    }

    public void removeListaCandidata(String nome){

        for (ListaCandidata l: this.listaCandidata) {
            if(nome.equals(l.getNome())){
                this.listaCandidata.remove(l);
                return;
            }
        }
    }

    public void removeDepartamento(Departamento d){

        for(Departamento d2: this.getDept()){
            if(d2.getNome().equals(d.getNome())){
                this.getDept().remove(d2);
                return;
            }
        }
    }

    public void printLista(){
        for(ListaCandidata l: this.listaCandidata){
            System.out.println(l.toString());
        }
    }

    @Override
    public String toString(){
        return  "Nova eleição criada\n" +
                "Titulo: " + this.titulo + "\n" +
                "Descrição: " + this.descricao + "\n" +
                "Departamentos: " + this.dept + "\n" +
                "Data de inicio: " + this.data_inicio.DAY_OF_MONTH + "/" + this.data_inicio.MONTH + "/" + this.data_inicio.YEAR + "\n" +
                "Data de encerramento: " + this.data_inicio.DAY_OF_MONTH + "/" + this.data_inicio.MONTH + "/" + this.data_inicio.YEAR
                ;
    }

}
