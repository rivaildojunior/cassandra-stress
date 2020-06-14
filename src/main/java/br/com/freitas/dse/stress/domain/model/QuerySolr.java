package br.com.freitas.dse.stress.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class QuerySolr {
    private String q;
    private List<String> fq;
    private String sort;
    private Integer start;

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final String q;
        private final List<String> fq;
        private String sort;
        private Integer start;

        private final Integer DEFAULT_SIZE = 10;
        private final Integer DEFAULT_PAGE = 0;

        public Builder() {
            this.q = "*:*";
            this.start = 0;
            this.fq = new ArrayList<>();
        }

        public Builder withRangeDate(String key, Object dateIni, Object dateEnd) {
            StringBuilder sb = new StringBuilder("\"")
                    .append(key)
                    .append(":[")
                    .append(dateIni)
                    .append("T00:00:00Z TO ")
                    .append(dateEnd)
                    .append("T00:00:00Z]");

            this.fq.add(sb.toString());
            return this;
        }

        public Builder withFilter(Object key, Object value) {
            this.fq.add(key + ":" + QuerySolr.getStrWithScape(value));
            return this;
        }

        public Builder withSort(Object nameColumn, Object sorting) {
            this.sort = nameColumn + " " + sorting;
            return this;
        }

        public Builder withStart(Integer page, Integer size) {
            if (page == null) {
                page = DEFAULT_PAGE;
            }

            if (size == null) {
                size = DEFAULT_SIZE;
            }

            this.start = page * size;
            return this;
        }

        public QuerySolr build() {
            return new QuerySolr(
                    this.q,
                    this.fq,
                    this.sort,
                    this.start
            );
        }
    }

    //TODO Esse método foi criado pela fato das pesquisas com palavras compostas no solar conter '\\' entre as palavras.
    private static String getStrWithScape(Object obj) {
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
