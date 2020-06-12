package br.com.freitas.dse.stress.domain.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import br.com.freitas.dse.stress.domain.model.User;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final CassandraOperations cqlTemplate;
    private String query = "";

    public UserCustomRepositoryImpl(CassandraOperations cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    public List<User> getQuery(Map<String, Object> filters, String order, Integer start, Integer size) {
        Select.Where select = QueryBuilder.select().from("tb_user").where();
        query = "";

        filters.forEach((key, value) -> {
            if (value != null) {
                if (key.equals("birthday_ini")) {
                    query = this.getQueryForBirthdayIni(query, value);
                    return;
                }

                if (key.equals("birthday_end")) {
                    query = this.getQueryForBirthdayEnd(query, value);
                    return;
                }

                query = this.getQueryForOtherFilters(query, key, value);
            }
        });

        if (order != null) {
            select = this.getSelectWithSorting(select, order, start);
        } else {
            select = this.getSelectWithoutSorting(select, start);
        }

        if (size != null) {
            select.limit(size);
        }

        return this.cqlTemplate.select(select, User.class);
    }

    private Select.Where getSelectWithSorting(Select.Where select, String order, Integer start) {
        if (!query.equals("")) {
            return select.and(QueryBuilder.eq("solr_query", this.getQueryOrderBy(query, start, order)));
        }

        return select.and(QueryBuilder.eq("solr_query", this.getQueryOrderByAndAllFields(start, order)));
    }

    private Select.Where getSelectWithoutSorting(Select.Where select, Integer start) {
        if (!query.equals("")) {
            return select.and(QueryBuilder.eq("solr_query", this.getQuery(query, start)));
        }

        return select.and(QueryBuilder.eq("solr_query", this.getQueryForAllFields(start)));
    }

    private String getQueryForOtherFilters(String query, String key, Object value) {
        return new StringBuilder(query)
                .append("\"fq\":\"")
                .append(key)
                .append(":")
                .append(value.toString())
                .append("\"")
                .toString();
    }

    private String getQueryForBirthdayIni(String query, Object birthdayIni) {
        return new StringBuilder(query)
                .append("\"fq\":\"birthday:[")
                .append(birthdayIni.toString())
                .append("T00:00:00Z")
                .toString();
    }

    private String getQueryForBirthdayEnd(String query, Object birthdayEnd) {
        return new StringBuilder(query)
                .append(" TO ")
                .append(birthdayEnd.toString())
                .append("T00:00:00Z]\"")
                .toString();
    }

    private String getQueryOrderBy(String query, Integer start, String fieldOrder) {
        return new StringBuilder("{\"q\":\"*:*\", \"start\":\"")
                .append(start)
                .append("\",")
                .append(query)
                .append(", \"sort\":\"")
                .append(fieldOrder)
                .append(" asc\"}")
                .toString();
    }

    private String getQueryOrderByAndAllFields(Integer start, String fieldOrder) {
        return new StringBuilder("{\"q\":\"*:*\", \"start\":\"")
                .append(start)
                .append("\", \"sort\":\"")
                .append(fieldOrder)
                .append(" asc\"}")
                .toString();
    }

    private String getQuery(String query, Integer start) {
        return new StringBuilder("{\"q\":\"*:*\", \"start\":\"")
                .append(start)
                .append("\",")
                .append(query)
                .append("}")
                .toString();
    }

    private String getQueryForAllFields(Integer start) {
        return new StringBuilder("{\"q\":\"*:*\", \"start\":\"")
                .append(start)
                .append("\"}")
                .toString();
    }
}