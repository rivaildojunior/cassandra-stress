# DataStax Enterprise + DSE Search - Stress Testing 

1. Pré-requisitos:
    -  OpenJDK 8 ou Oracle Java Platform, Standard Edition 8 (JDK).
    -  Python 2.7 para uso do cqlsh.
    -  Docker
    
2. Crie um container DSE com a Pesquisa ativada:
      ```console
      user@user:~$ docker run -e DS_LICENSE=accept --name demo-dse -p 9042:9042 -d datastax/dse-server:6.8.0-ubi7 -s -R
      ```

    - Mais orientações podem ser encontradas [Aqui](https://hub.docker.com/r/datastax/dse-server)

3. Crie um container DSE-Studio:
      ```console
      user@user:~$ docker run -e DS_LICENSE=accept --name my-studio -p 9091:9091 -d datastax/dse-studio --link demo-dse
      ```

    - Mais orientações podem ser encontradas [Aqui](https://hub.docker.com/r/datastax/dse-studio/)

4. Para poder administrar o banco de dados:
      ```console
      user@user:~$ docker exec -it demo-dse cqlsh
      ```
   
5. Criando keyspace:
      ```SQl
      cqlsh> CREATE KEYSPACE IF NOT EXISTS ep9cas001 WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'dc1' : 2 };
      cqlsh> USE ep9cas001;
      ```        

6. Subindo a aplicação:
   - Basta clonar o projeto:
     ```console
     user@user:~$ git clone https://github.com/RafaelOFreitas/cassandra-stress.git
     ```
   
   - Importe o projeto na IDE como um projeto Maven.
   - Caso as dependências maven não sejam importadas automaticamente, na pasta do projeto executar:
     ```console
     user@user:~$ mvn clean install
     ```
        
7. Importe os dados para simular a carga do banco:
    - A carga de dados em data.csv possui um milhão de linhas para serem importadas.
    
    - Primeiro precisamos copiar os dados de `data.csv` para o container:
      ```console
      user@user:~$ docker cp C:\Users\Zupper\Downloads\poc-itau\data.csv demo-dse:/home/data.csv 
      ```
      
    - Com os dados no container execute:
      ```SQl
      cqlsh> USE ep9cas001;
      cqlsh:ep9cas001> COPY "ep9cas001"."tb_user"(id, name, gender, birthday, city) FROM '/home/data.csv' WITH DELIMITER = ',' AND HEADER = TRUE;
      ```
   
    - Se a importação for realizada com sucesso, aparece a seguinte mensagem:
      ```
      Processed: 1000000 rows; Rate:    6850 rows/s; Avg. rate:   17392 rows/s
      1000000 rows imported from 1 files in 57.500 seconds (0 skipped).
      ```
    
    - O passo da importação dos dados deve ser realizada anteriormente ao subir a aplicação pelo fato
    de configurarmos `schema-action` para recriar a keyspace ao executar a mesma.

8. Criando os índices de pesquisa:
    ```SQl
    cqlsh> USE ep9cas001;
    cqlsh:ep9cas001> CREATE SEARCH INDEX IF NOT EXISTS ON ep9cas001.tb_user WITH COLUMNS name, gender, birthday, city {excluded : false};
    ```