package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.Page;
import br.com.freitas.dse.stress.domain.model.User;
import br.com.freitas.dse.stress.domain.model.UserPageable;
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
	private static final String URL_BASE = "/users/filters?page=";
	private static final String SIZE = "&size=";
	
	private final CassandraOperations cqlTemplate;
    private String filtro;

    public UserCustomRepositoryImpl(CassandraOperations cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    public UserPageable findUserByFilters(Map<String, Object> map, String asc, String desc, Integer page, Integer size) {
        Select.Where select = QueryBuilder.select().from("tb_user").where();
        Select.Where count = QueryBuilder.select().countAll().from("tb_user").where();

        Map<String, Object> filters = this.getMapForSolrQuery(map);

        if (desc != null) {
            filters.put("sort", desc + " desc");
        }

        if (asc != null) {
            filters.put("sort", asc + " asc");
        }

        count.and(QueryBuilder.eq("solr_query", new JSONObject(filters).toString()));

        filters.put("start", this.getStart(page, size));

        if (size != null) {
            select.limit(size);
        }

        select.and(QueryBuilder.eq("solr_query", new JSONObject(filters).toString()));

        List<User> users = this.cqlTemplate.select(select, User.class);

        return UserPageable.builder()
                .page(this.getPage(users, page, size, count, map))
                .users(users)
                .build();
    }

    private Integer getStart(Integer page, Integer size) {
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
                    .append(filters.get("birthday_ini"))
                    .append("T00:00:00Z TO ")
                    .append(filters.get("birthday_end"))
                    .append("T00:00:00Z]");

            fqs.add(sb.toString());
        }

        if (filters.get("city") != null) {
            fqs.add("city:" + this.getStrWithScape(filters.get("city")));
        }

        return map;
    }

    private Page getPage(List<User> users, Integer page, Integer size, Select.Where count, Map<String, Object> map) {
    	this.filtro = "";
        Integer rows = this.cqlTemplate.selectOne(count, Integer.class);
        int numberOfPages = 0;

        if (rows != null) {
            numberOfPages = rows / size;
        }

        int previous = page == 0 ? 0 : page - 1;
        Integer next = users.size() < size ? null : page + 1;
       
        map.forEach((chave, valor) -> {
        	if (valor != null) {
    			this.filtro = this.filtro.equals("") ? this.filtro + chave +"="+ valor : this.filtro + "&" + chave +"="+ valor;
        	}
        });

        return Page.builder()
                .previous(URL_BASE + previous + SIZE + size + "&" + this.filtro)
                .current(URL_BASE + page + SIZE + size + "&" + this.filtro)
                .next(URL_BASE + next + SIZE + size + "&" + this.filtro)
                .size(users.size())
                .numberOfPages(numberOfPages)
                .build();
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