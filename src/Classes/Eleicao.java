package Classes;

public class Eleicao {
    //usar Date
    private int data_inicio;
    private int data_final;
    private String titulo;
    private String Descricao;
    //Classe departamento
    private String dept;
    //So podem votar pessoas deste tipo
    private String tipo_Pessoa;

    public Eleicao(int data_inicio, int data_final, String titulo, String descricao, String dept, String tipo_Pessoa) {
        this.data_inicio = data_inicio;
        this.data_final = data_final;
        this.titulo = titulo;
        Descricao = descricao;
        this.dept = dept;
        this.tipo_Pessoa = tipo_Pessoa;
    }

    public int getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(int data_inicio) {
        this.data_inicio = data_inicio;
    }

    public int getData_final() {
        return data_final;
    }

    public void setData_final(int data_final) {
        this.data_final = data_final;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTipo_Pessoa() {
        return tipo_Pessoa;
    }

    public void setTipo_Pessoa(String tipo_Pessoa) {
        this.tipo_Pessoa = tipo_Pessoa;
    }
}
