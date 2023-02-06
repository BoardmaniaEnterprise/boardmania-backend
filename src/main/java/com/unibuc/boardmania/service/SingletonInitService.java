package com.unibuc.boardmania.service;

import com.unibuc.boardmania.repository.EventRepository;
import com.unibuc.boardmania.repository.ReviewRepository;
import com.unibuc.boardmania.repository.UserEventRepository;
import com.unibuc.boardmania.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class SingletonInitService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReviewRepository reviewRepository;
    private final UserEventRepository userEventRepository;
    private final KeycloakAdminService keycloakAdminService;

    @PostConstruct
    public void initSingletonService() {
        UserService userService = UserService.getInstance();
        userService.setUserRepository(userRepository);
        userService.setEventRepository(eventRepository);
        userService.setReviewRepository(reviewRepository);
        userService.setUserEventRepository(userEventRepository);
        userService.setKeycloakAdminService(keycloakAdminService);
    }
}
