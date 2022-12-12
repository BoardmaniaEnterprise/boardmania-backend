package com.unibuc.boardmania.service;

import com.unibuc.boardmania.config.AuthClient;
import com.unibuc.boardmania.dto.LoginDto;
import com.unibuc.boardmania.dto.RefreshTokenDto;
import com.unibuc.boardmania.dto.TokenDto;
import com.unibuc.boardmania.model.User;
import com.unibuc.boardmania.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springsecurity.KeycloakAuthenticationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthClient authClient;

    @Mock
    private UserRepository userRepository;

    private User user;

    private LoginDto loginDto;

    private TokenDto tokenDto;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .username("test")
                .email("test")
                .build();

        loginDto = LoginDto.builder()
                .email(user.getEmail())
                .password("test")
                .grantType("test")
                .build();

        tokenDto = TokenDto.builder()
                .accessToken("test")
                .build();
    }

    @Test
    @DisplayName("Login, expected success")
    public void login() {
        //when
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.ofNullable(user));
        when(authClient.login(any())).thenReturn(tokenDto);

        //then
        TokenDto response = authService.login(loginDto);

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(authClient).login(any());
        Assertions.assertEquals(tokenDto, response);

    }
    //login unauthorized
    @Test
    @DisplayName("Login, expected ResponseStatusException(NotFound)")
    public void loginUserNotFound() {
        //when
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!"));

        //then
        //then
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            authService.login(loginDto);
        });

        verify(userRepository).findByEmail(loginDto.getEmail());
        assertEquals("The user doesn't exist!", thrown.getReason());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    @DisplayName("Login, expected ResponseStatusException(Unauthorized)")
    public void loginWrongCredentials() {
        //having
        String expected = "Email or password incorrect!";
        //when
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.ofNullable(user));
        when(authClient.login(any())).thenThrow(new KeycloakAuthenticationException(""));

        //then
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            authService.login(loginDto);
        });

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(authClient).login(any());
        Assertions.assertEquals(expected, thrown.getReason());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
    }
    //refresh?
    @Test
    @DisplayName("Refresh, expected success")
    public void refresh() {
        //having
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("test", "test");
        TokenDto expected = new TokenDto();

        //when
        when(authClient.refresh(any())).thenReturn(expected);

        //then
        TokenDto response = authService.refresh(refreshTokenDto);

        verify(authClient).refresh(any());
        Assertions.assertEquals(expected, response);
    }
}
