"# Cassandra Stress" 

1° Importe os dados para simular a carga do banco de dados:

```console
cqlsh> USE ep9cas001;
cqlsh:ep9cas001> COPY "ep9cas001"."tb_user"(id, name, gender, birthday, city) FROM '/cassandra-stress/data.csv' WITH DELIMITER = ',' AND HEADER = TRUE;
```


