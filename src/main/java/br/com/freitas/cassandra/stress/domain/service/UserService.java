package br.com.freitas.cassandra.stress.domain.service;

import br.com.freitas.cassandra.stress.domain.model.User;
import br.com.freitas.cassandra.stress.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public List<User> getUsers(Map<String, Object> map) {
        //TODO implementar filtros
        Iterable<User> users = this.userRepository.findAll();

        return this.getListFromIterator(users);
    }

    private List<User> getListFromIterator(Iterable<User> iterable) {
        List<User> list = new ArrayList<>();

        iterable.forEach(list::add);

        return list;
    }
}
