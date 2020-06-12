package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.User;

import java.util.List;
import java.util.Map;


public interface UserCustomRepository {
    List<User> getQuery(Map<String, Object> filtro, String order, Integer start, Integer size);
}
