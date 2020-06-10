package br.com.freitas.dse.stress.domain.repository;

import br.com.freitas.dse.stress.domain.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface UserRepository extends CassandraRepository<User, UUID> {
}
