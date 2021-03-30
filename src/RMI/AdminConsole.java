package RMI;

import Classes.Departamento;
import Classes.Eleicao;
import Classes.Pessoa;

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

    public AdminConsole() throws RemoteException {super();}

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
        adminConsole = (RMI_S_I) Naming.lookup("Server");
        RMI_C_I client = new AdminConsole();
        String teste;
        teste = adminConsole.teste((RMI_C_I) client);
        System.out.println(teste);
        Departamento dei = new Departamento("DEI");
        Departamento deec = new Departamento("DEEC");
        GregorianCalendar datainicio = new GregorianCalendar(2021,2,26);
        GregorianCalendar datafim = new GregorianCalendar(2021,2,30);
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
            System.out.println("4. Mesas de Voto");
            System.out.println("5. Alterar propriedades de eleição");
            System.out.println("6. Saber local de voto de um dado eleitor");
            System.out.println("14. Consultar eleiçoes passadas");
            System.out.print("> ");
            option= myObj.nextLine();
            if(option.equals("1")){
                try{
                    RegistoPessoa();
                }catch (RemoteException e){
                    System.out.println(e);
                }

            }
            else if(option.equals("2")){
                try {
                    criaEleicao();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("3")){
                try {
                    gerirListaCandidata();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("4")){
                try{
                    GerirMesa();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("5")){
                try {
                    AlteraPropriedadesEleicao();
                    Thread.sleep(50);
                }catch (RemoteException | InterruptedException e){
                    System.out.println(e);
                }
            }
            else if (option.equals("6")){
                try {
                    LocaisDeVoto();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("14")){
                try {
                    ConsultarEleicoesPassadas();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
        }
    }

    public static void  RegistoPessoa() throws RemoteException {

        Scanner s = new Scanner(System.in);
        System.out.println("\n- - - - REGISTO DE ELEITOR- - - -\n");
        System.out.print("Nome: ");
        String nome = s.nextLine();

        String tipo;
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

        Departamento departamento= null;
        while (departamento == null){
            departamento = escolherDept();
            if(departamento == null){
                departamento = criaDepartamento();

            }
        }

        adminConsole.AddDepartamento(departamento);

        System.out.print("CC: ");
        int CC=Integer.parseInt(s.nextLine());
        System.out.println("Validade CC");
        System.out.print("Mês: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        GregorianCalendar CC_val = new GregorianCalendar(ano,mes,1);
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

    public static void  criaEleicao() throws RemoteException {
        //RECOLHER INFORMAÇÃO
        Scanner s = new Scanner(System.in);
        System.out.println("\n- - - - REGISTO DE ELEIÇÃO- - - -\n");
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

        Departamento departamento = escolherDept();
        if(departamento == null){
            departamento = criaDepartamento();
            adminConsole.AddDepartamento(departamento);
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

    public static void gerirListaCandidata() throws RemoteException {
        //TODO: Adicionar os membros na parte de criar
        try{
            //TODO: FAZER UM TRY CACTH PARA ESTA CHAMADA
            ArrayList<Eleicao> eleicoes = adminConsole.getListaEleicoes();

            ArrayList<Eleicao> elegiveis = new ArrayList<>();

            if(eleicoes.isEmpty()){
                System.out.println("Nao ha nenhuma eleiçao a decorrer");
                return;
            }
            GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
            int i= 1;
            for( Eleicao e : eleicoes){
                if(e.getData_inicio().compareTo(date) > 0){
                    System.out.println(i + ". " + e.getTitulo());
                    elegiveis.add(e);
                    i++;
                }

            }
            if(elegiveis.isEmpty()){
                System.out.println("Nao ha eleiçoes");
                return;
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

            if(opcao.equals("1")){
                System.out.println("Insira Lista Candidata que deseja inserir: ");
                System.out.print("> ");
                String nome = s.nextLine();
                adminConsole.AddListaCandidata(elegiveis.get(eleicao-1),nome);
            }
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

                adminConsole.RemoveListaCandidata(elegiveis.get(eleicao-1),elegiveis.get(eleicao-1).getListaCandidata().get(nome-1).getNome());
            }
        }catch (RemoteException e){
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

    public static void AlteraPropriedadesEleicao() throws RemoteException{
        try{
            ArrayList<Eleicao> eleicoes = adminConsole.getListaEleicoes();

            ArrayList<Eleicao> elegiveis = new ArrayList<>();

            GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
            int i =0;
            for (Eleicao e: eleicoes) {
                if(e.getData_inicio().compareTo(date) > 0){
                    System.out.println(e.getTitulo());
                    elegiveis.add(e);
                    i++;
                }
            }
            if(i == 0){
                System.out.println("Nao existe eleicoes elegiveis para alterar");
                return;
            }
            Scanner s = new Scanner(System.in);
            boolean valido = false;
            String escolha="";
            while (!valido){

                System.out.println("Selecione a Eleição que deseja alterar");
                escolha = s.nextLine();
                for(Eleicao e1: elegiveis){
                    if(escolha.equals(e1.getTitulo())){

                        valido = true;
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

            System.out.println(adminConsole.AlteraEleicao(escolha,data_inicio,data_final, titulo,descricao));

        }catch(RemoteException e){
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

    public static void GerirMesa() throws RemoteException{

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

        try {
            ArrayList<Departamento> depts = adminConsole.getListaDepartamentos();

            ArrayList<Departamento> deptsElegiveis = new ArrayList<>();

            int i=1;
            System.out.println("\nMESAS DISPONÍVEIS");
            boolean encontrou = false;
            for (Departamento d: depts) {
                for(Departamento d2 : e.getDept()){
                    if(d2.getNome().equals(d.getNome())){
                       encontrou= true;
                    }
                }
                if(encontrou == false){
                    System.out.println(i +". " + d.getNome());
                    deptsElegiveis.add(d);
                    i++;
                }
                else{
                    encontrou= false;
                }
            }
            if(deptsElegiveis.isEmpty()){
                System.out.println("(!) Nao existem.");
                return;
            }
            Scanner s = new Scanner(System.in);
            System.out.println("Escolha a mesa que pretende adicionar" );
            System.out.print("> ");
            int opcao = Integer.parseInt(s.nextLine());
            adminConsole.AddMesaVoto(e,deptsElegiveis.get(opcao-1));


        }catch (RemoteException e1){
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

    public static void RemoverMesa(){
        Eleicao e = escolheEleicao();
        try {
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
            adminConsole.RemoverMesaVoto(e,e.getDept().get(opcao-1));


        }catch (RemoteException e1){
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

    public void ApagarMesaVoto(){

    }


    public static Departamento escolherDept(){
        try{
            ArrayList<Departamento> depts = adminConsole.getListaDepartamentos();
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
        }catch (RemoteException e){
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
        return null;
    }

    public static Eleicao escolheEleicao(){

        try{
            ArrayList<Eleicao> eleicoes = adminConsole.getListaEleicoes();
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


        }catch (RemoteException e){
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
        //hmmm
        return null;
    }

    public static Departamento criaDepartamento(){

        while (true) {
            Scanner s = new Scanner(System.in);
            System.out.println("Insira nome do departamento");
            System.out.print("> ");
            String nome = s.nextLine();
            System.out.println("Insira ip correspondente ao departamento");
            System.out.print("> ");
            String ip = s.nextLine();
            Departamento d = new Departamento(nome);
            try {
                boolean check = adminConsole.checkDepartamentExist(d);
                if(check){
                    System.out.println("\nDEPARTAMENTO ADICIONADO AOS REGISTOS\n");
                    return d;
                }
                else {
                    System.out.println("\nDEPARTAMENTO JA SE ENCONTRA NO SISTEMA\n");
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

    public static void LocaisDeVoto() throws RemoteException{
        Scanner s = new Scanner(System.in);
        System.out.println("Insira o nome da pessoa:");

        String nome = s.nextLine();

        try {
            ArrayList<String> locais= adminConsole.LocalVoto(nome);
            if(locais.isEmpty()){
                System.out.println("A pessoa em questão ainda nao efectuou nenhuma votação");
                return;
            }
            for (String str:locais) {
                System.out.println(str);
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

    public static void ConsultarEleicoesPassadas() throws RemoteException{
        ArrayList<Eleicao> eleicoes = new ArrayList<>();
        try{
             eleicoes = adminConsole.getListaEleicoes();
        }catch (RemoteException e){
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

        ArrayList<Eleicao> listaEleicoesPassadas = new ArrayList<Eleicao>();

        if(eleicoes.isEmpty()){
            System.out.println("Nao ha eleiçoes");
            return;
        }
        GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
        int i = 1;
        for (Eleicao e : eleicoes){
            if(e.getData_final().compareTo(date) < 0){
                System.out.println(i +". "+ e.getTitulo());
                listaEleicoesPassadas.add(e);
                i++;
            }
        }
        if(i==1){
            System.out.println("Nao existem eleiçoes finalizadas!");
            return;
        }
        System.out.println("Escolha uma das opçoes acima listas");
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
