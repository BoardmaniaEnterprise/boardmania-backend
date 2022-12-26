package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.RegisterDto;
import com.unibuc.boardmania.dto.ReviewDto;
import com.unibuc.boardmania.dto.UserDto;
import com.unibuc.boardmania.model.*;
import com.unibuc.boardmania.repository.EventRepository;
import com.unibuc.boardmania.repository.ReviewRepository;
import com.unibuc.boardmania.repository.UserEventRepository;
import com.unibuc.boardmania.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReviewRepository reviewRepository;
    private final UserEventRepository userEventRepository;
    private final KeycloakAdminService keycloakAdminService;

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> UserDto.builder()
                .username(user.getUsername())
                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void registerUser(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with email %s already exists!", registerDto.getEmail()));
        }

        User newUser = User.builder()
                .username(registerDto.getUsername())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .trustScore(100)
                .deleted(false)
                .build();

        newUser = userRepository.save(newUser);
        keycloakAdminService.registerUser(newUser, registerDto.getPassword(), "ROLE_USER");
    }

    @Transactional
    public Long submitReview(ReviewDto reviewDto, Long eventId, Long reviewerId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found!"));
        if (event.getEventDateTimestamp() > DateTime.now().getMillis()) {
            throw new BadRequestException("Cannot send review before the event begins!");
        }
        if (userEventRepository.findByEventIdAndUserId(eventId, reviewerId).isEmpty() ||
                userEventRepository.findByEventIdAndUserId(eventId, userId).isEmpty()) {
            throw new BadRequestException("Users did not take part in the same event!");
        }
        Review review = Review.builder()
                .event(event)
                .comment(reviewDto.getComment())
                .reviewedUser(userRepository.getById(userId))
                .reviewer(userRepository.getById(reviewerId))
                .honor(reviewDto.getHonor())
                .deleted(false).build();

        return reviewRepository.save(review).getId();
    }
}
