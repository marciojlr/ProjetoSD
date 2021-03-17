# ProjetoSD

## Ideias de Implementação

#### RMI SERVER
> É o servidor que armazeda todos os dados da aplicação, suportando por essa razão
> todas as operações necessárias através de métodos remotos usando Java RMI.

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
