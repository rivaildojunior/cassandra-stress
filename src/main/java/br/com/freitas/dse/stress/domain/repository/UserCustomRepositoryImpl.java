package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.User;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.json.JSONObject;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final CassandraOperations cqlTemplate;

    public UserCustomRepositoryImpl(CassandraOperations cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    public List<User> findUserByFilters(Map<String, Object> map, String order, Integer page, Integer size) {
        Select.Where select = QueryBuilder.select().from("tb_user").where();

        Map<String, Object> filters = this.getMapForSolrQuery(map);

        if (size != null) {
            select.limit(size);
        }

        if (order != null) {
            filters.put("sort", order + " asc");
        }

        filters.put("start", this.getPage(page, size));

        JSONObject json = new JSONObject(filters);

        select.and(QueryBuilder.eq("solr_query", json.toString()));

        return this.cqlTemplate.select(select, User.class);
    }

    private Integer getPage(Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 10;
        }

        return page * size;
    }

    private Map<String, Object> getMapForSolrQuery(Map<String, Object> filters) {
        Map<String, Object> map = new HashMap<>();
        List<String> fqs = new ArrayList<>();

        map.put("q", "*:*");
        map.put("fq", fqs);

        if (filters.get("id") != null) {
            map.put("fq", "id:" + filters.get("id"));
            return map;
        }

        if (filters.get("name") != null) {
            fqs.add("name:" + this.getStrWithScape(filters.get("name")));
        }

        if (filters.get("gender") != null) {
            fqs.add("gender:" + filters.get("gender"));
        }

        if (filters.get("birthday_ini") != null && filters.get("birthday_end") != null) {
            StringBuilder sb = new StringBuilder("birthday:[")
                    .append(filters.get("birthday_ini")).append("T00:00:00Z TO ")
                    .append(filters.get("birthday_end"))
                    .append("T00:00:00Z]");

            fqs.add(sb.toString());
        }

        if (filters.get("city") != null) {
            fqs.add("city:" + this.getStrWithScape(filters.get("city")));
        }

        return map;
    }

    private String getStrWithScape(Object obj) {
        String str = String.valueOf(obj);
        String[] words = str.split(" ");

        if (words.length > 1) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                if (i != words.length - 1) {
                    sb.append(words[i]).append("\\ ");
                    continue;
                }

                sb.append(words[i]);
            }

            return sb.toString();
        }

        return str;
    }
}