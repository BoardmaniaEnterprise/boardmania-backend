package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.RegisterDto;
import com.unibuc.boardmania.dto.ReviewDto;
import com.unibuc.boardmania.service.UserService;
import com.unibuc.boardmania.utils.KeycloakHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import static com.unibuc.boardmania.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    UserService userService = UserService.getInstance();

    @GetMapping()
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return successResponse();
    }
    @PostMapping("/submitReview/{eventId}/{userId}")
    public ResponseEntity<?> submitReview(@PathVariable Long eventId, @PathVariable Long userId, @RequestBody ReviewDto reviewDto, Authentication authentication) {
        return new ResponseEntity<>(userService.submitReview(reviewDto, eventId, userId, KeycloakHelper.getUserId(authentication)),
                HttpStatus.OK);
    }
}
