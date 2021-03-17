# ProjetoSD

## Ideias de Implementação

#### RMI SERVER
> Progamar Server para se connectar a um porto. Se o porto já estiver ocupado vai dar uma
> excessão, essa excessão vai para um while que mete o servidor a mandar pings, se não receber
> resposta este pode voltar a tentar conectar-se ao porto, assim se o principal já não estiver
> a operar, o secundário pode assumir o seu papel.


#### ADMIN CONSOLE
* 1 - Registar Pessoas
  > Registar dados das pessoas numa classe pessoa e guardar essa classe num ArrayList.
* 2 - Criar Eleição
  > Registar dados de uma eleição numa classe e guardar essa classe num ArrayList.
* 3 - Gerir listas de candidatos de uma eleição
* 4 - Gerir mesas de voto
* 9 - Alterar propriedade de uma eleição
* 10 - Saber em que local votou cada eleitor
* 11 - Mostrar estado das mesas de voto
* 12 - Mostrar eleitores em tempo real
* 13 - Término da eleição na data
* 14 - Consultar resultador detalhados de eleições passadas


#### MULTICAST SERVER
> Existe um Multicast Server por cada mesa de voto que gere localmente os terminais
> que lhe estão associados. Uma pessoa entra num Multicast Server e identifica-se,

* 5 - Gerir terminais de voto
* 6 - Indentificar eleitor na mesa de voto


#### VOTING TERMINAL
> As Voting Terminals estão bloquadas até serem desbloqueadas pelo Multicast Server.
> Basicamente acho que o eleitor se dirige ao Server, identifica-se e depois é mandado
> para um terminal.

* 7 - Autenticação de eleitor no terminal de voto
* 8 - Votar
