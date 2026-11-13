package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * The one place that answers "who is making this request?" The answer always comes from the login token,
 * never from a request body or URL, so a client cannot act as someone else by sending a different user id.
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** The logged-in user. Call it inside a @Transactional method if you will use the entity afterwards. */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }
        // the token's subject is the username
        String username = authentication.getPrincipal() instanceof User principalUser
                ? principalUser.getUsername()
                : authentication.getName();
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            // a valid token for an account that has since been deleted
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }
        return user;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
