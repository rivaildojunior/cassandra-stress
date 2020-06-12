package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.User;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final CassandraOperations cqlTemplate;

    public UserCustomRepositoryImpl(CassandraOperations cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    public List<User> getQuery(Map<String, Object> filtro) {
        Select.Where select = QueryBuilder.select().from("tb_user")
                .where();

        filtro.forEach((chave, valor) -> {
            if (valor != null) {
            	   if (chave.equals("birthdayIni")) {
                       select.and(QueryBuilder.gte("birthday", this.convertToDate(valor)));
                       return;
                   }
            	   if (chave.equals("birthdayFim")) {
                       select.and(QueryBuilder.lte("birthday", this.convertToDate(valor)));
                       return;
                   }

                select.and(QueryBuilder.eq(chave, valor));
            }
        });

        return this.cqlTemplate.select(select, User.class);
    }

    private Date convertToDate(Object Object) {
        LocalDate date = (LocalDate) Object;

        return Date.from(date.atStartOfDay()
                .atZone(ZoneId.of("GMT"))
                .toInstant());
    }
}