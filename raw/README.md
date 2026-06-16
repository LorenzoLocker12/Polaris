# Polaris

Polaris e um mod client-side para Minecraft Fabric 1.21.1. Ele usa a API oficial do Cobblemon apenas no fluxo PC -> party e envia comandos de chat para os testes de GTS e timing. Nao depende do LeanGTS nem do CentralGts.

Use somente em servidor proprio de DEV/STAGING, com autorizacao e backup.

## Dependencias

- Fabric Loader 0.17.2 ou superior
- Fabric API
- Fabric Language Kotlin
- Cobblemon Fabric 1.7.3
- Java 21

## Build

```text
gradle build
```

O JAR final fica em:

```text
build/libs/polaris-0.1.0.jar
```

## Anuncio De Pokemon

Pressione `F8` para:

1. Abrir `/pc` e aguardar a sincronizacao do Cobblemon.
2. Escolher o Pokemon por caixa e slot.
3. Escolher um slot livre da party.
4. Aguardar o delay mantendo o Pokemon no PC.
5. Mover o Pokemon pelo pacote oficial do Cobblemon.
6. Aguardar a party sincronizar e executar `/gts add pokemon` imediatamente.

Pressione `F9` para anunciar com delay um Pokemon que ja esta na party.

Tambem e possivel usar:

```text
/polaris gts list <posicao 1-6> <preco> [delay_ms]
```

O comando enviado ao servidor sera:

```text
/gts add pokemon <posicao> <preco>
```

## Anuncio De Item + Drop

Anuncia e dropa o item da hotbar praticamente no mesmo instante:

```text
/polaris gts item_drop_race <posicao 1-9> <preco> <quantidade>
```

Exemplo:

```text
/polaris gts item_drop_race 1 36000 1
```

Para calibrar a corrida:

```text
/polaris gts item_drop_race 1 36000 1 <gap_ms> <add_first|drop_first>
```

Com `gap_ms=0`, as duas acoes sao disparadas no mesmo tick do cliente.

## Concorrencia No Mercado De Itens

### Mesmo slot anunciado varias vezes

```text
/polaris gts item_add_burst <posicao> <preco> <quantidade> <tentativas> [gap_ms]
```

Exemplo:

```text
/polaris gts item_add_burst 1 36000 1 20 0
```

Procura anuncios duplicados do mesmo item e reservas cuja soma exceda o stack.

### Mesmo item com precos concorrentes

```text
/polaris gts item_price_race <posicao> <preco_a> <preco_b> <quantidade> <ciclos> [gap_ms]
```

Exemplo:

```text
/polaris gts item_price_race 1 36000 1 1 10 0
```

Procura dois ou mais anuncios ativos do mesmo item com precos conflitantes.

### Mesmo item com quantidades concorrentes

```text
/polaris gts item_quantity_race <posicao> <preco> <qty_a> <qty_b> <ciclos> [gap_ms]
```

Exemplo:

```text
/polaris gts item_quantity_race 1 36000 1 64 10 0
```

Procura overbooking, stack negativo, consumo parcial incorreto e anuncios cuja quantidade agregada supera o estoque real.

Em todos os testes, resultado seguro significa que o servidor reserva e remove o item atomicamente, aceitando apenas operacoes compatíveis com o estoque real.
