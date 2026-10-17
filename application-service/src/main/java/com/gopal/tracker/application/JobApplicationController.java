package com.gopal.tracker.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {
    private final JobApplicationRepository repository;

    public JobApplicationController(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<JobApplication> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public JobApplication getById(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplication create(@RequestBody JobApplication application) {
        application.setCompanyName(requireText(application.getCompanyName(), "companyName"));
        application.setJobTitle(requireText(application.getJobTitle(), "jobTitle"));
        return repository.save(application);
    }

    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @RequestBody JobApplication input) {
        JobApplication existing = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        existing.setCompanyName(requireText(input.getCompanyName(), "companyName"));
        existing.setJobTitle(requireText(input.getJobTitle(), "jobTitle"));
        existing.setJobUrl(input.getJobUrl());
        existing.setStatus(input.getStatus());
        existing.setNotes(input.getNotes());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        repository.deleteById(id);
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        return value.trim();
    }
}
