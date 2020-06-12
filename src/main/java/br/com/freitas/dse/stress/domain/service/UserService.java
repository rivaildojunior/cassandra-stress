package br.com.freitas.dse.stress.domain.service;

import br.com.freitas.dse.stress.domain.model.User;
import br.com.freitas.dse.stress.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public Long getCount() {
        return this.userRepository.count();
    }

    public List<User> getUsers(Map<String, Object> map, String order) {
        return this.userRepository.getQuery(map, order);
    }
}
