package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.RegisterDto;
import com.unibuc.boardmania.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.unibuc.boardmania.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return successResponse();
    }
}
