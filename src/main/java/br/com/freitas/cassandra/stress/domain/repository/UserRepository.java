package br.com.freitas.cassandra.stress.domain.repository;

import br.com.freitas.cassandra.stress.domain.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
