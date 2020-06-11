package br.com.freitas.cassandra.stress.domain.repository;

import java.util.List;
import java.util.Map;

import br.com.freitas.cassandra.stress.domain.model.User;

public interface UserCustomRepository {
	List<User> getQuery(Map<String, Object> filtro);
}
