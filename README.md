# CPD Projects

CPD Project2 of group T03G18.

Group members:

1. Gabriel Alves (201709532)
2. Sofia
3. Bruno

## Trabalho 2

Jogo de adivinhar um número.
Cada jogador pode-se juntar a uma sala de `ROOM_PLAYERS = 5` jogadores.
Em que o objetivo é ficar o mais perto do número gerado pelo servidor.
Ganha o(s) melhor(es) com o resultado mais perto.

### Compilar

Foi feito com Java 17, Gradle e Docker.
Deve-se executar `gradle shadowJar` na dentro da pasta `/assign2/src` para gerar o `servidor.jar` e o `cliente.jar`.

### Executar

Dentro da pasta `/assign2/src` existe o ficheiro `compose.yaml` que contém as instruções como criar os containers, network.
Para arrancar o servidor, `docker compose up server`.
Para os clientes existe já de 1 a 6, podendo iniciar mais se necessário, usando `docker compose up client1`.

