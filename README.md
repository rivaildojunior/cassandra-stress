# DataStax Enterprise + DSE Search - Stress Testing 

#### Pré-requisitos:
   - OpenJDK 8 ou Oracle Java Platform, Standard Edition 8 (JDK).
   - Python 2.7 para uso do cqlsh.
   - Docker
            
1. Crie um container DSE com a Pesquisa ativada:
      ```console
      user@user:~$ docker pull datastax/dse-server:6.8.0
      ```  
   
      ```console 
      user@user:~$ docker run -e DS_LICENSE=accept --name demo-dse -p 9042:9042 -d datastax/dse-server:6.8.0 -s -R
      ```

    - Mais orientações podem ser encontradas [Aqui](https://hub.docker.com/r/datastax/dse-server)

2. Crie um container DSE-Studio:
      ```console
      user@user:~$ docker pull datastax/dse-studio:6.8.0
      ```    
   
      ```console
      user@user:~$ docker run -e DS_LICENSE=accept --name my-studio -p 9091:9091 -d datastax/dse-studio:6.8.0 --link demo-dse
      ```

    - Mais orientações podem ser encontradas [Aqui](https://hub.docker.com/r/datastax/dse-studio/)

3. Para poder administrar o banco de dados:
      ```console
      user@user:~$ docker exec -it demo-dse cqlsh
      ```
   
4. Criando keyspace:
      ```CQL
      cqlsh> CREATE KEYSPACE IF NOT EXISTS ep9cas001 WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'dc1' : 2 };
      cqlsh> USE ep9cas001;
      ```        

5. Subindo a aplicação:
   - Basta clonar o projeto:
     ```console
     user@user:~$ git clone https://github.com/RafaelOFreitas/cassandra-stress.git
     ```
   
   - Importe o projeto na IDE como um projeto Maven.
   - Caso as dependências maven não sejam importadas automaticamente, na pasta do projeto executar:
     ```console
     user@user:~$ mvn clean install
     ```
        
6. Importe os dados para simular a carga do banco:
    - A carga de dados em data.csv possui um milhão de linhas para serem importadas.
    
    - Primeiro precisamos copiar os dados de `data.csv` para o container:
      ```console
      user@user:~$ docker cp `caminho completo`/cassandra-stress/data.csv demo-dse:/home/data.csv 
      ```
      
    - Com os dados no container execute:
      ```CQL
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

7. Criando os índices de pesquisa:
    ```CQL
    cqlsh> USE ep9cas001;
    cqlsh:ep9cas001> CREATE SEARCH INDEX IF NOT EXISTS ON ep9cas001.tb_user WITH COLUMNS name {docValues:true}, gender {docValues:true}, birthday {docValues:true}, city {docValues:true, excluded : false};
    ```
   
    - Exemplo de Query Solr:
        ```CQL
        cqlsh:ep9cas001> SELECT id FROM ep9cas001.tb_user WHERE solr_query='{"q":"*:*", "sort":"id asc"}' LIMIT 10;
        ```
    