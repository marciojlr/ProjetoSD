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
        menu();

    }

    public static void menu(){
        Scanner myObj = new Scanner(System.in);
        String option;
        while (true){
            System.out.println("====== Bem vindo! ======");
            System.out.println("1. Registar");
            System.out.println("2. Criar Eleicao");
            System.out.println("3. Gerir Listas de candidatos a uma eleicao");
            System.out.println("4. Gerir Mesas de Voto");
            System.out.println("5. Alterar propriedades de eleicao");
            System.out.println("6. Saber local de voto de um dado eleitor");
            System.out.println("7. Estado das mesas de voto / Votos em tempo real");
            System.out.println("14. Consultar eleicoes passadas");
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
                    System.out.println("opcao invalida");
            }
        }
    }

    public static String grupoDeVoto(){
        Scanner s = new Scanner(System.in);
        String tipo;
        while(true){
            System.out.print("1. Docente\n2. Estudante\n3. Funcionario\n");
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
                    System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
                }
            }catch (NumberFormatException e ){
                System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
            }
        }
        return tipo;
    }

    //Todo alterar o while sleeps
    public static void  RegistoPessoa(){

        Scanner s = new Scanner(System.in);
        System.out.println("\n- - - - REGISTO DE ELEITOR- - - -\n");
        System.out.print("Nome: ");
        String nome = s.nextLine();

        String tipo = grupoDeVoto();

        System.out.print("Password: ");
        String password = s.nextLine();

        Departamento departamento= null;
        while (departamento == null){
            departamento = escolherDept();
            if(departamento == null){
                departamento = criaDepartamento();
            }
        }

        System.out.print("CC: ");
        int CC=Integer.parseInt(s.nextLine());
        System.out.println("Validade CC");
        System.out.print("Mes: ");
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
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.registarPessoa(nome, tipo, password, departamento, CC, CC_val, telemovel, morada);
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
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
            System.out.println("POR FAVOR INSIRA UM FORMATO DE DATA VALIDO");
            setHour(date);
        }
    }

    public static GregorianCalendar pedeData(){
        Scanner s = new Scanner(System.in);
        System.out.print("Dia: ");
        int dia = Integer.parseInt(s.nextLine());
        System.out.print("Mes: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        GregorianCalendar data = new GregorianCalendar(ano,mes-1,dia);

        return data;
    }

    public static void  criaEleicao(){
        //RECOLHER INFORMAÇÃO
        Scanner s = new Scanner(System.in);
        System.out.println("\n- - - - REGISTO DE ELEICAO- - - -\n");

        System.out.println("Data de inicio");
        GregorianCalendar data_inicio = pedeData();
        setHour(data_inicio);

        System.out.println("Data de encerramento");
        GregorianCalendar data_fim = pedeData();
        setHour(data_fim);

        System.out.println("Titulo: ");
        String titulo= s.nextLine();

        System.out.println("Descricao: ");
        String descricao= s.nextLine();

        Departamento departamento= null;
        while (departamento == null){
            departamento = escolherDept();
            if(departamento == null){
                departamento = criaDepartamento();
            }
        }
        //So podem votar pessoas deste tipo
        System.out.println("GRUPO DE PESSOAS QUE PODEM ESTUDAR");
        String tipo_Pessoa = grupoDeVoto();

        try{
            String r;
            r = adminConsole.criarEleicao(data_inicio, data_fim , titulo, descricao, departamento, tipo_Pessoa);
            System.out.println(r);
        }catch (RemoteException e){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.criarEleicao(data_inicio, data_fim, titulo, descricao, departamento, tipo_Pessoa);
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException |InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }
    }

    public static void gerirListaCandidata(){

        Scanner s = new Scanner(System.in);
        String opcao;
        boolean valido = false;

        Eleicao elegivel = escolheEleicao();

        if(elegivel != null){
            System.out.println("1. Adicionar  2. Remover");
            while (!valido){
                System.out.print("> ");
                opcao = s.nextLine();
                if (opcao.equals("1")) {
                    adicionaListaCandidata(elegivel);
                    valido = true;
                }
                else if(opcao.equals("2")){
                    removeListaCandidata(elegivel);
                    valido = true;
                }
                else {
                    System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
                }
            }
        }
    }

    public static void  adicionaListaCandidata(Eleicao elegivel){
        Scanner s = new Scanner(System.in);
        System.out.println("Insira Lista Candidata que deseja inserir: ");
        System.out.print("> ");
        String nome = s.nextLine();
        try{
            adminConsole.AddListaCandidata(elegivel,nome);
        }catch (RemoteException e){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.AddListaCandidata(elegivel,nome);
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }
    }

    public static void removeListaCandidata(Eleicao elegivel){
        Scanner s = new Scanner(System.in);
        if(elegivel.getListaCandidata().isEmpty()){
            System.out.println("(!) NAO HA LISTAS PARA REMOVER");
            return;
        }

        //print listas candidatas
        elegivel.printLista();

        System.out.println("Insira a Lista Candidata que deseja remover:");
        int nome=0;
        boolean valido= false;
        while (!valido) {
            try {
                System.out.print("> ");
                nome = Integer.parseInt(s.nextLine());
                if (nome <= elegivel.getListaCandidata().size() && nome > 0) {
                    valido = true;
                } else {
                    System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
                }

            }catch (Exception e ){
                System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
            }
        }
        try {
            adminConsole.RemoveListaCandidata(elegivel,elegivel.getListaCandidata().get(nome-1).getNome());
        }catch (RemoteException e){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.RemoveListaCandidata(elegivel,elegivel.getListaCandidata().get(nome-1).getNome());
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }
    }

    public static void AlteraPropriedadesEleicao(){

        Scanner s = new Scanner(System.in);
        Eleicao elegivel = escolheEleicao();

        if(elegivel != null){
            String nome = elegivel.getTitulo();
            GregorianCalendar data_inicio= elegivel.getData_inicio();
            GregorianCalendar data_final = elegivel.getData_inicio();
            String titulo= elegivel.getTitulo();
            String descricao= elegivel.getDescricao();
            boolean sair = false;
            while (!sair){
                System.out.println("Escolha a propriedade que deseja alterar");
                System.out.println("1. Data de inicio");
                System.out.println("2. Data de final");
                System.out.println("3. Titulo");
                System.out.println("4. Descricao");
                System.out.println("5. Sair");
                System.out.print("> ");
                String opcao = s.nextLine();
                switch (opcao){
                    case "1":
                        System.out.println("Data: "+ data_inicio.get(5) + "/" + (data_inicio.get(2)+1) + "/" + data_inicio.get(1) +" Horas: " + data_inicio.get(11)+":"+data_inicio.get(12));
                        System.out.println("Nova data de inicio:");
                        data_inicio=pedeData();
                        setHour(data_inicio);
                        break;
                    case "2":
                        System.out.println("Data: "+ data_final.get(5) + "/" + (data_final.get(2) + 1) + "/" + data_final.get(1) +" Horas: " + data_final.get(11)+":"+data_final.get(12));
                        System.out.println("Nova data final:");
                        data_final=pedeData();
                        setHour(data_final);
                        break;
                    case "3":
                        System.out.println("Titulo: "+ titulo);
                        System.out.println("Novo titulo: ");
                        System.out.print("> ");
                        titulo=s.nextLine();
                        break;
                    case "4":
                        System.out.println("Descricao: "+ descricao);
                        System.out.println("Nova Descricao");
                        System.out.print("> ");
                        descricao=s.nextLine();
                        break;
                    case "5":
                        sair = true;
                        break;
                    default:
                        System.out.println("Opcao nao valida");
                        break;
                }
            }
            try {
                System.out.println(adminConsole.AlteraEleicao(nome,data_inicio,data_final, titulo,descricao));
            }catch(RemoteException e){
                long sTime = System.currentTimeMillis();
                while ( System.currentTimeMillis() - sTime < 30000){
                    try {
                        Thread.sleep(500);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        System.out.println(adminConsole.AlteraEleicao(nome,data_inicio,data_final, titulo,descricao));
                        break;
                    }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                        if(System.currentTimeMillis() - sTime >= 30000){
                            System.exit(-1);
                        }
                    }
                }
            }
        }

    }

    public static void GerirMesa(){
        System.out.println("1. Associar   2. Remover  3. Criar");
        Scanner s = new Scanner(System.in);

        boolean valido = false;
        while(!valido){
            System.out.println("Insira a opcao: ");
            System.out.print("> ");
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
                else if(opcao == 3){
                    valido = true;
                    criaDepartamento();
                }
                else {
                    System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
                }
            }catch (Exception e){
                System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
            }
        }
    }

    public static void AssociarMesaVoto(){

        Eleicao e = escolheEleicao();
        ArrayList<Departamento> deptsElegiveis=new ArrayList<>();

        if(e != null){
            try {
                deptsElegiveis = adminConsole.getDepartamentosElegiveis(e);
            }catch (RemoteException r) {
                long sTime = System.currentTimeMillis();
                while ( System.currentTimeMillis() - sTime < 30000){
                    try {
                        Thread.sleep(500);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        deptsElegiveis = adminConsole.getDepartamentosElegiveis(e);
                        break;
                    }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                        if(System.currentTimeMillis() - sTime >= 30000){
                            System.exit(-1);
                        }
                    }
                }
            }

            if(deptsElegiveis.isEmpty()){
                System.out.println("(!) Nao existem departamentos para associar.");
                return;
            }

            boolean valido = false;
            int opcao= -1;
            Departamento dept = null;
            while (!valido){
                int i=1;
                System.out.println("\nMESAS DISPONIVEIS");
                for(Departamento d : deptsElegiveis) {
                    System.out.println(i +". " + d.getNome());
                    i++;
                }
                Scanner s = new Scanner(System.in);
                System.out.println("Escolha a mesa que pretende adicionar" );
                System.out.print("> ");
                opcao = Integer.parseInt(s.nextLine());

                if( opcao <= deptsElegiveis.size() && opcao > 0 ){
                    valido= true;
                }
                else {
                    System.out.println("(!) OPCAO NAO VALIDA");
                }
            }

            try {
                adminConsole.AddMesaVoto(e, deptsElegiveis.get(opcao - 1));
            } catch (RemoteException e1){
                long sTime = System.currentTimeMillis();
                while ( System.currentTimeMillis() - sTime < 30000){
                    try {
                        Thread.sleep(500);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        adminConsole.AddMesaVoto(e, deptsElegiveis.get(opcao - 1));
                        break;
                    }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                        if(System.currentTimeMillis() - sTime >= 30000){
                            System.exit(-1);
                        }
                    }
                }
            }
        }
    }

    public static void RemoverMesa(){
        Eleicao e = escolheEleicao();
        int i=1;
        if(e.getDept().isEmpty()){
            System.out.println("NAO EXISTEM MESAS PARA REMOVER\n");
            return;
        }
        System.out.println("\nMESAS DISPONIVEIS");
        for (Departamento d: e.getDept()) {
            System.out.println(i +". " +d.getNome());
            i++;
        }
        Scanner s = new Scanner(System.in);

        boolean valido = false;
        int opcao;
        Departamento departamento= null;
        while (!valido){
            System.out.println("Escolha a mesa que pretende remover");
            System.out.print("> ");
            opcao = Integer.parseInt(s.nextLine());
            try{
                departamento = e.getDept().get(opcao-1);
                valido=true;
            }catch (Exception m){
                System.out.println("Opcao invalida");
            }
        }
        try {
            adminConsole.RemoverMesaVoto(e,departamento);

        }catch (RemoteException e1){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.RemoverMesaVoto(e,departamento);
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }
    }

    public static Departamento escolherDept(){

        ArrayList<Departamento> depts = new ArrayList<>();
        try{
            depts = adminConsole.getListaDepartamentos();
        }catch (RemoteException e){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    depts = adminConsole.getListaDepartamentos();
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }

        if(depts.isEmpty()){
            System.out.println("NAO EXISTEM DEPARTAMENTOS NOS REGISTOS, ADICIONE O DEPARTAMENTO QUE DESEJA");
            return null;
        }
        int i=1;
        System.out.println("\nDEPARTAMENTOS DISPONIVEIS");
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
                    System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
                }
                }catch (Exception e){
                    System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA");
                }
            }

    }

    public static Eleicao escolheEleicao(){
        ArrayList<Eleicao> eleicoes = new ArrayList<>();
        try{
            eleicoes = adminConsole.getEleicoesElegiveis();
        }catch (RemoteException e){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    eleicoes = adminConsole.getEleicoesElegiveis();
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }
        //VERIFICAR SE EXISTEM ELEICOES
        if(eleicoes.isEmpty()){
            System.out.println("NAO EXISTEM ELEICOES NOS REGISTOS!");
            return null;
        }
        //PRINT DE ELEICOES
        int i = 1;
        System.out.println("\nELEICOES DISPONIVEIS");
        for (Eleicao e: eleicoes) {
            System.out.println(i +". " +e.getTitulo());
            i++;
        }
        while (true){
            try {
                Scanner s = new Scanner(System.in);
                System.out.print("> ");
                int opcao = Integer.parseInt(s.nextLine());
                return eleicoes.get(opcao-1);

            }catch (Exception e){
                System.out.println("\nOPCAO INVALIDA, ESCOLHA OUTRA!");
            }
        }
    }

    public static Departamento criaDepartamento(){
            Scanner s = new Scanner(System.in);
            System.out.println("Insira nome do departamento");
            System.out.print("> ");
            String nome = s.nextLine();
            Departamento d = new Departamento(nome);
            boolean check = false;
            try {
                check = adminConsole.AddDepartamento(d);
            } catch (RemoteException e) {
                long sTime = System.currentTimeMillis();
                while ( System.currentTimeMillis() - sTime < 30000){
                    try {
                        Thread.sleep(500);
                        adminConsole = (RMI_S_I) Naming.lookup("Server");
                        check = adminConsole.AddDepartamento(d);
                        break;
                    } catch (NotBoundException | RemoteException | MalformedURLException | InterruptedException m) {
                        if(System.currentTimeMillis() - sTime >= 30000){
                            System.exit(-1);
                        }
                    }
                }
            }
            if(check){
                System.out.println("\nDEPARTAMENTO ADICIONADO AOS REGISTOS\n");
                return d;
            }
            else {
                System.out.println("\n(!) DEPARTAMENTO JA SE ENCONTRA NO SISTEMA\n");
                return null;
            }

    }

    public static void LocaisDeVoto(){
        Scanner s = new Scanner(System.in);
        System.out.println("Insira o nome do eleitor");
        System.out.print("> ");
        String nome = s.nextLine();
        ArrayList<String> locais = new ArrayList<>();
        try {
            locais= adminConsole.LocalVoto(nome);
        }catch (RemoteException e ){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    locais= adminConsole.LocalVoto(nome);
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if( System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }
        if(locais.isEmpty()){
            System.out.println("\n(!) A PESSOA EM QUESTAO AINDA NAO EFETUOU NENHUMA VOTACAO\n");
        }
        else{
            System.out.println("\nLista de locais onde " + nome + " votou:");
            for (String str:locais) {
                System.out.println(str);
            }
            System.out.print("\n");
        }
    }

    public static void ConsultarEleicoesPassadas(){
        ArrayList<Eleicao> listaEleicoesPassadas = new ArrayList<>();
        try{
            listaEleicoesPassadas = adminConsole.getEleicoesPassadas();
        }catch (RemoteException e){
            long sTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() - sTime < 30000){
                try {
                    Thread.sleep(500);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    listaEleicoesPassadas = adminConsole.getEleicoesPassadas();
                    break;
                }catch(NotBoundException | RemoteException | MalformedURLException | InterruptedException m){
                    if(System.currentTimeMillis() - sTime >= 30000){
                        System.exit(-1);
                    }
                }
            }
        }

        if(listaEleicoesPassadas.isEmpty()){
            System.out.println("(!) NAO EXISTEM ELEICOES PASSADAS NO HISTORICO");
            return;
        }
        //PRINT DAS OPCOES DISPONIVEIS
        int i = 1;
        for (Eleicao e : listaEleicoesPassadas){
            System.out.println(i +". "+ e.getTitulo());
            i++;
        }

        System.out.println("Escolha a opcao que pretende visitar");
        System.out.print("> ");
        Scanner s = new Scanner(System.in);
        int opcao;
        while (true){
            try{
                opcao = Integer.parseInt(s.nextLine());
                System.out.println(listaEleicoesPassadas.get(opcao-1).resultados());
                return;
            }catch (Exception e){
                System.out.println("Insira uma opcao valida!");
            }
        }
    }

    public static void notifications(){
        notifications=true;
        boolean valido = false;
        Scanner s = new Scanner(System.in);
        String opcao;
        System.out.println("- - - PRESSIONE ENTER PARA SAIR - - -");
        System.out.println("Notificacoes: ");
        while (!valido){
            opcao = s.nextLine();
            switch (opcao) {
                default :
                    valido = true;
                    notifications = false;
                    break;

            }
        }
    }
    //==================================================================

    public void notification(String message, int priority){
        if(priority == 1){
            System.out.println(message);
            System.out.print("> ");
        }
        else{
            if(notifications){
                System.out.println(message);
                System.out.print("> ");
            }
        }
    }
}
