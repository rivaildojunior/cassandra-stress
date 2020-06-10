# Cassandra Stress 

1. Faça instalação do Cassandra:
`https://cassandra.apache.org/download/`

2. Iniciar o servidor do Cassandra:
    - Navegar pelo prompt de comando até a pasta do cassandra e depois para dentro da pasta bin usando o 
      comando cd. Após entrar na pasta bin do cassandra basta digitar.

      ```console
      user@user:~$ cassandra.bat -f
      ```

   - Após executar o comando o servidor estaria ligado e você deve manter a janela do prompt de comando aberta. 
     Caso você fechar a janela do prompt de comando, o servidor sera desligado.  

4. Para poder administrar o banco de dados:
Basta navegar para dentro da basta bin do cassandra novamente e executar o comando:

    ```console
    user@user:~$ cqlsh.bat
    ```

5. Importe os dados para simular a carga do banco de dados:

```SQl
cqlsh> USE ep9cas001;
cqlsh:ep9cas001> COPY "ep9cas001"."tb_user"(id, name, gender, birthday, city) FROM '/cassandra-stress/data.csv' WITH DELIMITER = ',' AND HEADER = TRUE;
```