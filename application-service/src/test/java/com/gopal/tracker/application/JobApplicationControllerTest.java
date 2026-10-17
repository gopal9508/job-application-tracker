
package com.gopal.tracker.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationControllerTest {

    @Mock
    private JobApplicationRepository repository;

    @InjectMocks
    private JobApplicationController controller;

    private JobApplication createApplication() {
        JobApplication application = new JobApplication();
        application.setCompanyName("Infosys");
        application.setJobTitle("Java Developer");
        application.setStatus("APPLIED");
        application.setNotes("Initial application");
        return application;
    }

    @Test
    void getAllReturnsApplications() {
        List<JobApplication> applications =
                List.of(createApplication());

        when(repository.findAll()).thenReturn(applications);

        assertEquals(applications, controller.getAll());
        verify(repository).findAll();
    }

    @Test
    void getByIdReturnsExistingApplication() {
        JobApplication application = createApplication();

        when(repository.findById(1L))
                .thenReturn(Optional.of(application));

        assertSame(application, controller.getById(1L));
    }

    @Test
    void getByIdThrows404WhenMissing() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createSavesTrimmedApplicationDetails() {
        JobApplication application = createApplication();
        application.setCompanyName("  Infosys  ");
        application.setJobTitle("  Java Developer  ");

        when(repository.save(application)).thenReturn(application);

        JobApplication result = controller.create(application);

        assertEquals("Infosys", result.getCompanyName());
        assertEquals("Java Developer", result.getJobTitle());
        verify(repository).save(application);
    }

    @Test
    void createRejectsBlankCompanyName() {
        JobApplication application = createApplication();
        application.setCompanyName("   ");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(application)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void createRejectsMissingJobTitle() {
        JobApplication application = createApplication();
        application.setJobTitle(null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(application)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void updateChangesExistingApplication() {
        JobApplication existing = createApplication();

        JobApplication input = new JobApplication();
        input.setCompanyName("TCS");
        input.setJobTitle("Software Engineer");
        input.setStatus("INTERVIEW");
        input.setNotes("Interview scheduled");
        input.setJobUrl("https://example.com/job");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        JobApplication result = controller.update(1L, input);

        assertEquals("TCS", result.getCompanyName());
        assertEquals("Software Engineer", result.getJobTitle());
        assertEquals("INTERVIEW", result.getStatus());
        assertEquals("Interview scheduled", result.getNotes());

        verify(repository).save(existing);
    }

    @Test
    void deleteRemovesExistingApplication() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> controller.delete(1L));

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteThrows404WhenMissing() {
        when(repository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.delete(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).deleteById(anyLong());
    }
}
