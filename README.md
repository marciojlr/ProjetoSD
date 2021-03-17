# ProjetoSD

## Ideias de Implementação

#### RMI SERVER
> Progamar Server para se connectar a um porto. Se o porto já estiver ocupado vai dar uma
> excessão, essa excessão vai para um while que mete o servidor a mandar pings, se não receber
> resposta este pode voltar a tentar conectar-se ao porto, assim se o principal já não estiver
> a operar, o secundário pode assumir o seu papel.


#### ADMIN CONSOLE
* Registar Pessoas
* Criar Eleição
* Gerir listas de candidatos de uma eleição
* Gerir mesas de voto
* Alterar propriedade de uma eleição
* Saber em que local votou cada eleitor
* Mostrar estado das mesas de voto
* Mostrar eleitores em tempo real
* Término da eleição na data
* Consultar resultador detalhados de eleições passadas


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
