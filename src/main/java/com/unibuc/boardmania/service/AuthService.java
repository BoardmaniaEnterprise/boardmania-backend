package com.unibuc.boardmania.service;

import com.unibuc.boardmania.config.AuthClient;
import com.unibuc.boardmania.dto.LoginDto;
import com.unibuc.boardmania.dto.RefreshTokenDto;
import com.unibuc.boardmania.dto.TokenDto;
import com.unibuc.boardmania.model.User;
import com.unibuc.boardmania.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthClient authClient;
    private final UserRepository userRepository;

    @Value("${keycloak.resource}")
    private String clientId;

    @SneakyThrows
    public TokenDto login(LoginDto loginDto) {
        User inAppUser = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!"));

        // Set the request body
        MultiValueMap<String, String> loginCredentials = new LinkedMultiValueMap<>();
        loginCredentials.add("client_id", clientId);
        loginCredentials.add("username", inAppUser.getId().toString());
        loginCredentials.add("password", loginDto.getPassword());
        loginCredentials.add("grant_type", loginDto.getGrantType());
        // Keycloak login (will return an Access Token)
        try{
            TokenDto token = authClient.login(loginCredentials);
            return token;
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password incorrect!");
        }
    }

    @SneakyThrows
    public TokenDto refresh(RefreshTokenDto refreshTokenDto) {

        // Set the request body
        MultiValueMap<String, String> refreshCredentials = new LinkedMultiValueMap<>();
        refreshCredentials.add("client_id", clientId);
        refreshCredentials.add("refresh_token", refreshTokenDto.getRefresh_token());
        refreshCredentials.add("grant_type", refreshTokenDto.getGrantType());

        return authClient.refresh(refreshCredentials);
    }

}
