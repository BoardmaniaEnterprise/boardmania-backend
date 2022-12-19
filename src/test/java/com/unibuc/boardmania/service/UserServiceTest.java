package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.RegisterDto;
import com.unibuc.boardmania.dto.UserDto;
import com.unibuc.boardmania.model.User;
import com.unibuc.boardmania.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakAdminService keycloakAdminService;

    @Test
    @DisplayName("Get users, expected success")
    public void getUsers() {
        //having
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        List<UserDto> expected = users.stream().map(user -> UserDto.builder()
                .username(user.getUsername())
                .build())
                .collect(Collectors.toList());

        //when
        when(userRepository.findAll()).thenReturn(users);

        //then
        List<UserDto> response = userService.getUsers();

        Assertions.assertEquals(expected, response);
    }

    @Test
    @DisplayName("Register user, expected success")
    public void registerUser() {
        //having
        RegisterDto registerDto = RegisterDto.builder()
                .email("test")
                .password("test")
                .build();

        User user = User.builder().id(1L).build();

        //when
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        //then
        userService.registerUser(registerDto);

        verify(userRepository).existsByEmail(registerDto.getEmail());
        verify(keycloakAdminService).registerUser(any(User.class), any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Register user, expected ResponseStatusException")
    public void registerUserAlreadyExists() {
        //having
        RegisterDto registerDto = RegisterDto.builder()
                .email("test")
                .build();

        String expected = String.format("User with email %s already exists!", registerDto.getEmail());

        //when
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        //then
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            userService.registerUser(registerDto);
        });

        verify(userRepository).existsByEmail(registerDto.getEmail());
        assertEquals(expected, thrown.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }
}
