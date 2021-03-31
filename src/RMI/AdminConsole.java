package RMI;

import Classes.Departamento;
import Classes.Eleicao;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

// Script de Cliente diretamente ligado ao Server
public class AdminConsole extends UnicastRemoteObject implements RMI_C_I {

    private static RMI_S_I adminConsole;
    private static boolean notifications;

    public AdminConsole() throws RemoteException {
        super();
        notifications= false;
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
        adminConsole = (RMI_S_I) Naming.lookup("Server");
        RMI_C_I client = new AdminConsole();
        String teste;
        teste = adminConsole.teste((RMI_C_I) client);
        System.out.println(teste);
        Departamento dei = new Departamento("DEI");
        Departamento deec = new Departamento("DEEC");
        GregorianCalendar datainicio = new GregorianCalendar(2021, Calendar.MARCH,26);
        GregorianCalendar datafim = new GregorianCalendar(2021, Calendar.MARCH,30);
        adminConsole.AddDepartamento(dei);
        adminConsole.AddDepartamento(deec);
        adminConsole.registarPessoa("Marcio","Estudante", "123", dei, 12345678, null,910,"Coimbra");
        adminConsole.registarPessoa("Filipe","Estudante", "123", deec, 123456789, null,910,"Coimbra");

        adminConsole.criarEleicao(datainicio,datafim,"Eleicao 1", "Descricao 1", dei, "Estudante");
        adminConsole.criarEleicao(datainicio,datafim,"Eleicao 2", "Descricao 2", deec, "Estudante");

        menu();

    }

    public static void menu(){
        Scanner myObj = new Scanner(System.in);
        String option;
        while (true){
            System.out.println("====== Bem vindo! ======");
            System.out.println("1. Registar");
            System.out.println("2. Criar Eleição");
            System.out.println("3. Gerir Listas de candidatos a uma eleição");
            System.out.println("4. Gerir Mesas de Voto");
            System.out.println("5. Alterar propriedades de eleição");
            System.out.println("6. Saber local de voto de um dado eleitor");
            System.out.println("7. Estado das mesas de voto / Votos em tempo real");
            System.out.println("14. Consultar eleiçoes passadas");
            System.out.print("> ");
            option= myObj.nextLine();
            switch (option) {
                case "1":
                    RegistoPessoa();
                    break;
                case "2":
                    criaEleicao();
                    break;
                case "3":
                    gerirListaCandidata();
                    break;
                case "4":
                    GerirMesa();
                    break;
                case "5":
                    //catch para a trhead
                    try {
                        AlteraPropriedadesEleicao();
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    break;
                case "6":
                    LocaisDeVoto();
                    break;
                case "7":
                    notifications();
                    break;
                case "14":
                    ConsultarEleicoesPassadas();
                    break;
                default:
                    System.out.println("opçao invalida");
            }
        }
    }

    public static void  RegistoPessoa(){

        Scanner s = new Scanner(System.in);
        System.out.println("\n- - - - REGISTO DE ELEITOR- - - -\n");
        System.out.print("Nome: ");
        String nome = s.nextLine();

        String tipo;
        //TODO: ACHO QUE ISTO TAMBEM PODE SER UMA FUNÇAO
        while(true){
            System.out.print("1. Docente\n2. Estudante\n3. Funcionário\n");
            System.out.print("> ");
            try {
                int aux = Integer.parseInt(s.nextLine());
                if (aux == 1) {
                    tipo = "Docente";
                    break;
                } else if (aux == 2) {
                    tipo = "Estudante";
                    break;
                } else if (aux == 3) {
                    tipo = "Funcionario";
                    break;
                }
                else{
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
            }catch (NumberFormatException e ){
                System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
            }
        }

        System.out.print("Password: ");
        String password = s.nextLine();

        //TODO: Posso fazer a verificaçao se o departamento é novo no metodo de registar do rmi
        Departamento departamento= null;
        while (departamento == null){
            departamento = escolherDept();
            if(departamento == null){
                departamento = criaDepartamento();
                if(departamento !=null){
                    try{
                        adminConsole.AddDepartamento(departamento);
                    }catch (RemoteException e){
                        while (true){
                            try{
                                adminConsole = (RMI_S_I) Naming.lookup("Server");
                                adminConsole.AddDepartamento(departamento);
                                break;
                            }catch (NotBoundException  | RemoteException | MalformedURLException m){
                                System.out.println("nao conectei");
                            }
                        }
                    }
                }
            }
        }

        System.out.print("CC: ");
        int CC=Integer.parseInt(s.nextLine());
        System.out.println("Validade CC");
        System.out.print("Mês: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        GregorianCalendar CC_val = new GregorianCalendar(ano,mes-1,1);
        System.out.print("Telemovel: ");
        int telemovel =Integer.parseInt(s.nextLine());

        System.out.print("Morada: ");
        String morada = s.nextLine();

        //chamar funçao registar do server
        try{
            boolean result;
            result = adminConsole.registarPessoa(nome, tipo, password, departamento, CC, CC_val, telemovel, morada);
            if(result){
                System.out.println("\nELEITOR REGISTADO COM SUCESSO!\n");
            }
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.registarPessoa(nome, tipo, password, departamento, CC, CC_val, telemovel, morada);
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    private static void setHour(GregorianCalendar date){
        Scanner s = new Scanner(System.in);
        System.out.println("HORA (HH:MM)");
        System.out.print("> ");
        String input = s.nextLine();

        try {
            int hour = Integer.parseInt(input.split(":")[0]);
            int minute = Integer.parseInt(input.split(":")[1]);
            date.set(Calendar.HOUR_OF_DAY, hour);
            date.set(Calendar.MINUTE,minute);
        }catch(Exception e){
            System.out.println("POR FAVOR INSIRA UM FORMATO DE DATA VÁLIDO");
            setHour(date);
        }
    }

    public static void  criaEleicao(){
        //RECOLHER INFORMAÇÃO
        Scanner s = new Scanner(System.in);
        System.out.println("\n- - - - REGISTO DE ELEIÇÃO- - - -\n");
        //TODO: Criar funçao para pedir data
        System.out.println("DATA DE INICIO");
        System.out.print("Dia: ");
        int dia = Integer.parseInt(s.nextLine());
        System.out.print("Mês: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        GregorianCalendar data_inicio = new GregorianCalendar(ano,mes-1,dia);
        setHour(data_inicio);

        System.out.println("DATA DE ENCERRAMENTO");
        System.out.print("Dia: ");
        int dia2 = Integer.parseInt(s.nextLine());
        System.out.print("Mês: ");
        int mes2 = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano2 = Integer.parseInt(s.nextLine());

        GregorianCalendar data_fim = new GregorianCalendar(ano2,mes2-1,dia2);
        setHour(data_fim);

        System.out.println("Titulo: ");
        String titulo= s.nextLine();

        System.out.println("Descrição: ");
        String descricao= s.nextLine();

        Departamento departamento= null;
        while (departamento == null){
            departamento = escolherDept();
            if(departamento == null){
                departamento = criaDepartamento();
                if(departamento !=null){
                    try{
                        adminConsole.AddDepartamento(departamento);
                    }catch (RemoteException e){
                        while (true){
                            try{
                                adminConsole = (RMI_S_I) Naming.lookup("Server");
                                adminConsole.AddDepartamento(departamento);
                                break;
                            }catch (NotBoundException  | RemoteException | MalformedURLException m){
                                System.out.println("nao conectei");
                            }
                        }
                    }
                }

            }
        }

        //So podem votar pessoas deste tipo
        System.out.println("GRUPO DE PESSOAS QUE PODEM ESTUDAR");
        String tipo_Pessoa;
        while(true){
            System.out.print("1. Docentes\n2. Estudantes\n3. Funcionários\n");
            System.out.print("> ");
            int aux = Integer.parseInt(s.nextLine());
            if(aux == 1){
                tipo_Pessoa = "Docente";
                break;
            }
            else if(aux == 2){
                tipo_Pessoa = "Estudante";
                break;
            }
            else if(aux == 3){
                tipo_Pessoa = "Funcionario";
                break;
            }
        }
        try{
            String r;
            r = adminConsole.criarEleicao(data_inicio, data_fim , titulo, descricao, departamento, tipo_Pessoa);
            System.out.println(r);
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.criarEleicao(data_inicio, data_fim, titulo, descricao, departamento, tipo_Pessoa);
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public static void gerirListaCandidata(){
        //TODO: Adicionar os membros na parte de criar

        ArrayList<Eleicao> elegiveis;
        try {
            elegiveis = adminConsole.getEleicoesElegiveis();
        }catch (RemoteException e) {
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    elegiveis = adminConsole.getEleicoesElegiveis();
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }

        }

        if(elegiveis.isEmpty()){
            System.out.println("Nao ha eleiçoes");
            return;
        }
        //Posso fazer uma funçao para o print
        int i= 1;
        for( Eleicao e : elegiveis){
            System.out.println(i + ". " + e.getTitulo());
            i++;
        }

        Scanner s = new Scanner(System.in);
        int eleicao=-1;
        boolean valido = false;
        while (!valido){
            try {
                System.out.println("Insira a eleiçao que pretende: ");
                System.out.print("> ");
                eleicao = Integer.parseInt(s.nextLine());
                if (eleicao < i && eleicao > 0) {
                    valido = true;
                }
                else {
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
            }catch (NumberFormatException e){
                System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
            }
        }

        System.out.println("1-Adicionar  2-Remover");
        String opcao="";
        valido = false;

        while (!valido){
            try {
                System.out.print("> ");
                opcao = s.nextLine();
                if (opcao.equals("1") || opcao.equals("2")) {
                    valido = true;
                }
                else {
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
            }catch (Exception e){
                System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
            }
        }

        //TODO: FAZER UMA FUNÇAO PARA O ADICIONAR
        if(opcao.equals("1")){

            System.out.println("Insira Lista Candidata que deseja inserir: ");
            System.out.print("> ");
            String nome = s.nextLine();
            try{
                adminConsole.AddListaCandidata(elegiveis.get(eleicao-1),nome);
            }catch (RemoteException e){
                while (true){
                    try {
                        //Thread.sleep(1000);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        adminConsole.AddListaCandidata(elegiveis.get(eleicao-1),nome);
                        break;
                    }catch(NotBoundException  | RemoteException | MalformedURLException m){
                        System.out.println("nao conectei");
                    }
                }
            }
        }
        //TODO: FAZER UMA FUNÇAO PARA O REMOVER
        else if(opcao.equals("2")){
            if(elegiveis.get(eleicao-1).getListaCandidata().isEmpty()){
                System.out.println("Nao ha listas para remover");
                return;
            }

            //print listas candidatas
            elegiveis.get(eleicao-1).printLista();
            System.out.println("Insira a Lista Candidata que deseja remover:");
            int nome=0;
            valido= false;
            while (!valido) {
                try {
                    System.out.print("> ");
                    nome = Integer.parseInt(s.nextLine());
                    if (nome <= elegiveis.get(eleicao - 1).getListaCandidata().size() && nome > 0) {
                        valido = true;
                    } else {
                        System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                    }

                }catch (Exception e ){
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
            }

            try {
                adminConsole.RemoveListaCandidata(elegiveis.get(eleicao-1),elegiveis.get(eleicao-1).getListaCandidata().get(nome-1).getNome());
            }catch (RemoteException e){
                while (true){
                    try {
                        //Thread.sleep(1000);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        adminConsole.RemoveListaCandidata(elegiveis.get(eleicao-1),elegiveis.get(eleicao-1).getListaCandidata().get(nome-1).getNome());
                        break;
                    }catch(NotBoundException  | RemoteException | MalformedURLException m){
                        System.out.println("nao conectei");
                    }
                }
            }

        }

    }


    public static void AlteraPropriedadesEleicao(){

        ArrayList<Eleicao> elegiveis;
        try {
            elegiveis = adminConsole.getEleicoesElegiveis();
        }catch (RemoteException e) {
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    elegiveis = adminConsole.getEleicoesElegiveis();
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }

        if(elegiveis.isEmpty()) {
            System.out.println("Nao ha eleicoes");
            return;
        }

        int i =0;
        for (Eleicao e: elegiveis) {
            System.out.println(e.getTitulo());
            i++;
        }

        Scanner s = new Scanner(System.in);
        boolean valido = false;
        String escolha="";
        while (!valido){
            System.out.println("Selecione a Eleição que deseja alterar");
            escolha = s.nextLine();
            for(Eleicao e1: elegiveis){
                if (escolha.equals(e1.getTitulo())) {
                    valido = true;
                    break;
                }
            }
            if(!valido){
                System.out.println("Opçao Invalida\nCertifique-se que escolhe uma opçao valida\n");
            }
        }

        //todo: datas e -1 no mes;

        System.out.println("Data de inicio: ");
        int data_inicio = Integer.parseInt(s.nextLine());

        System.out.println("Data de final: ");
        int data_final = Integer.parseInt(s.nextLine());

        System.out.println("Titulo: ");
        String titulo= s.nextLine();

        System.out.println("Descrição: ");
        String descricao= s.nextLine();

        try {

        System.out.println(adminConsole.AlteraEleicao(escolha,data_inicio,data_final, titulo,descricao));

        }catch(RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    System.out.println(adminConsole.AlteraEleicao(escolha,data_inicio,data_final, titulo,descricao));
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public static void GerirMesa(){

        System.out.println("1. Associar   2. Remover");
        Scanner s = new Scanner(System.in);
        System.out.println("Insira a opção: ");
        System.out.print("> ");
        boolean valido = false;
        while(!valido){
            try{
                int opcao = Integer.parseInt(s.nextLine());

                if ( opcao == 1){
                    valido=true;
                    AssociarMesaVoto();
                }
                else if(opcao == 2){
                    valido=true;
                    RemoverMesa();
                }
                else {
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }

            }catch (Exception e){
                System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
            }
        }


    }


    // Todo: Ja faz sentido talvez
    public void CriaMesaVoto(){
        Scanner s = new Scanner(System.in);
        System.out.println("Em que Departamento pretende criar a Mesa de Voto");
        //Dar display dos depts
        Departamento dep = escolherDept();
        //verificar se o departamento ja tem uma mesa
        if(dep.getNome().equals("")){
            System.out.println("O ja tem mesa de voto");
        }
        //se nao tiver crio a mesa e adiciono ao array de mesas

    }

    public static void AssociarMesaVoto(){

        Eleicao e = escolheEleicao();
        ArrayList<Departamento> deptsElegiveis;

        try {
            deptsElegiveis = adminConsole.getDepartamentosElegiveis(e);
        }catch (RemoteException r) {
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    deptsElegiveis = adminConsole.getDepartamentosElegiveis(e);
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }



        if(deptsElegiveis.isEmpty()){
            System.out.println("(!) Nao existem.");
            return;
        }

        int i=1;
        System.out.println("\nMESAS DISPONÍVEIS");
        for(Departamento d : deptsElegiveis) {
            System.out.println(i +". " + d.getNome());
            i++;
        }


        Scanner s = new Scanner(System.in);
        System.out.println("Escolha a mesa que pretende adicionar" );
        System.out.print("> ");
        int opcao = Integer.parseInt(s.nextLine());

        try {
            adminConsole.AddMesaVoto(e, deptsElegiveis.get(opcao - 1));
        } catch (RemoteException e1){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.AddMesaVoto(e, deptsElegiveis.get(opcao - 1));
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public static void RemoverMesa(){
        Eleicao e = escolheEleicao();
        int i=1;
        if(e.getDept().isEmpty()){
            System.out.println("NÃO EXISTEM MESAS PARA REMOVER\n");
            return;
        }
        System.out.println("\nMESAS DISPONÍVEIS");
        for (Departamento d: e.getDept()) {
            System.out.println(i +". " +d.getNome());
            i++;
        }
        Scanner s = new Scanner(System.in);
        System.out.println("Escolha a mesa que pretende remover");
        System.out.print("> ");
        int opcao = Integer.parseInt(s.nextLine());

        try {
            adminConsole.RemoverMesaVoto(e,e.getDept().get(opcao-1));

        }catch (RemoteException e1){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.RemoverMesaVoto(e,e.getDept().get(opcao-1));
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public static Departamento escolherDept(){

        ArrayList<Departamento> depts;
        try{
            depts = adminConsole.getListaDepartamentos();
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    depts = adminConsole.getListaDepartamentos();
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }

        if(depts.isEmpty()){
            System.out.println("NÃO EXISTEM DEPARTAMENTOS NOS REGISTOS, ADICIONE O DEPARTAMENTO QUE DESEJA");
            return null;
        }
        int i=1;
        System.out.println("\nDEPARTAMENTOS DISPONÍVEIS");
        for (Departamento d: depts) {
            System.out.println(i +". " +d.getNome());
            i++;
        }
        System.out.println("0. Registar novo departamento");
        while (true){
            try{
                Scanner s = new Scanner(System.in);
                System.out.print("> ");
                int opcao = Integer.parseInt(s.nextLine());
                if(opcao == 0 ){
                    return null;
                }
                else if(opcao <= i){
                    return depts.get(opcao-1);
                }
                else {
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
                }catch (Exception e){
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
            }

    }

    public static Eleicao escolheEleicao(){
        ArrayList<Eleicao> eleicoes;
        try{
            eleicoes = adminConsole.getListaEleicoes();
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    eleicoes = adminConsole.getListaEleicoes();
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }

        if(eleicoes.isEmpty()){
            System.out.println("NÃO EXISTEM ELEIÇÔES NOS REGISTOS!");
            return null;
        }
        int i = 1;
        System.out.println("\nELEIÇÔES DISPONIVEIS");
        for (Eleicao e: eleicoes) {
            System.out.println(i +". " +e.getTitulo());
            i++;
        }
        while (true){
            try {
                Scanner s = new Scanner(System.in);
                System.out.print("> ");
                int opcao = Integer.parseInt(s.nextLine());
                if(opcao <= i){
                    return eleicoes.get(opcao-1);
                }
                else {
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }

            }catch (Exception e){
                    System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
                }
            }
    }

    public static Departamento criaDepartamento(){

        while (true) {
            Scanner s = new Scanner(System.in);
            System.out.println("Insira nome do departamento");
            System.out.print("> ");
            String nome = s.nextLine();
            Departamento d = new Departamento(nome);
            try {
                boolean check = adminConsole.checkDepartamentExist(d);
                if(check){
                    System.out.println("\nDEPARTAMENTO ADICIONADO AOS REGISTOS\n");
                    return d;
                }
                else {
                    System.out.println("\n(!) DEPARTAMENTO JA SE ENCONTRA NO SISTEMA\n");
                    return null;
                }
            } catch (RemoteException e) {
                while (true) {
                    try {
                        //Thread.sleep(1000);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        break;
                    } catch (NotBoundException | RemoteException | MalformedURLException m) {
                        System.out.println("nao conectei");
                    }
                }
            }
        }
    }

    public static void LocaisDeVoto(){
        Scanner s = new Scanner(System.in);
        System.out.println("Insira o nome do eleitor");
        System.out.print("> ");
        String nome = s.nextLine();

        try {
            ArrayList<String> locais= adminConsole.LocalVoto(nome);
            if(locais.isEmpty()){
                System.out.println("\n(!) A PESSOA EM QUESTÃO AINDA NÃO EFETUOU NENHUMA VOTAÇÃO\n");
            }
            else{
                System.out.println("\nLista de locais onde " + nome + " votou:");
                for (String str:locais) {
                    System.out.println(str);
                }
                System.out.print("\n");
            }
        }catch (RemoteException e ){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public static void ConsultarEleicoesPassadas(){
        ArrayList<Eleicao> listaEleicoesPassadas;
        try{
            listaEleicoesPassadas = adminConsole.getEleicoesPassadas();
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    listaEleicoesPassadas = adminConsole.getEleicoesPassadas();
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }

        if(listaEleicoesPassadas.isEmpty()){
            System.out.println("(!) NÃO EXISTEM ELEIÇÕES PASSADAS NO HISTÓRICO");
            return;
        }
        //PRINT DAS OPÇÕES DISPONÍVEIS
        int i = 1;
        for (Eleicao e : listaEleicoesPassadas){
            System.out.println(i +". "+ e.getTitulo());
            i++;
        }

        System.out.println("Escolha a opção que pretende visitar");
        System.out.print(">");
        Scanner s = new Scanner(System.in);
        int opcao;
        while (true){
            try{
                opcao = Integer.parseInt(s.nextLine());
                if(opcao <= i){
                    System.out.println("\nInformaçao\n");
                    System.out.println(listaEleicoesPassadas.get(opcao-1).getTotal_votos());
                    System.out.println(listaEleicoesPassadas.get(opcao-1).resultados());
                    return;
                }
            }catch (NumberFormatException e){
                System.out.println("Insira uma opçao valida!");
            }
        }
    }

    //todo: Informaçao em falta
    public static void notifications(){
        notifications=true;
        boolean valido = false;
        Scanner s = new Scanner(System.in);
        String opcao;
        System.out.println("- - - INSIRA 0 PARA SAIR - - -");
        System.out.println("Notificações: ");
        while (!valido){
            opcao = s.nextLine();
            if(opcao.equals("0")){
                valido = true;
                notifications = false;
            }
        }
    }
    //==================================================================

    public void mesaNotification(String mesa){
        if(notifications){
            System.out.println("Mesa: " + mesa + " is On ");
        }
    }

    public void loginNotification(int userCC, String mesa){
        if(notifications){
            System.out.println("Novo eleitor chegou à mesa " + mesa);
        }
    }

    @Override
    public void newServer() throws RemoteException, NotBoundException, MalformedURLException {
        while (true){
            try {
                //Thread.sleep(1000);
                adminConsole = (RMI_S_I) Naming.lookup("Server");
                break;
            }catch(NotBoundException  | RemoteException |MalformedURLException m){
                System.out.println("nao conectei");
            }
        }
    }
}
