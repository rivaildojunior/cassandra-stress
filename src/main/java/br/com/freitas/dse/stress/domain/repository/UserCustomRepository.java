package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.UserPageable;

import java.util.Map;


public interface UserCustomRepository {
    UserPageable findUserByFilters(Map<String, Object> filters, String asc, String desc, Integer page, Integer size);
}
