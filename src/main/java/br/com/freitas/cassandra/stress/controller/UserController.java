package br.com.freitas.cassandra.stress.controller;

import br.com.freitas.cassandra.stress.domain.model.User;
import br.com.freitas.cassandra.stress.domain.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return this.userService.getUsers();
    }

    @GetMapping(value = "/filters")
    public List<User> getUsers(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "city", required = false) String city
    ) {
        Map<String, Object> map = this.getMapFilters(id, name, gender, birthday, city);

        return this.userService.getUsers(map);
    }

    public Map<String, Object> getMapFilters(
            UUID id,
            String name,
            String gender,
            String birthday,
            String city
    ) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> filters = new HashMap<>();

        filters.put("id", id);
        filters.put("name", name);
        filters.put("house", gender);
        filters.put("school", birthday);
        filters.put("species", city);

        map.put("filters", filters);

        return map;
    }
}
