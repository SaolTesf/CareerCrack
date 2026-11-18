package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private CurrentUserService service;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new CurrentUserService(userRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(Object principal) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    void findsTheUserByTheUsernameInTheToken() {
        User alice = user(7L, "alice");
        when(userRepository.getUserByUsername("alice")).thenReturn(alice);
        loginAs("alice");

        assertThat(service.getCurrentUser()).isSameAs(alice);
        assertThat(service.getCurrentUserId()).isEqualTo(7L);
    }

    @Test
    void usesTheUsernameWhenThePrincipalIsTheUserEntity() {
        User alice = user(7L, "alice");
        when(userRepository.getUserByUsername("alice")).thenReturn(alice);
        loginAs(user(null, "alice"));

        assertThat(service.getCurrentUserId()).isEqualTo(7L);
    }

    @Test
    void refusesWhenNobodyIsLoggedIn() {
        assertThatThrownBy(() -> service.getCurrentUser())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void refusesAnAnonymousVisitor() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertThatThrownBy(() -> service.getCurrentUser())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void refusesATokenForAnAccountThatNoLongerExists() {
        when(userRepository.getUserByUsername("ghost")).thenReturn(null);
        loginAs("ghost");

        assertThatThrownBy(() -> service.getCurrentUser())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
