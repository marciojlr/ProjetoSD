package rmiserver;

import java.io.Serializable;
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
    //VOTOS
    private int total_votos;
    private int votos_branco;
    private int votos_nulos;
    private ArrayList<String> realTime;

    public Eleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, Departamento dept, String tipo_Pessoa) {
        this.data_inicio = data_inicio;
        this.data_final = data_final;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dept = new ArrayList<>();
        this.dept.add(dept);
        this.tipo_Pessoa = tipo_Pessoa;
        this.listaCandidata= new ArrayList<>();
        this.votantes = new ArrayList<>();
        this.total_votos = 0;
        this.votos_branco = 0;
        this.votos_nulos = 0;
        this.realTime = new ArrayList<>();
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

    public void addTotalVotos(){
        this.total_votos++;
    }

    public void addVotoBranco(){
        this.votos_branco++;
    }

    public void addVotoNulo(){
        this.votos_nulos++;
    }

    public String getTipo_Pessoa() {
        return tipo_Pessoa;
    }

    public ArrayList<ListaCandidata> getListaCandidata() {
        return listaCandidata;
    }

    public ArrayList<String> getRealTime() {
        return realTime;
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
        int i= 1;
        for(ListaCandidata l: this.listaCandidata){
            System.out.println(i+ ". " +l.toString());
            i++;
        }
    }

    @Override
    public String toString(){
        return  "Titulo: " + this.titulo  + "; Descrição: " + this.descricao + "; Departamentos: " + this.dept +
                "; Data de inicio: " + this.data_inicio.get(5) + "/" + (this.data_inicio.get(2)+1) + "/" + this.data_inicio.get(1) +
                " " + this.data_inicio.get(Calendar.HOUR_OF_DAY) + ":" + this.data_inicio.get(Calendar.MINUTE) +
                "; Data de encerramento: " + this.data_final.get(5) + "/" + (this.data_final.get(2)+1) + "/" + this.data_final.get(1) +
                " " + this.data_final.get(Calendar.HOUR_OF_DAY) + ":" + this.data_final.get(Calendar.MINUTE)
                ;
    }

    public String resultadosOnline(){
        String out = "Total de votos ate ao momento: " + this.total_votos + "\n";
        return out;
    }

    public String resultados(){
        String out;
        out = "\n- - - Resultados da Eleicao - - -\n";
        out += "Total de votos: " + this.total_votos + "\n";
        out += "Titulo: " + getTitulo()+"\n";
        try{
            float perVotosBrancos = ((float)votos_branco/total_votos)*100;

            for(ListaCandidata lc: this.listaCandidata){
                float per = ((float)lc.getVotes()/total_votos)*100;
                out += "Lista: " + lc.getNome() +" " +"Numero de Votos: " + lc.getVotes() + "\t"+per +"%\n";
            }
            out += "Votos Brancos: " + votos_branco + "\t" + perVotosBrancos + "%\n";
            out += "Votos Nulos: " + votos_nulos + "\t" + ((float)votos_nulos/(total_votos+votos_nulos))*100 +"%\n";
        }catch (Exception e){
            out = "(!) Ninguem votou nesta eleiçao\n";
        }

        return out;

    }

    public void addRealTime(String str){
        if(!this.realTime.contains(str))
            this.realTime.add(str);
    }

    public void removeRealTime(String str){
        for(String elector : this.realTime){
            if(elector.equals(str)){
                realTime.remove(elector);
                return;
            }
        }
    }

}
