package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.NewGameDto;
import com.unibuc.boardmania.dto.UpdateGameDto;
import com.unibuc.boardmania.model.Game;
import com.unibuc.boardmania.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    // Entities
    private Game game;
    // Dtos
    private NewGameDto newGameDto;
    private UpdateGameDto updateGameDto;

    @BeforeEach
    public void setup() {
        game = Game.builder()
                .id(1L)
                .build();

        newGameDto = NewGameDto.builder()
                .name("test")
                .build();

        updateGameDto = UpdateGameDto.builder()
                .name("test")
                .description("test")
                .maxNumberOfPlayers(10)
                .minNumberOfPlayers(2)
                .deleted(false)
                .url("test")
                .build();

    }

    @Test
    @DisplayName("Add game, expected success")
    public void addGame() {
        //when
        when(gameRepository.findByName(newGameDto.getName())).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        //then
        Long response = gameService.addGame(newGameDto, 1L);
        verify(gameRepository).findByName(newGameDto.getName());
        verify(gameRepository).save(any(Game.class));
        assertEquals(game.getId(), response);
    }

    @Test
    @DisplayName("Add game, expected BadRequestException(Game exists)")
    public void addGameAlreadyExists() {
        //when
        when(gameRepository.findByName(newGameDto.getName())).thenReturn(Optional.ofNullable(game));

        //then
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            gameService.addGame(newGameDto, 1L);
        });

        verify(gameRepository).findByName(newGameDto.getName());
        assertEquals("Game already exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Get all games, expected success")
    public void getGames() {
        //having
        List<Game> gameList = new ArrayList<>();
        gameList.add(game);

        //when
        when(gameRepository.findAllByDeletedFalse()).thenReturn(gameList);

        //then
        List<Game> response = gameService.getAllGames();

        verify(gameRepository).findAllByDeletedFalse();
        assertEquals(gameList, response);
    }

    @Test
    @DisplayName("Delete game, expected success")
    public void deleteGame() {
        //when
        when(gameRepository.findById(1L)).thenReturn(Optional.ofNullable(game));


        //then
        gameService.deleteGame(1L);

        verify(gameRepository).findById(1L);
    }

    @Test
    @DisplayName("Delete game, expected NotFoundException")
    public void deleteGameNotFound() {
        //when
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            gameService.deleteGame( 1L);
        });
        assertEquals("Game not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Get game, expected success")
    public void getGame() {
        //when
        when(gameRepository.findById(game.getId())).thenReturn(Optional.ofNullable(game));

        //then
        Game response = gameService.getGame(game.getId());
        assertEquals(game, response);
    }

    @Test
    @DisplayName("Get game, expected not found")
    public void getGameNotFound() {
        //when
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            gameService.getGame( 1L);
        });
        assertEquals("Game not found", thrown.getMessage());
    }

    @Test
    @DisplayName("Update game, expected success")
    public void updateGame() {
        //when
        when(gameRepository.findById(game.getId())).thenReturn(Optional.ofNullable(game));

        //then
        gameService.updateGame(game.getId(), updateGameDto);

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("Update game, expected NotFoundException")
    public void updateGameNotFound() {
        //when
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        //then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            gameService.updateGame( game.getId(), updateGameDto);
        });
        assertEquals("Game not found", thrown.getMessage());
    }

}
