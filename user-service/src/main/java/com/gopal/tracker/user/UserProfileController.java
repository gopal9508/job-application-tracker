package com.gopal.tracker.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {
    private final UserProfileRepository repository;

    public UserProfileController(UserProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UserProfile> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public UserProfile getById(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfile create(@RequestBody UserProfile user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "valid email is required");
        }
        user.setName(user.getName().trim());
        return repository.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        repository.deleteById(id);
    }
}
