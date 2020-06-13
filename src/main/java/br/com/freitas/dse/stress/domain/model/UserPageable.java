package br.com.freitas.dse.stress.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UserPageable {
    private List<User> users;
    private Page page;
}
