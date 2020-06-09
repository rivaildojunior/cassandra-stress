package br.com.freitas.cassandra.stress.domain.repository;

import br.com.freitas.cassandra.stress.domain.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
}
