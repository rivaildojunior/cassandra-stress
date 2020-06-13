package br.com.freitas.dse.stress.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Page {
    private Integer size;
    private Integer previous;
    private Integer current;
    private Integer next;
    private Integer numberOfPages;
}
