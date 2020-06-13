package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.User;

import java.util.List;
import java.util.Map;


public interface UserCustomRepository {
    List<User> findUserByFilters(Map<String, Object> filters, String asc, String desc, Integer page, Integer size);
}
