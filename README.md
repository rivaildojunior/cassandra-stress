# Cassandra Stress 

1. Pré-requisitos:

    -  OpenJDK 8 ou Oracle Java Platform, Standard Edition 8 (JDK).
    -  Python 2.7 para uso do cqlsh.
    
2. Faça instalação do Cassandra:
`https://cassandra.apache.org/download/`

3. Iniciar o servidor do Cassandra:
    - Navegar pelo prompt de comando até a pasta do cassandra e depois para dentro de bin usando o 
      comando cd. Após entrar na pasta bin do cassandra basta digitar.

      ```console
      user@user:~$ cassandra.bat -f
      ```

   - Após executar o comando o servidor estaria ligado e deve-se manter a janela do prompt aberta. 
     Caso você fechar a janela do prompt de comando, o servidor será desligado.  

4. Subindo a aplicação:
   - Basta clonar o projeto:
   
     ```console
     user@user:~$ git clone https://github.com/RafaelOFreitas/cassandra-stress.git
     ```
   
   - Importe o projeto na IDE como um projeto Maven.
   - Caso as dependências maven não sejam importadas automaticamente, na pasta do projeto executar:
      
     ```console
     user@user:~$ mvn clean install
     ```
        
5. Para poder administrar o banco de dados:
   - Basta navegar para dentro da pasta bin do cassandra novamente e executar o comando:

     ```console
     user@user:~$ cqlsh.bat
     ```

6. Importe os dados para simular a carga do banco:
    - A carga de dados em data.scv possui um milhão de linhas para serem importadas.
       
    ```SQl
    cqlsh> USE ep9cas001;
    cqlsh:ep9cas001> COPY "ep9cas001"."tb_user"(id, name, gender, birthday, city) FROM '/cassandra-stress/data.csv' WITH DELIMITER = ',' AND HEADER = TRUE;
    ```
   
    - Se a importação for realizada com sucesso, aparece a seguinte mensagem:
    
    ```
   Processed: 1000000 rows; Rate:    6850 rows/s; Avg. rate:   17392 rows/s
   1000000 rows imported from 1 files in 57.500 seconds (0 skipped).
    ```
    
    - O passo da importação dos dados deve ser realizada anteriormente ao subir a aplicação pelo fato
    de configurarmos a ação de esquema para recriar a keyspace ao executar a mesma.
    