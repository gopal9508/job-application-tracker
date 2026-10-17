
package com.gopal.tracker.user;

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
class UserProfileControllerTest {

    @Mock
    private UserProfileRepository repository;

    @InjectMocks
    private UserProfileController controller;

    private UserProfile createUser() {
        UserProfile user = new UserProfile();
        user.setName("Gopal Kumar");
        user.setEmail("gopal@example.com");
        return user;
    }

    @Test
    void getAllReturnsUsers() {
        List<UserProfile> users = List.of(createUser());
        when(repository.findAll()).thenReturn(users);

        assertEquals(users, controller.getAll());
        verify(repository).findAll();
    }

    @Test
    void getByIdReturnsExistingUser() {
        UserProfile user = createUser();
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertSame(user, controller.getById(1L));
    }

    @Test
    void getByIdThrows404WhenUserMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createSavesUserWithTrimmedName() {
        UserProfile user = createUser();
        user.setName("  Gopal Kumar  ");
        when(repository.save(user)).thenReturn(user);

        UserProfile result = controller.create(user);

        assertEquals("Gopal Kumar", result.getName());
        assertEquals("gopal@example.com", result.getEmail());
        verify(repository).save(user);
    }

    @Test
    void createRejectsBlankName() {
        UserProfile user = createUser();
        user.setName("   ");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any(UserProfile.class));
    }

    @Test
    void createRejectsInvalidEmail() {
        UserProfile user = createUser();
        user.setEmail("invalid-email");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any(UserProfile.class));
    }

    @Test
    void createRejectsNullEmail() {
        UserProfile user = createUser();
        user.setEmail(null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(repository, never()).save(any(UserProfile.class));
    }

    @Test
    void deleteRemovesExistingUser() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> controller.delete(1L));

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteThrows404WhenUserMissing() {
        when(repository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.delete(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).deleteById(anyLong());
    }
}
