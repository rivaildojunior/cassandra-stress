package br.com.freitas.dse.stress.controller;

import br.com.freitas.dse.stress.domain.model.User;
import br.com.freitas.dse.stress.domain.model.UserPageable;
import br.com.freitas.dse.stress.domain.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "/count")
    public Long getCount() {
        return this.userService.getCount();
    }

    @GetMapping(value = "/filters")
    public UserPageable getUsers(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "birthday_ini", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthdayIni,
            @RequestParam(value = "birthday_end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthdayEnd,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "asc", required = false) String asc,
            @RequestParam(value = "desc", required = false) String desc,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        Map<String, Object> filters = this.getMapFilters(id, name, gender, birthdayIni, birthdayEnd, city);

        return this.userService.getUsers(filters, asc, desc, page, size);
    }

    public Map<String, Object> getMapFilters(
            String id,
            String name,
            String gender,
            LocalDate birthdayIni,
            LocalDate birthdayFim,
            String city
    ) {
        Map<String, Object> filters = new LinkedHashMap<>();

        filters.put("id", id);
        filters.put("name", name);
        filters.put("gender", gender);
        filters.put("birthday_ini", birthdayIni);
        filters.put("birthday_end", birthdayFim);
        filters.put("city", city);

        return filters;
    }
}
