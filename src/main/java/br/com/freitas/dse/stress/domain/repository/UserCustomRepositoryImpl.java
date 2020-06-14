package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.Page;
import br.com.freitas.dse.stress.domain.model.QuerySolr;
import br.com.freitas.dse.stress.domain.model.User;
import br.com.freitas.dse.stress.domain.model.UserPageable;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.json.JSONObject;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final CassandraOperations cqlTemplate;

    public UserCustomRepositoryImpl(CassandraOperations cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    public UserPageable findUserByFilters(Map<String, Object> map, String asc, String desc, Integer page, Integer size) {
        Select.Where select = QueryBuilder.select().from("tb_user").where();

        page = page == null ? 0 : page <= 0 ? 0 : page;
        size = size == null ? 10 : size == 0 ? 10 : size;

        QuerySolr filters = this.getMapForSolrQuery(map, asc, desc, page, size);
        select.limit(size);

        select.and(QueryBuilder.eq("solr_query", new JSONObject(filters).toString()));

        List<User> users = this.cqlTemplate.select(select, User.class);

        /*TODO
            O spring-data-cassandra possui um objeto para criar a paginação (Slice<T> .getPageable()), porém os métodos
            não são implementados corretamente, forçando o usuário a criar sua própria implementação, esta pode ser seguida
            como exemplo.
        */
        return UserPageable.builder()
                .page(Page.newBuilder(this.cqlTemplate)
                        .withSize(users.size())
                        .withPrevious(page == 0 ? 0 : page - 1)
                        .withCurrent(page)
                        .withNext(users.size() < size ? null : page + 1)
                        //TODO Precisamos saber o total de linhas para calcular a quantidade de páginas
                        .withNumberOfPages(filters, size)
                        .build())
                .users(users)
                .build();
    }

    private QuerySolr getMapForSolrQuery(Map<String, Object> filters, String asc, String desc, Integer page, Integer size) {
        //TODO Este objeto foi criado com intuito de diminuir a complexidade das querys!
        QuerySolr.Builder builder = QuerySolr.newBuilder();

        if (filters.get("id") != null) {
            builder.withFilter("id", filters.get("id"));
        }

        if (filters.get("birthday_ini") != null && filters.get("birthday_end") != null) {
            builder.withRangeDate("birthday", filters.get("birthday_ini"), filters.get("birthday_end"));
        }

        if (filters.get("name") != null) {
            builder.withFilter("name", filters.get("name"));
        }

        if (filters.get("gender") != null) {
            builder.withFilter("gender", filters.get("gender"));
        }

        if (filters.get("city") != null) {
            builder.withFilter("city", filters.get("city"));
        }

        if (asc != null) {
            builder.withSort(asc, "asc");
        } else if (desc != null) {
            builder.withSort(desc, "desc");
        }

        return builder.withStart(page, size)
                .build();
    }
}