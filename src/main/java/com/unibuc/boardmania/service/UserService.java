package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.UserDto;
import com.unibuc.boardmania.model.User;
import com.unibuc.boardmania.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> UserDto.builder()
                .username(user.getUsername())
                .build())
                .collect(Collectors.toList());
    }


}
