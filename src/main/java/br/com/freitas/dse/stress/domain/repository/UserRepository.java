package br.com.freitas.cassandra.stress.domain.repository;

import br.com.freitas.cassandra.stress.domain.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface UserRepository extends CassandraRepository<User, UUID>, UserCustomRepository {
}
