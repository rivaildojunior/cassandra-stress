package br.com.freitas.dse.stress.domain.model;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.data.cassandra.core.CassandraOperations;

@Data
@AllArgsConstructor
public class Page {
    private Integer size;
    private Integer previous;
    private Integer current;
    private Integer next;
    private Integer numberOfPages;

    public static Page.Builder newBuilder(CassandraOperations cqlTemplate) {
        return new Page.Builder(cqlTemplate);
    }

    public static class Builder {
        private Integer size;
        private Integer previous;
        private Integer current;
        private Integer next;
        private Integer numberOfPages;
        private final CassandraOperations cqlTemplate;

        public Builder(CassandraOperations cqlTemplate) {
            this.cqlTemplate = cqlTemplate;
        }

        public Builder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public Builder withPrevious(Integer previous) {
            this.previous = previous;
            return this;
        }

        public Builder withCurrent(Integer current) {
            this.current = current;
            return this;
        }

        public Builder withNext(Integer next) {
            this.next = next;
            return this;
        }

        public Builder withNumberOfPages(QuerySolr filters, Integer size) {
            Integer rows = this.getRows(filters);

            this.numberOfPages = rows != null ? rows / size : 0;
            return this;
        }

        public Page build() {
            return new Page(
                    this.size,
                    this.previous,
                    this.current,
                    this.next,
                    this.numberOfPages
            );
        }

        /*TODO
            Implementado para saber a quantidade de registros retornados pela query sem LIMIT.
            Este método é necessário apenas se o usuário não estiver na página 0, visto que o client já possui
            a quantidade de páginas a serem exibidas para o usuário.
        */
        private Integer getRows(QuerySolr filters) {
            filters.setStart(0);

            Select.Where count = QueryBuilder.select().countAll().from("tb_user").where();

            count.and(QueryBuilder.eq("solr_query", new JSONObject(filters).toString()));

            return this.cqlTemplate.selectOne(count, Integer.class);
        }
    }
}
