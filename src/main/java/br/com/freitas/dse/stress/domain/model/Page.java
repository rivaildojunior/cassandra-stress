package br.com.freitas.dse.stress.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Page {
    private Integer size;
    private String previous;
    private String current;
    private String next;
    private Integer numberOfPages;
}
