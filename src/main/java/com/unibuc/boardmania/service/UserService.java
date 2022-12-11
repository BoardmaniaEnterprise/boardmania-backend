package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.RegisterDto;
import com.unibuc.boardmania.dto.UserDto;
import com.unibuc.boardmania.model.User;
import com.unibuc.boardmania.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> UserDto.builder()
                .username(user.getUsername())
                .build())
                .collect(Collectors.toList());
    }

    public void registerUser(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with email %s already exists!", registerDto.getEmail()));
        }

        User newUser = User.builder()
                .username(registerDto.getUsername())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .deleted(false)
                .build();

        newUser = userRepository.save(newUser);
        keycloakAdminService.registerUser(newUser, registerDto.getPassword(), "USER");
    }




}
