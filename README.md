# ProjetoSD

## Ideias de Implementação

#### Classes a implementar
* Classe Pessoas
* Classe Mesa
* Classe Eleição

#### RMI SERVER
> Programar Servidor para se connector a um porto. Se o porto já estiver ocupado vai dar uma
> excessão, essa excessão vai para um while que mete o servidor a mandar pings, se não receber
> resposta este pode voltar a tentar conectar-se ao porto, assim se o principal já não estiver
> a operar, o secundário pode assumir o seu papel.


#### ADMIN CONSOLE
* **1 - Registar Pessoas**

  > Registar dados das pessoas numa classe pessoa e guardar essa classe num ArrayList.
  
* **2 - Criar Eleição**

  > Registar dados de uma eleição numa classe e guardar essa classe num ArrayList.

* **3 - Gerir listas de candidatos de uma eleição**
  
  > Verificar as pessoas que podem fazer parte da lista.

* **4 - Gerir mesas de voto**

  > Criar mesas de voto e terminais associados. Apenas pode existir uma mesa de votos por departamento.

* **9 - Alterar propriedade de uma eleição**


* **10 - Saber em que local votou cada eleitor**

* **11 - Mostrar estado das mesas de voto**

* **12 - Mostrar eleitores em tempo real**

* **13 - Término da eleição na data**

* **14 - Consultar resultador detalhados de eleições passadas**



#### MULTICAST SERVER
> Existe um Multicast Servidor por cada mesa de voto que gere localmente os terminais
> que lhe estão associados. Uma pessoa entra num Multicast Servidor e identifica-se.

* **5 - Gerir terminais de voto**

  > Esta funcionalidade tem de ser automática. Penso que é a parte de quando se liga procurar terminais para se conectar.
  > Criar dois grupos Multicast, um para as maquinas se descobrirem e outro para comunicação de votos dos terminais para o servidor da mesa.
  
* **6 - Identificar eleitor na mesa de voto**

  > Inserir dados, se houver mais que uma eleição escolher a eleição e ordenar a ida a um terminal.

#### VOTING TERMINAL
> As Voting Terminals estão bloquadas até serem desbloqueadas pelo Multicast Server.
> Basicamente acho que o eleitor se dirige ao Server, identifica-se e depois é mandado
> para um terminal.

* **7 - Autenticação de eleitor no terminal de voto**

* **8 - Votar**

  > Um dos problemas aqui vai ser ter a certeza que o voto foi contabilizado, sem saber a identidade da pessoa que votou.


####Questões

* Tem de ser m - out - n reliable Multicast ? 
* Se houver mais que uma eleição a decorrer ao mesmo tempo, no mesmo departamento, a mesa de voto é a mesma para as
  duas eleições?
  
* Podemos criar uma classe (Departamento) que tem um endereço de ip associado? 
  > Visto só haver uma mesa por departamento, quando criamos as eleições, dizemos os departamentos em que elas ocorrem e depois
  > podemos criar os grupos para esses endereços já guardados. A melhor maneira é criar um ip random ou existe algum comando?
  
* Como é que os clientes vão saber os endereços das mesas?
  > Existe um grupo principal em que estão conectadas todas as mesas e todos os terminais. Depois os terminais
  > vão sendo redirecionados para os grupos específicos. A partir daí só passam a comunicar com o grupo que lhes diz
  > respeito.