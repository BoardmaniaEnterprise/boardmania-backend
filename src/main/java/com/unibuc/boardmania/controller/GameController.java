package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.NewGameDto;
import com.unibuc.boardmania.dto.UpdateGameDto;
import com.unibuc.boardmania.service.GameService;
import com.unibuc.boardmania.utils.KeycloakHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.unibuc.boardmania.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("games")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class GameController {

    @Autowired
    private final GameService gameService;

    @PostMapping
    public ResponseEntity<?> addGame(@RequestBody NewGameDto newGameDto) {
        return new ResponseEntity<>(gameService.addGame(newGameDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllGames() {
        return new ResponseEntity<>(gameService.getAllGames(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGame(@PathVariable Long id) {
        return new ResponseEntity<>(gameService.getGame(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody UpdateGameDto updateGameDto, Authentication authentication) {
        gameService.updateGame(id, updateGameDto);
        return successResponse();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id, Authentication authentication) {
        gameService.deleteGame(id);
        return successResponse();
    }
}
