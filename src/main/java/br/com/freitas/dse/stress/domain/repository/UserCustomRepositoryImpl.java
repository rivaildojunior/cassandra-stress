package br.com.freitas.dse.stress.domain.repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import br.com.freitas.dse.stress.domain.model.User;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

	private final CassandraOperations cqlTemplate;
	private String where = "";

	public UserCustomRepositoryImpl(CassandraOperations cqlTemplate) {
		this.cqlTemplate = cqlTemplate;
	}

	public List<User> getQuery(Map<String, Object> filtro, String order, Integer start, Integer size) {
		Select.Where select = QueryBuilder.select().from("tb_user").where();
		where = "";

		filtro.forEach((key, value) -> {
			if (value != null) {
				if (key.equals("birthday_ini")) {
					where = where + "\"fq\":\"birthday:[" +  value.toString() + "T00:00:00Z";
					// select.and(QueryBuilder.gte("birthday", this.convertToDate(value)));
					// return;
				} else

				if (key.equals("birthday_end")) {
					where = where + " TO " + value + "T00:00:00Z]\"";
					// return;
				} else {
					where = where + "\"fq\":\"" + key + ":" + value + "\"";
				}

				// select.and(QueryBuilder.eq(key, value));
			}
			// ""fq":"birthday:[2016-03-15T00:00:00Z TO 2017-02-01T00:00:00Z]"
		});

		if (order != null) {
			if (!where.equals("")) {
				select.and(QueryBuilder.eq("solr_query",
						"{\"q\":\"*:*\", \"start\":\"" + start + "\"," + where + ", \"sort\":\"" + order + " asc\"}"));
			} else {
				select.and(QueryBuilder.eq("solr_query",
						"{\"q\":\"*:*\", \"start\":\"" + start + "\", \"sort\":\"" + order + " asc\"}"));
			}
		} else {
			if (!where.equals("")) {
				select.and(QueryBuilder.eq("solr_query", "{\"q\":\"*:*\", \"start\":\"" + start + "\"," + where + "}"));
			} else {
				select.and(QueryBuilder.eq("solr_query", "{\"q\":\"*:*\", \"start\":\"" + start + "\"}"));
			}
		}
		select.limit(size);
		return this.cqlTemplate.select(select, User.class);
	}

	private Date convertToDate(Object Object) {
		LocalDate date = (LocalDate) Object;

		return Date.from(date.atStartOfDay().atZone(ZoneId.of("GMT")).toInstant());
	}
}